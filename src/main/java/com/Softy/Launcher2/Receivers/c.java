package com.Softy.Launcher2.Receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.Softy.Launcher2.Classes.BroadcastReceiver;
import com.Softy.Launcher2.Classes.RandomMessage;
import com.Softy.Launcher2.Views.MessagePreference;


/**
 * Created by softy on 6/29/17.
 */

public class c extends BroadcastReceiver{
    private MessagePreference messagePreference;
    public c()
    {

    }
    public c(MessagePreference messagePreference)
    {
        this.messagePreference = messagePreference;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //Have we been nullified?
        if(messagePreference == null) return;

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Updating suggestions every three seconds
                messagePreference.setMessage(RandomMessage.getRandomMessage());
            }
        }, 3000);
    }
}
