package com.Softy.Launcher2.Classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.Softy.Launcher2.Data;

/**
 * Created by Admin on 4/19/2017.
 */

public class BroadcastReceiver extends android.content.BroadcastReceiver{
    private Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
    }

    public Context getThis()
    {
        return mContext;
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
}
