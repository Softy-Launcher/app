package com.Softy.Launcher2.Receivers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.Softy.Launcher2.Classes.BroadcastReceiver;
import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.R;
import com.android.launcher3.AppsCustomizePagedView;
import com.android.launcher3.Launcher;

/**
 * Created by Admin on 4/19/2017.
 */

public class a extends BroadcastReceiver {
    private Context c;
    @Override
    public void onReceive(Context context, Intent intent) {
        this. c = context;
        try {
            SharedPreferences sharedPrefs = context.getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
            int back = 0;
            if(sharedPrefs.getInt(Data.Drawer.DRAWER_BACKGROUND, 0) == 0 )
            {
                back = context.getResources().getColor(R.color.white);
            }else
            {
                back = sharedPrefs.getInt(Data.Drawer.DRAWER_BACKGROUND, 0);
            }
            int fore = 0;
            if(sharedPrefs.getInt(Data.Drawer.DRAWER_FOREGROUND, 0) == 0)
            {
                fore = context.getResources().getColor(R.color.dark);
            }else
            {
                fore = sharedPrefs.getInt(Data.Drawer.DRAWER_FOREGROUND, 0);
            }
            Launcher.mAppsCustomizeContent.setBackgroundColor(back);
            isColorDark(back, Launcher.mAppsCustomizeContent);
            Launcher.mAppsCustomizeTabHost.setBackgroundColor(fore);
        }catch(NullPointerException npe)
        {
            npe.printStackTrace();
        }
    }
    public void isColorDark(int color, AppsCustomizePagedView mAppsCustomizeContent){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness<0.5){
            mAppsCustomizeContent.getIcon().setTextColor(Color.BLACK);
        }else{
            mAppsCustomizeContent.getIcon().setTextColor(Color.WHITE);
        }
    }

}
