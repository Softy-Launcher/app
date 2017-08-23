package com.Softy.Launcher2.Receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.Softy.Launcher2.Classes.BroadcastReceiver;
import com.Softy.Launcher2.Template;

import java.io.File;
import java.io.IOException;

/**
 * Created by Admin on 4/19/2017.
 */

public class b extends BroadcastReceiver {

    @Override
    public void onReceive(Context c, Intent i)
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

}