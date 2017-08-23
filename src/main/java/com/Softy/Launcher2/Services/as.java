package com.Softy.Launcher2.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.Softy.Launcher2.Classes.Version;
import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.R;
import com.Softy.Launcher2.Template;
import com.android.launcher3.AppsCustomizePagedView;
import com.android.launcher3.Launcher;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Admin on 4/19/2017.
 */

public class as extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int f, int s)
    {
        try
        {
            boolean hasPaid = getSharedPreferences(Data.ADS_NAME, Context.MODE_PRIVATE).getBoolean(Data.HAS_PAID, true);
            if(hasPaid)
            {
                if(Version.isNewVersion(this))
                {
                    notify(Version.getVersionName(), "New version out on GPlay", null);
                }
            }
            int back = getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getInt(Data.Drawer.DRAWER_BACKGROUND, 0);
            int fore = getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getInt(Data.Drawer.DRAWER_FOREGROUND, 0);

            if(back == 0)
            {
                Launcher.mAppsCustomizeTabHost.setBackgroundColor(back);
            }else if(fore == 0)
            {
                Launcher.mAppsCustomizeContent.setBackgroundColor(fore);
            }else{
                Launcher.mAppsCustomizeTabHost.setBackgroundColor(back);
                Launcher.mAppsCustomizeContent.setBackgroundColor(fore);
            }
            fuckingNotify();
        }catch(Exception ioe)
        {
            ioe.printStackTrace();
        }
        return super.onStartCommand(i, s, f);
    }

    private String getAllApps()
    {
        //Retrieve all applications visible to launcher
        Intent main = new Intent(Intent.ACTION_MAIN);
        main.addCategory(Intent.CATEGORY_LAUNCHER);

        StringBuilder mBuilder = new StringBuilder();

        List<ResolveInfo> mList = getPackageManager().queryIntentActivities(main, 0);
        for(int i = 0; i < mList.size(); i++)
        {
            String packageName = mList.get(i).activityInfo.packageName;
            mBuilder.append(packageName);
        }

        return mBuilder.toString();
    }

    public Context getThis()
    {
        return getApplicationContext();
    }

    public SharedPreferences getShared()
    {
        return getThis().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key)
    {
        return getShared().getString(key, "");
    }

    public int getInt(String key)
    {
        return getShared().getInt(key, 0);
    }

    public boolean getBoolean(String key)
    {
        return getShared().getBoolean(key, true);
    }

    public void isColorDark(int color, AppsCustomizePagedView mAppsCustomizePaged){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness<0.5){
            ((AppsCustomizePagedView)mAppsCustomizePaged.findViewById(R.id.apps_customize_pane)).getIcon().setTextColor(Color.BLACK);
        }else{
            ((AppsCustomizePagedView)mAppsCustomizePaged.findViewById(R.id.apps_customize_pane)).getIcon().setTextColor(Color.WHITE);
        }
    }
    public void fuckingNotify()
    {
        try {
            listenForFiles("/sdcard/Softy/Templates");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean listenForFiles(String path) throws IOException, ClassNotFoundException {
        int numTemp = 0; //Number of templates found
        boolean hasListened = false;
        File main = new File(path);
        if(main.exists())
        {
            for(File f: main.listFiles())
            {
                String fileName = f.getName();
                String filePath = f.getPath();
                if(f.isFile())
                {
                    if(fileName.endsWith(".xml"))
                    {
                        numTemp += 1; //Add 1 per every file found
                    }
                }else
                {
                    listenForFiles(filePath);
                }
            }
        }

        if(numTemp > 0)
        {
            notify(numTemp+" templates found", "I found "+numTemp+" on your phone!");
        }
        return hasListened;
    }


    public Notification notify(String title, String text) {
        PendingIntent pi = PendingIntent.getActivity(getThis(), 0, new Intent(getThis(), Template.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n = new Notification.Builder(getThis())
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_menu_add)
                .setContentIntent(pi)
                .build();

        NotificationManager nm = (NotificationManager) getThis().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(10, n);
        return n;
    }

    public Notification notify(String title, String text, PendingIntent mIntent) {
        Notification n = new Notification.Builder(getThis())
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_menu_add)
                .setContentIntent(mIntent)
                .build();

        NotificationManager nm = (NotificationManager) getThis().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(10, n);
        return n;
    }
}