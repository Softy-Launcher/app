package com.Softy.Launcher2.Classes;

import android.content.Context;

/**
 * Created by softy on 6/30/17.
 */

public class Version {
    static VersionTask mInfo;

    static void runTask()
    {
        new VersionTask().execute();
    }

    public static String getVersionName()
    {
        runTask();
        return VersionTask.getVersionName();
    }

    public static int getVersionCode()
    {
        runTask();
        return VersionTask.getVersionCode();
    }

    public static boolean isNewVersion(Context mContext)
    {
        new Build().setContext(mContext);
        return getVersionCode() > Build.getVersionCode();
    }

}