package com.Softy.Launcher2.Classes;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.launcher3.AppInfo;
import com.android.launcher3.AppsCustomizePagedView;
import com.android.launcher3.BubbleTextView;

/**
 * Created by softy on 7/4/17.
 */

public class IconService extends Service {
    private static AppInfo info;
    public static void setInfo(AppInfo info) {
        IconService.info = info;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent main, int i, int i2)
    {
        try {
            BubbleTextView mText = AppsCustomizePagedView.getIcon();
            if(info == null) info = new AppInfo();

            mText.applyFromApplicationInfo(info);
        }catch(Exception e)
        {
            //Swallow the error
            e.printStackTrace();
        }
        return super.onStartCommand(main,i, i2);
    }

}