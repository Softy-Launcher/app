package com.Softy.Launcher2.Services.Tiles;

import android.content.Intent;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.WindowManager;

import com.Softy.Launcher2.Settings.General;

/**
 * Created by Admin on 4/29/2017.
 */

public class GeneralTileService extends TileService{
    private WindowManager wm;
    @Override
    public void onTileAdded()
    {
        Log.i("TileAdded", "User added apps tile");
    }

    @Override
    public void onStartListening()
    {
        Log.i("Listening", "Listening for press events");
    }

    @Override
    public void onClick() {
        handleWindows();

    }



    private void handleWindows()
    {
        Intent i = new Intent(this,General.class);
        startActivity(i);
    }

    @Override
    public void onTileRemoved()
    {
        Log.e("TileRemoved", "User removed apps tile");
    }
}
