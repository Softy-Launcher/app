package com.Softy.Launcher2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Locker extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: Make sure to display this activity when the user turns off screen.
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent i = new Intent(context, LockScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
