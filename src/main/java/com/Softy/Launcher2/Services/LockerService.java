package com.Softy.Launcher2.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.Softy.Launcher2.Locker;

/**
 * Created by mcom on 1/29/17.
 */

public class LockerService extends Service{
    private Locker mLocker;
    private Context mContext;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        setLock();
        return super.onStartCommand(intent, flags, startId);
    }

    private void setLock(){
        IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mIntentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);

        mLocker = new Locker();
        registerReceiver(mLocker, mIntentFilter);
    }

    @Override
    public void onCreate(){
        setLock();
    }

}
