package com.Softy.Launcher2.Classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by mcom on 2/10/17.
 */

public class Build extends android.os.Build{
    private static double version;
    public static class App{
        public static double APP_VERSION = 0.0;
        public static String VERSION_NAME = "Wild Coyote";
        public static int APP_CODE = 0;
    }

    public static class VersionClass{
        public static double COYOTE = 1.0;
        public static double FOX = 1.1;
        public static double WILY_GRAY_FOX = 1.2;
    }

    public static void selectVersion(double version){
        Build.version = version;
    }

    public static double getSelectedVersion(){
        return version;
    }

    public static String getSelectedVersionName(){
        if(version == VersionClass.COYOTE){
            return "Wild Coyote";
        }else if(version == VersionClass.FOX){
            return "Nefarious Fox";
        }else if(version == VersionClass.WILY_GRAY_FOX){
            return "Flying Wolf";
        }else
            return "A wild error has appeared";
    }

    private static Context mContext;
    public Build setContext(Context mContext)
    {
        this.mContext = mContext;
        return this;
    }

    public static String getVersionName()
    {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            int version = info.versionCode;
            String name = info.versionName;
            return name;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Could not get name";
        }
    }

    public static int getVersionCode()
    {
        try
        {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        }catch(PackageManager.NameNotFoundException nnfe)
        {
            nnfe.printStackTrace();
            return 0;
        }
    }

    public static void showChangeLog(String title, String message)
    {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

//use this code for storing payment info and other data,
//instead of writing to /data/data/com.Softy.Launcher.MiniUI/shared_prefs
//write to /sdcard/Softy/app.prop
//This can also be used to add features to specific devices
//Like fingerprint actions for fingerprint devices
//R.I.P Nexus
    public static class PropReader{
    public static String get(String fileName, String line){
        String text ="";
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File("/sdcard/Softy/"+fileName+".prop")));
            String newLine;
            while((newLine = br.readLine()) != null){
                newLine = newLine.replace(line+"=", "");
                text = newLine;
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        return text;
    }
        public static String get(String line){
            String text ="";
            try{
                BufferedReader br = new BufferedReader(new FileReader(new File("/sdcard/Softy/app.prop")));
                String newLine;
                while((newLine = br.readLine()) != null){
                    newLine = newLine.replace(line+"=", "");
                    text = newLine;
                }
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
            return text;
        }

    public static void writeToProp(String fileName, String line, String value){
        try{
            File prop = new File("/sdcard/Softy/"+fileName+".prop");
            if(!(new File("/sdcard/Softy").exists())){
                new File("/sdcard/Softy").mkdirs();
            }
            FileWriter fw = new FileWriter(prop);

            fw.write(line+"="+value+"\n");
            fw.flush();
            fw.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
        public static void writeToProp(String line, String value){
            try{
                File prop = new File("/sdcard/Softy/app.prop");
                if(!(new File("/sdcard/Softy").exists())){
                    new File("/sdcard/Softy").mkdirs();
                }
                if(prop.exists())
                {
                    prop.delete();
                }
                FileWriter fw = null;
                if(prop.exists()){
                    fw = new FileWriter(prop, true);
                }else{
                    fw = new FileWriter(prop);
                }

                fw.write(line+"="+value+"\n");
                fw.flush();
                fw.close();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

    public static class Hardware
    {
        public static String FINGERPRINT_SCANNER = "ro.hardware.fingerprint_scanner_exists";
        public static String BACK_KEY = "ro.hardware.key_back";
        public static String HOME_KEY = "ro.hardware.key_home";
        public static String MENU_KEY = "ro.hardware.key_menu";
        public static String SEARCH_KEY = "ro.hardware.key_search";
        public static String CAPACITIVE_KEYS = "ro.hardware.key_keys";

        public static void writeHardware(Context c)
        {
            if(VERSION.SDK_INT >= 23)
            {
                FingerprintManager mManager = (FingerprintManager) c.getSystemService(Context.FINGERPRINT_SERVICE);
                if(mManager.isHardwareDetected())
                {
                    PropReader.writeToProp("hardware",FINGERPRINT_SCANNER, "true");
                }else
                {
                    PropReader.writeToProp("hardware",FINGERPRINT_SCANNER, "false");
                }

            }
            boolean hasMenu = ViewConfiguration.get(c).hasPermanentMenuKey();
            boolean hasBack = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            boolean hasHome = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
            boolean hasSearch = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_SEARCH);

            if(hasMenu)
            {
                PropReader.writeToProp("hardware",MENU_KEY, "capacitive");
            }else
            {
                PropReader.writeToProp("hardware",MENU_KEY, "software");
            }

            if(hasBack)
            {
                PropReader.writeToProp("hardware",BACK_KEY, "capacitive");
            }else
            {
                PropReader.writeToProp("hardware",BACK_KEY, "software");
            }

            if(hasHome)
            {
                PropReader.writeToProp("hardware",HOME_KEY, "capacitive");
            }else
            {
                PropReader.writeToProp("hardware",HOME_KEY, "software");
            }

            if(hasSearch)
            {
                PropReader.writeToProp("hardware",SEARCH_KEY, "capacitive");
            }else
            {
                PropReader.writeToProp("hardware",SEARCH_KEY, "software");
            }
        }

        public static String getHardware(String hardware)
        {
            return PropReader.get("hardware", hardware);
        }
    }

    public void s()
    {
    }
}
