package com.Softy.Launcher2;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Softy.Launcher2.R;
import com.Softy.Services.GoodHub.GoodHub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mcom on 1/22/17.
 */

public class Sprite extends GoodHub{
    private boolean arePermsDone = false;
    Transition f;
    private Button dark;
    private Button light;
    private ImageView preview;
    private TextView t;
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        apply_info();
    }
    void  apply_info(){
        getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putBoolean("checker", true).commit();
        final SharedPreferences sharedPrefs = getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        String themePicked = sharedPrefs.getString(Data.TEMP_THEME, "");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int color = getResources().getColor(R.color.sprite);
            getWindow().setNavigationBarColor(Color.rgb(Color.red(color), Color.green(color), Color.blue(color)));
        }
        if(themePicked.equals(Data.DARK) || themePicked.equals(Data.LIGHT)){
            setContentView(R.layout.sprite_animation);

            Handler h = new Handler();
            final TextView sum = (TextView) findViewById(R.id.sum);
            t = sum;
            String dir = Environment.getExternalStorageDirectory().getPath() +"/Softy/Templates/Default/Default.softy";
            String nd = Environment.getExternalStorageDirectory().getPath() +"/Softy/Templates/Default";
            if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.M )
            {
                preLogin();
            }
            moveFromAssets(sum, "INF/Default.softy", dir, nd);
            askForPermissions(sum, h);
        }else{
            setContentView(R.layout.set_up_revamped);

            dark = (Button) findViewById(R.id.dark);
            light = (Button) findViewById(R.id.light);
            preview = (ImageView) findViewById(R.id.preview);

            dark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dark.setBackgroundColor(Color.GREEN);
                    light.setBackground(null);
                    sharedPrefs.edit().putString(Data.TEMP_THEME, Data.DARK).commit();
                    apply_info();
                }
            });

            light.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    light.setBackgroundColor(Color.GREEN);
                    dark.setBackground(null);
                    sharedPrefs.edit().putString(Data.TEMP_THEME, Data.LIGHT).commit();
                    apply_info();
                }
            });

            preview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getApplicationContext(), "You can change the theme by Settings > General > Theme", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

        }
    }
    void initViews(final TextView sum , Handler h){
        final String sd = "/sdcard";
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFolderExist(Data.BACKUP_PATH) == true){
                    sum.setText("Extracting backups");
                    Data.copy(Data.BACKUP_PATH, "data.xml", "/data/data/com.MApps.Launcher.MiniUI/shared_prefs/"+Data.NAME);
                }
            }
        }, 3000);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFolderExist(sd + "/Softy/download")){
                    sum.setText("Extracting download");
                }
            }
        }, 3000);
        sum.setText("Finishing setup");
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                sum.setText("Done!");
            }
        }, 3000);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(arePermsDone == true){
                    preLogin();
                }else{

                }
            }
        }, 3000);
    }

    @Override
    public void afterLogin()
    {
        super.afterLogin();
        //In all cases, Softy does not create the preference
        SharedPreferences mProPrefs = getSharedPreferences(Data.PRO_NAME, Context.MODE_PRIVATE);
        SharedPreferences mAdsPrefs = getSharedPreferences(Data.ADS_NAME, Context.MODE_PRIVATE);
        getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putBoolean("ic-pack-applied", false).commit();

        mProPrefs.edit().putString("starter", "yes").commit();
        mAdsPrefs.edit().putString("starter","yes").commit();
        mAdsPrefs.edit().putBoolean(Data.HAS_PAID, false).commit();
        mAdsPrefs.edit().putBoolean(Data.FIRST_TIME_PAID, false).commit();
        getSharedPreferences(Data.NAME, MODE_PRIVATE).edit().putBoolean("show_change", true).commit();
        getPackageManager().setComponentEnabledSetting(new ComponentName(Sprite.this, Mini.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        getPackageManager().setComponentEnabledSetting(new ComponentName(Sprite.this, Sprite.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        t.setText("Please select Softy");
        Intent main = new Intent(Intent.ACTION_MAIN);
        main.addCategory(Intent.CATEGORY_HOME);
        getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putBoolean("is_new", true).commit();
        startActivity(main);
    }
    private void moveFromAssets(TextView tv, String filename,String dir, String newDir)
    {
        tv.setText("Downloading default template");
        AssetManager assetManager = getAssets();
        String[] files = null;
        InputStream in = null;
        OutputStream out = null;
        try
        {
            File main = new File(newDir);
            if(!(main.exists())){
                main.mkdirs();
            }
            in = assetManager.open(filename);
            out = new FileOutputStream(dir);
            copyFile(in, out, tv);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        }
        catch(IOException e)
        {
            Log.e("tag", "Failed to copy asset file: " + filename, e);
            tv.setText("Download FAILED:(");
        }
    }
    private void copyFile(InputStream in, OutputStream out, TextView tv) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
            tv.setText("Download success!");
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        finish();
    }
    private void askForPermissions(final TextView tv, final Handler h) {
        arePermsDone = false;
        int write = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeContacts = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CONTACTS);
        int readContacts = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        int telephony = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        List<String> perms = new ArrayList<String>();
        if(write != PackageManager.PERMISSION_GRANTED){
            perms.add( Manifest.permission.WRITE_EXTERNAL_STORAGE);
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv.setText("Thank you for allowing me to write.");
                }
            }, 3000);
        }
        if(writeContacts != PackageManager.PERMISSION_GRANTED){
            perms.add(Manifest.permission.WRITE_CONTACTS);
        }
        if(readContacts != PackageManager.PERMISSION_GRANTED){
            perms.add(Manifest.permission.READ_CONTACTS);
            setText(tv, h, "Thanks for the Contacts permissions.");
        }
        if(telephony != PackageManager.PERMISSION_GRANTED){
            perms.add(Manifest.permission.CALL_PHONE);
            setText(tv, h, "Thanks for the calling permissions.");
        }
        if(!perms.isEmpty()){
            ActivityCompat.requestPermissions(Sprite.this, perms.toArray(new String[perms.size()]), 1);
        }
        initViews(tv ,h);
        arePermsDone = true;
    }

    boolean isExist(File f){
        return f.exists();
    }

    boolean isExist(String path){
        return new File(path).exists();
    }

    boolean isFolderExist(File f){
        return f.isDirectory() && f.exists();
    }

    boolean isFolderExist(String path){
        File f = new File(path);
        return f.isDirectory() && f.exists();
    }

    void setText(final TextView tv, Handler h, final String text){
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setText(text);
            }
        }, 3000);
    }
}
