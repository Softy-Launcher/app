package com.Softy.Launcher2.Classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.Softy.Launcher2.R;

/**
 * Created by mcom on 1/29/17.
 */

public class LockUtils {
    private Overlay mOverlay;
    private OnLockChanged mLockChanged;

    public interface OnLockChanged{
        public void onStatusChanged(boolean isLocked);
    }

    public LockUtils(){
        reset();
    }

    // Display overlay
    public void lock(Activity mActivity){
        if(mOverlay == null){
            mOverlay = new Overlay(mActivity);
            mOverlay.show();
            mLockChanged = (OnLockChanged) mActivity;
        }
    }

    //reset lock variables
    public void reset(){
        if(mOverlay != null){
            mOverlay.dismiss();
            mOverlay = null;
        }
    }

    //Unlock the app
    public void unlock(){
        if(mOverlay != null){
            mOverlay.dismiss();
            mOverlay = null;
            if(mLockChanged != null){
                mLockChanged.onStatusChanged(false);
            }
        }
    }

    public static class Overlay extends AlertDialog {
        public Overlay(Activity mActivity){
            super(mActivity, R.style.Overlay);
            WindowManager.LayoutParams mWindowLayout = getWindow().getAttributes();
            mWindowLayout.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            mWindowLayout.dimAmount = 0.0f;
            mWindowLayout.width = 0;
            mWindowLayout.height = 0;
            mWindowLayout.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(mWindowLayout);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    0xffffff);
            setOwnerActivity(mActivity);
            setCancelable(false);
        }

        //consume touch
        public final boolean dispatchTouchEvent(MotionEvent mMotionEvent){
            return true;
        }
    }
}
