/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.Softy.Launcher2.Classes.ThemeTools;
import com.Softy.Launcher2.Classes.Tools;
import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.R;

/**
 * TextView that draws a bubble behind the text. We cannot use a LineBackgroundSpan
 * because we want to make the bubble taller than the text and TextView's clip is
 * too aggressive.
 */
public class BubbleTextView extends AppCompatTextView {

    private static SparseArray<Theme> sPreloaderThemes = new SparseArray<Theme>(2);

    private static final float SHADOW_LARGE_RADIUS = 4.0f;
    private static final float SHADOW_SMALL_RADIUS = 1.75f;
    private static final float SHADOW_Y_OFFSET = 2.0f;
    private static final int SHADOW_LARGE_COLOUR = 0xDD000000;
    private static final int SHADOW_SMALL_COLOUR = 0xCC000000;
    static final float PADDING_V = 3.0f;

    private HolographicOutlineHelper mOutlineHelper;
    private Bitmap mPressedBackground;

    private float mSlop;

    private int mTextColor;
    private final boolean mCustomShadowsEnabled;
    private boolean mIsTextVisible;

    // TODO: Remove custom background handling code, as no instance of BubbleTextView use any
    // background.
    private boolean mBackgroundSizeChanged;
    private final Drawable mBackground;

    private boolean mStayPressed;
    private boolean mIgnorePressedStateChange;
    private CheckLongPressHelper mLongPressHelper;

    public BubbleTextView(Context context) {
        this(context, null, 0);
        SharedPreferences mShared = (SharedPreferences) getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        int textColor = mShared.getInt(Data.DRAWER_TEXT_COLOR, 0);
        if(textColor >= -1){
            setTextColor(Color.WHITE);
        }else{
            setTextColor(textColor);
        }
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        SharedPreferences mShared = (SharedPreferences) getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        int textColor = mShared.getInt(Data.DRAWER_TEXT_COLOR, 0);
        if(textColor >= -1){
            setTextColor(Color.WHITE);
        }else{
            setTextColor(textColor);
        }
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        SharedPreferences mShared = (SharedPreferences) getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        int textColor = mShared.getInt(Data.DRAWER_TEXT_COLOR, 0);
        if(textColor >= -1){
            setTextColor(Color.WHITE);
        }else{
            setTextColor(textColor);
        }
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BubbleTextView, defStyle, 0);
        mCustomShadowsEnabled = a.getBoolean(R.styleable.BubbleTextView_customShadows, true);
        a.recycle();

        if (mCustomShadowsEnabled) {
            // Draw the background itself as the parent is drawn twice.
            mBackground = getBackground();
            setBackground(null);
        } else {
            mBackground = null;
        }
        init();
    }

    public void onFinishInflate() {
        super.onFinishInflate();

        // Ensure we are using the right text size
        LauncherAppState app = LauncherAppState.getInstance();
        DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();
        setTextSize(TypedValue.COMPLEX_UNIT_PX, grid.iconTextSizePx);
    }

    private void init() {
        mLongPressHelper = new CheckLongPressHelper(this);

        mOutlineHelper = HolographicOutlineHelper.obtain(getContext());
        if (mCustomShadowsEnabled) {
            setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, SHADOW_Y_OFFSET, SHADOW_LARGE_COLOUR);
        }
    }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache,
            boolean setDefaultPadding) {
        applyFromShortcutInfo(info, iconCache, setDefaultPadding, false);
    }

    private int numApplied = 0;
    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache,
            boolean setDefaultPadding, boolean promiseStateChanged) {
        boolean iconPack = getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getBoolean("ic-pack-applied", true);
        Bitmap b = null;
        if(iconPack == true)
            b = setIconPack(info, iconCache);
        else
            b = info.getIcon(iconCache);
        numApplied += 1;
        if(numApplied > 1)
        {
            getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().remove("theme").commit();
        }
        LauncherAppState app = LauncherAppState.getInstance();

        FastBitmapDrawable iconDrawable = Utilities.createIconDrawable(b);
        iconDrawable.setGhostModeEnabled(info.isDisabled != 0);

        setCompoundDrawables(null, iconDrawable, null, null);
        if (setDefaultPadding) {
            DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();
            setCompoundDrawablePadding(grid.iconDrawablePaddingPx);
        }
        if (info.contentDescription != null) {
            setContentDescription(info.contentDescription);
        }
        setText(info.title);
        setTag(info);

        if (promiseStateChanged || info.isPromise()) {
            applyState(promiseStateChanged);
        }
    }

    private int numAppsApplied = 0;
    public void applyFromApplicationInfo(AppInfo info) {
        boolean iconPack = getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getBoolean("ic-pack-applied", true);
        Bitmap b = null;
        if(iconPack == true)
            setIconPack(info);
        else
            Log.i("Comely", "Device does not have any icon packs selected.");

        LauncherAppState app = LauncherAppState.getInstance();
        DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();

        Drawable topDrawable = Utilities.createIconDrawable(info.iconBitmap);
        topDrawable.setBounds(0, 0, grid.allAppsIconSizePx, grid.allAppsIconSizePx);
        setCompoundDrawables(null, topDrawable, null, null);
        setCompoundDrawablePadding(grid.iconDrawablePaddingPx);
        setText(info.title);
        if (info.contentDescription != null) {
            setContentDescription(info.contentDescription);
        }
        setTag(info);
    }
    int length = 1;
    boolean setIconPack(AppInfo mInfo)
    {
        Bitmap b = mInfo.iconBitmap;
        //theming vars-----------------------------------------------
        final int ICONSIZE = Tools.numtodp(65,getContext());
        Resources themeRes = null;
        String resPacName = getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getString("theme", "");
        String usedPackName = getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getString("used-theme", "");
        if(resPacName.equals(usedPackName))
            return false;
        String iconResource = null;
        int intres=0;
        int intresiconback = 0;
        int intresiconfront = 0;
        int intresiconmask = 0;
        float scaleFactor = 1.0f;

        Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
        p.setAntiAlias(true);

        Paint origP = new Paint(Paint.FILTER_BITMAP_FLAG);
        origP.setAntiAlias(true);

        Paint maskp= new Paint(Paint.FILTER_BITMAP_FLAG);
        maskp.setAntiAlias(true);
        maskp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        if (resPacName.compareTo("")!=0){
            try{themeRes =getContext().getPackageManager().getResourcesForApplication(resPacName);}catch(Exception e){};
            if (themeRes!=null){
                String[] backAndMaskAndFront = ThemeTools.getIconBackAndMaskResourceName(themeRes, resPacName);
                if (backAndMaskAndFront[0]!=null)
                    intresiconback=themeRes.getIdentifier(backAndMaskAndFront[0],"drawable",resPacName);
                if (backAndMaskAndFront[1]!=null)
                    intresiconmask=themeRes.getIdentifier(backAndMaskAndFront[1],"drawable",resPacName);
                if (backAndMaskAndFront[2]!=null)
                    intresiconfront=   themeRes.getIdentifier(backAndMaskAndFront[2],"drawable",resPacName);
            }
        }

        BitmapFactory.Options uniformOptions = new BitmapFactory.Options();
        uniformOptions.inScaled=false;
        uniformOptions.inDither=false;
        uniformOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Canvas origCanv;
        Canvas canvas;
        scaleFactor= ThemeTools.getScaleFactor(themeRes, resPacName);
        Bitmap back=null;
        Bitmap mask=null;
        Bitmap front=null;
        Bitmap scaledBitmap = null;
        Bitmap scaledOrig = null;
        Bitmap orig = null;

        if (resPacName.compareTo("")!=0 && themeRes!=null){
            try{
                if (intresiconback!=0)
                    back =BitmapFactory.decodeResource(themeRes,intresiconback,uniformOptions);
            }catch(Exception e){}
            try{
                if (intresiconmask!=0)
                    mask = BitmapFactory.decodeResource(themeRes,intresiconmask,uniformOptions);
            }catch(Exception e){}
            try{
                if (intresiconfront!=0)
                    front = BitmapFactory.decodeResource(themeRes,intresiconfront,uniformOptions);
            }catch(Exception e){}
        }
        //theming vars-----------------------------------------------
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        for(int I=0;I<length;I++) {
            if (themeRes!=null){
                iconResource=null;
                intres=0;
                iconResource=ThemeTools.getResourceName(themeRes, resPacName, mInfo.componentName.toString());
                if (iconResource!=null){
                    intres = themeRes.getIdentifier(iconResource,"drawable",resPacName);
                }

                if (intres!=0){//has single drawable for app
                    mInfo.iconBitmap = BitmapFactory.decodeResource(themeRes,intres,uniformOptions);
                }else{
                    Drawable mIcon = new BitmapDrawable(getResources(), mInfo.iconBitmap);
                    orig=Bitmap.createBitmap(mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    mIcon.setBounds(0, 0, mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight());
                    mIcon.draw(new Canvas(orig));

                    scaledOrig =Bitmap.createBitmap(ICONSIZE, ICONSIZE, Bitmap.Config.ARGB_8888);
                    scaledBitmap = Bitmap.createBitmap(ICONSIZE, ICONSIZE, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(scaledBitmap);
                    if (back!=null){
                        canvas.drawBitmap(back, Tools.getResizedMatrix(back, ICONSIZE, ICONSIZE), p);
                    }

                    origCanv=new Canvas(scaledOrig);
                    orig=Tools.getResizedBitmap(orig, ((int)(ICONSIZE*scaleFactor)), ((int)(ICONSIZE*scaleFactor)));
                    origCanv.drawBitmap(orig, scaledOrig.getWidth() - (orig.getWidth() / 2) - (scaledOrig.getWidth() / 2),scaledOrig.getWidth()-(orig.getWidth()/2)-scaledOrig.getWidth()/2, origP);

                    if (mask!=null){
                        origCanv.drawBitmap(mask,Tools.getResizedMatrix(mask, ICONSIZE, ICONSIZE), maskp);
                    }

                    if (back!=null){
                        canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig,ICONSIZE,ICONSIZE), 0, 0,p);
                    }else
                        canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig,ICONSIZE,ICONSIZE), 0, 0,p);

                    if (front!=null)
                        canvas.drawBitmap(front,Tools.getResizedMatrix(front, ICONSIZE, ICONSIZE), p);

                    mInfo.iconBitmap = new BitmapDrawable(scaledBitmap).getBitmap();
                }
            }
        }


        front=null;
        back=null;
        mask=null;
        scaledOrig=null;
        orig=null;
        scaledBitmap=null;
        canvas=null;
        origCanv=null;
        p=null;
        maskp=null;
        resPacName=null;
        iconResource=null;
        intres=0;
        return true;
    }

    Bitmap setIconPack(ShortcutInfo mInfo, IconCache mCache)
    {
        Bitmap b = mInfo.getIcon(mCache);
        //theming vars-----------------------------------------------
        final int ICONSIZE = Tools.numtodp(65,getContext());
        Resources themeRes = null;
        String resPacName = getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getString("theme", "");
        String usedPackName = getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getString("used-theme", "");
        if(resPacName.equals(usedPackName))
            return null;

        String iconResource = null;
        int intres=0;
        int intresiconback = 0;
        int intresiconfront = 0;
        int intresiconmask = 0;
        float scaleFactor = 1.0f;

        Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
        p.setAntiAlias(true);

        Paint origP = new Paint(Paint.FILTER_BITMAP_FLAG);
        origP.setAntiAlias(true);

        Paint maskp= new Paint(Paint.FILTER_BITMAP_FLAG);
        maskp.setAntiAlias(true);
        maskp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        if (resPacName.compareTo("")!=0){
            try{themeRes =getContext().getPackageManager().getResourcesForApplication(resPacName);}catch(Exception e){};
            if (themeRes!=null){
                String[] backAndMaskAndFront = ThemeTools.getIconBackAndMaskResourceName(themeRes, resPacName);
                if (backAndMaskAndFront[0]!=null)
                    intresiconback=themeRes.getIdentifier(backAndMaskAndFront[0],"drawable",resPacName);
                if (backAndMaskAndFront[1]!=null)
                    intresiconmask=themeRes.getIdentifier(backAndMaskAndFront[1],"drawable",resPacName);
                if (backAndMaskAndFront[2]!=null)
                    intresiconfront=   themeRes.getIdentifier(backAndMaskAndFront[2],"drawable",resPacName);
            }
        }

        BitmapFactory.Options uniformOptions = new BitmapFactory.Options();
        uniformOptions.inScaled=false;
        uniformOptions.inDither=false;
        uniformOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Canvas origCanv;
        Canvas canvas;
        scaleFactor= ThemeTools.getScaleFactor(themeRes, resPacName);
        Bitmap back=null;
        Bitmap mask=null;
        Bitmap front=null;
        Bitmap scaledBitmap = null;
        Bitmap scaledOrig = null;
        Bitmap orig = null;

        if (resPacName.compareTo("")!=0 && themeRes!=null){
            try{
                if (intresiconback!=0)
                    back =BitmapFactory.decodeResource(themeRes,intresiconback,uniformOptions);
            }catch(Exception e){}
            try{
                if (intresiconmask!=0)
                    mask = BitmapFactory.decodeResource(themeRes,intresiconmask,uniformOptions);
            }catch(Exception e){}
            try{
                if (intresiconfront!=0)
                    front = BitmapFactory.decodeResource(themeRes,intresiconfront,uniformOptions);
            }catch(Exception e){}
        }
        //theming vars-----------------------------------------------
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        for(int I=0;I<length;I++) {
            if (themeRes!=null){
                iconResource=null;
                intres=0;
                iconResource=ThemeTools.getResourceName(themeRes, resPacName, mInfo.getTargetComponent().toString());
                if (iconResource!=null){
                    intres = themeRes.getIdentifier(iconResource,"drawable",resPacName);
                }

                if (intres!=0){//has single drawable for app
                    b = BitmapFactory.decodeResource(themeRes,intres,uniformOptions);
                }else{
                    Drawable mIcon = new BitmapDrawable(getResources(), b);
                    orig=Bitmap.createBitmap(mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    mIcon.setBounds(0, 0, mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight());
                    mIcon.draw(new Canvas(orig));

                    scaledOrig =Bitmap.createBitmap(ICONSIZE, ICONSIZE, Bitmap.Config.ARGB_8888);
                    scaledBitmap = Bitmap.createBitmap(ICONSIZE, ICONSIZE, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(scaledBitmap);
                    if (back!=null){
                        canvas.drawBitmap(back, Tools.getResizedMatrix(back, ICONSIZE, ICONSIZE), p);
                    }

                    origCanv=new Canvas(scaledOrig);
                    orig=Tools.getResizedBitmap(orig, ((int)(ICONSIZE*scaleFactor)), ((int)(ICONSIZE*scaleFactor)));
                    origCanv.drawBitmap(orig, scaledOrig.getWidth() - (orig.getWidth() / 2) - (scaledOrig.getWidth() / 2),scaledOrig.getWidth()-(orig.getWidth()/2)-scaledOrig.getWidth()/2, origP);

                    if (mask!=null){
                        origCanv.drawBitmap(mask,Tools.getResizedMatrix(mask, ICONSIZE, ICONSIZE), maskp);
                    }

                    if (back!=null){
                        canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig,ICONSIZE,ICONSIZE), 0, 0,p);
                    }else
                        canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig,ICONSIZE,ICONSIZE), 0, 0,p);

                    if (front!=null)
                        canvas.drawBitmap(front,Tools.getResizedMatrix(front, ICONSIZE, ICONSIZE), p);

                    b = new BitmapDrawable(scaledBitmap).getBitmap();
                }
            }
        }


        front=null;
        back=null;
        mask=null;
        scaledOrig=null;
        orig=null;
        scaledBitmap=null;
        canvas=null;
        origCanv=null;
        p=null;
        maskp=null;
        resPacName=null;
        iconResource=null;
        intres=0;
        return b;
    }

    @Override
    protected boolean setFrame(int left, int top, int right, int bottom) {
        if (getLeft() != left || getRight() != right || getTop() != top || getBottom() != bottom) {
            mBackgroundSizeChanged = true;
        }
        return super.setFrame(left, top, right, bottom);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mBackground || super.verifyDrawable(who);
    }

    @Override
    public void setTag(Object tag) {
        if (tag != null) {
            LauncherModel.checkItemInfo((ItemInfo) tag);
        }
        super.setTag(tag);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (!mIgnorePressedStateChange) {
            updateIconState();
        }
    }

    private void updateIconState() {
        Drawable top = getCompoundDrawables()[1];
        if (top instanceof FastBitmapDrawable) {
            ((FastBitmapDrawable) top).setPressed(isPressed() || mStayPressed);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Call the superclass onTouchEvent first, because sometimes it changes the state to
        // isPressed() on an ACTION_UP
        boolean result = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // So that the pressed outline is visible immediately on setStayPressed(),
                // we pre-create it on ACTION_DOWN (it takes a small but perceptible amount of time
                // to create it)
                if (mPressedBackground == null) {
                    mPressedBackground = mOutlineHelper.createMediumDropShadow(this);
                }

                mLongPressHelper.postCheckForLongPress();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // If we've touched down and up on an item, and it's still not "pressed", then
                // destroy the pressed outline
                if (!isPressed()) {
                    mPressedBackground = null;
                }

                mLongPressHelper.cancelLongPress();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!Utilities.pointInView(this, event.getX(), event.getY(), mSlop)) {
                    mLongPressHelper.cancelLongPress();
                }
                break;
        }
        return result;
    }

    void setStayPressed(boolean stayPressed) {
        mStayPressed = stayPressed;
        if (!stayPressed) {
            mPressedBackground = null;
        }

        // Only show the shadow effect when persistent pressed state is set.
        if (getParent() instanceof ShortcutAndWidgetContainer) {
            CellLayout layout = (CellLayout) getParent().getParent();
            layout.setPressedIcon(this, mPressedBackground, mOutlineHelper.shadowBitmapPadding);
        }

        updateIconState();
    }

    void clearPressedBackground() {
        setPressed(false);
        setStayPressed(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (super.onKeyDown(keyCode, event)) {
            // Pre-create shadow so show immediately on click.
            if (mPressedBackground == null) {
                mPressedBackground = mOutlineHelper.createMediumDropShadow(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Unlike touch events, keypress event propagate pressed state change immediately,
        // without waiting for onClickHandler to execute. Disable pressed state changes here
        // to avoid flickering.
        mIgnorePressedStateChange = true;
        boolean result = super.onKeyUp(keyCode, event);

        mPressedBackground = null;
        mIgnorePressedStateChange = false;
        updateIconState();
        return result;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mCustomShadowsEnabled) {
            super.draw(canvas);
            return;
        }

        final Drawable background = mBackground;
        if (background != null) {
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();

            if (mBackgroundSizeChanged) {
                background.setBounds(0, 0,  getRight() - getLeft(), getBottom() - getTop());
                mBackgroundSizeChanged = false;
            }

            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
            } else {
                canvas.translate(scrollX, scrollY);
                background.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }

        // If text is transparent, don't draw any shadow
        if (getCurrentTextColor() == getResources().getColor(android.R.color.transparent)) {
            getPaint().clearShadowLayer();
            super.draw(canvas);
            return;
        }

        // We enhance the shadow by drawing the shadow twice
        getPaint().setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, SHADOW_Y_OFFSET, SHADOW_LARGE_COLOUR);
        super.draw(canvas);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.clipRect(getScrollX(), getScrollY() + getExtendedPaddingTop(),
                getScrollX() + getWidth(),
                getScrollY() + getHeight(), Region.Op.INTERSECT);
        getPaint().setShadowLayer(SHADOW_SMALL_RADIUS, 0.0f, 0.0f, SHADOW_SMALL_COLOUR);
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mBackground != null) mBackground.setCallback(this);
        Drawable top = getCompoundDrawables()[1];

        if (top instanceof PreloadIconDrawable) {
            ((PreloadIconDrawable) top).applyTheme(getPreloaderTheme());
        }
        mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBackground != null) mBackground.setCallback(null);
    }

    @Override
    public void setTextColor(int color) {
        mTextColor = color;
        super.setTextColor(color);
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        mTextColor = colors.getDefaultColor();
        super.setTextColor(colors);
    }

    public void setTextVisibility(boolean visible) {
        Resources res = getResources();
        if (visible) {
            super.setTextColor(mTextColor);
        } else {
            super.setTextColor(res.getColor(android.R.color.transparent));
        }
        mIsTextVisible = visible;
    }

    public boolean isTextVisible() {
        return mIsTextVisible;
    }

    @Override
    protected boolean onSetAlpha(int alpha) {
        return true;
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        mLongPressHelper.cancelLongPress();
    }

    public void applyState(boolean promiseStateChanged) {
        if (getTag() instanceof ShortcutInfo) {
            ShortcutInfo info = (ShortcutInfo) getTag();
            final boolean isPromise = info.isPromise();
            final int progressLevel = isPromise ?
                    ((info.hasStatusFlag(ShortcutInfo.FLAG_INSTALL_SESSION_ACTIVE) ?
                            info.getInstallProgress() : 0)) : 100;

            Drawable[] drawables = getCompoundDrawables();
            Drawable top = drawables[1];
            if (top != null) {
                final PreloadIconDrawable preloadDrawable;
                if (top instanceof PreloadIconDrawable) {
                    preloadDrawable = (PreloadIconDrawable) top;
                } else {
                    preloadDrawable = new PreloadIconDrawable(top, getPreloaderTheme());
                    setCompoundDrawables(drawables[0], preloadDrawable, drawables[2], drawables[3]);
                }

                preloadDrawable.setLevel(progressLevel);
                if (promiseStateChanged) {
                    preloadDrawable.maybePerformFinishedAnimation();
                }
            }
        }
    }

    private Theme getPreloaderTheme() {
        Object tag = getTag();
        int style = ((tag != null) && (tag instanceof ShortcutInfo) &&
                (((ShortcutInfo) tag).container >= 0)) ? R.style.PreloadIcon_Folder
                        : R.style.PreloadIcon;
        Theme theme = sPreloaderThemes.get(style);
        if (theme == null) {
            theme = getResources().newTheme();
            theme.applyStyle(style, true);
            sPreloaderThemes.put(style, theme);
        }
        return theme;
    }
}

