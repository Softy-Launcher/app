package com.Softy.Launcher2.Classes;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.Softy.Launcher2.MiniSettings;
import com.Softy.Launcher2.Settings.Drawer;
import com.Softy.Launcher2.Settings.General;

/**
 * Created by softy on 6/24/17.
 */

public class RandomMessage extends ContextWrapper{
    static String[] randomMessage = {"I am your settings suggestion. I suggest to you things you can do on Softy",
            "Change background & foreground color in Drawer menu.",
            "Go PRO to get rid of ads, get faster updates, and more!",
            "Customize your homescreen to your liking",
            "Join the BETA program to get timed patches",
            "E-Mail the developer for suggestions!",
            "Try some other Softy apps!",
            "Clear the app's cache for a more smoother experience",
            "Check Comely on Google Play for release notes!"
    };

    Intent[] randomAction = {new Intent(this, MiniSettings.class),
            new Intent(this, Drawer.class),
            new Intent(this, General.class),
            getPackageManager().getLaunchIntentForPackage("com.android.providers.settings")
    };

    static int mRandomKey = generateRandomKey();

    public RandomMessage(Context base) {
        super(base);
    }

    private static int generateRandomKey()
    {
        return ((int)(Math.random() * randomMessage.length)+0);
    }

    public static String getRandomMessage()
    {
        return randomMessage[generateRandomKey()];
    }

    public Intent getRandomAction()
    {
        return randomAction[mRandomKey];
    }
}