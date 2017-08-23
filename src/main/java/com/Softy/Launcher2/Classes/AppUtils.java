package com.Softy.Launcher2.Classes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.Softy.Launcher2.Data;
import com.android.launcher3.AppInfo;
import com.android.launcher3.IconCache;
import com.android.launcher3.ShortcutInfo;

/**
 * Created by softy on 7/4/17.
 */

public class AppUtils {
    private static int length = 1;
    public static void themeApps(Context mContext, AppInfo mInfo)
    {
        Bitmap b = mInfo.iconBitmap;
        //Variables needed for adding icon pack
        final int ICONSIZE = Tools.numtodp(65,mContext);
        Resources themeRes = null;
        String resPacName = mContext.getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getString("theme", "");

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
            try{themeRes =mContext.getPackageManager().getResourcesForApplication(resPacName);}catch(Exception e){};
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
        //Variables needed for adding icon pack
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
                    Drawable mIcon = new BitmapDrawable(mContext.getResources(), mInfo.iconBitmap);
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
    }

    void cancer()
    {
    }

    public static Bitmap themeShortcuts(Context mContext,ShortcutInfo mInfo, IconCache mCache)
    {

        Bitmap b = mInfo.getIcon(mCache);
        //Variables needed for adding icon pack
        final int ICONSIZE = Tools.numtodp(65,mContext);
        Resources themeRes = null;
        String resPacName = mContext.getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getString("theme", "");

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
            try{themeRes =mContext.getPackageManager().getResourcesForApplication(resPacName);}catch(Exception e){};
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
        //Variables needed for adding icon pack
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
                    Drawable mIcon = new BitmapDrawable(mContext.getResources(), b);
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
}
