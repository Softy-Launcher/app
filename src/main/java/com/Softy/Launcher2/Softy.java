package com.Softy.Launcher2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.Softy.Launcher2.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by softy on 6/16/17.
 */

public class Softy extends Application {
    //Do oncreate for Theme, instead of in every activity

    @Override
    public void onCreate()
    {
        super.onCreate();
        final SharedPreferences sharedPrefs = getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        String theme = sharedPrefs.getString(Data.TEMP_THEME, "");
        switch(theme)
        {
            case Data.LIGHT:
                setTheme(R.style.Light);
                break;

            case Data.DARK:
                setTheme(R.style.Dark);
                break;

            case "":
                setTheme(R.style.Dark);
                break;
        }
    }

    //Open the notifications panel/shade, whatever the fuck Google calls it
    public Softy openNotifications() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //get the service for the status bar
        @SuppressLint("WrongConstant") Object statusBar = getSystemService("statusbar");
        Class<?> Manager = Class.forName("android.app.StatusBarManager");
        Method show = null;
        int buildSDKInt = Build.VERSION.SDK_INT;
        if(buildSDKInt >= 17)
        {
            show = Manager.getMethod("expandNotificationsPanel");
        }else
        {
            show = Manager.getMethod("expand");
        }
        show.invoke(statusBar);
        return this;
    }

    //Launch the application the user chose on swipe down/up
    public Softy launchApp(String packageName)
    {
        Intent main = getPackageManager().getLaunchIntentForPackage(packageName);
        startActivity(main);
        return this;
    }

    //Launch picker for Settings
    public Softy openAppPicker(Activity mainActivity)
    {
        Intent main = new Intent(Intent.ACTION_MAIN);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        mainActivity.startActivityForResult(main, Data.LAUNCH_PICKER_CODE);
        return this;
    }
}
