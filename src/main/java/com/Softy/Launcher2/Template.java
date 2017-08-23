package com.Softy.Launcher2;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.widget.Toast;

import com.Softy.Launcher2.Classes.Build;
import com.Softy.Launcher2.Classes.RandomMessage;
import com.Softy.Launcher2.Classes.Templates;
import com.Softy.Launcher2.R;
import com.Softy.Launcher2.Services.as;
import com.Softy.Launcher2.Views.MessagePreference;
import com.Softy.Templates.Creator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * Created by mcom on 2/21/17.
 */

public class Template extends PreferenceActivity{

    @Override
    public void onCreate(Bundle b){
        String theme = getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getString(Data.TEMP_THEME, "");
        switch(theme)
        {
            case Data.LIGHT:
                setTheme(R.style.Light);
                break;

            case Data.DARK:
                setTheme(R.style.Dark);
                break;

            case "":
                setTheme(R.style.Dark);
                break;
        }
        super.onCreate(b);
        addPreferencesFromResource(R.xml.templates);
        MessagePreference mMessage = (MessagePreference) findPreference("message");
        mMessage.setMessage(RandomMessage.getRandomMessage());
        final PreferenceCategory mList = (PreferenceCategory) findPreference(getResources().getString(R.string.templates_category));
        final PreferenceCategory mAppsList = (PreferenceCategory) findPreference("app_templates");

        try {
            showAndAdd(this, mAppsList );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            listTemplatesAt(mList, Environment.getExternalStorageDirectory().getPath()+"/Softy/Templates/");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private Templates mTemp;
    public void listTemplatesAt(final PreferenceCategory mList, String mPath) throws IOException, ClassNotFoundException {
        final File main = new File(mPath);
        if(main.exists() && main.isDirectory())
        {
            File[] list = main.listFiles();
            for(final File f : list)
            {
                String fileName = f.getName();
                final String filePath = f.getPath();
                if(f.isFile())
                {
                    if(f.getPath().endsWith(".xml"))
                    {
                        mTemp = new Templates(this).read(f.getPath());
                        Preference mTempPrefs = new Preference(this);
                        mTempPrefs.setTitle(fileName.replace(".xml", ""));
                        mTempPrefs.setSummary(mTemp.getSummary(f.getPath()));
                        mTempPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {
                                mTemp.showMessage("Using "+mTemp.getTemplateName(f.getPath())+" as a template");
                                try {
                                    Creator mCreator = new Creator("/sdcard/Softy/used.xml")
                                            .init()
                                            .addExternalNode("USED_TEMPLATE", mTemp.getTemplateName(f.getPath()))
                                            .build();
                                } catch (TransformerException e) {
                                    e.printStackTrace();
                                } catch (ParserConfigurationException e) {
                                    e.printStackTrace();
                                }
                                ProgressDialog mDialog = initializeProgressDialog("Using "+mTemp.getTemplateName(f.getPath())+" as a template");
                                mDialog.setCanceledOnTouchOutside(false);
                                mDialog.setCancelable(false);
                                mDialog.show();
                                SharedPreferences mSharedPrefs = getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                                int versionCode = mTemp.getMin(f.getPath());
                                String theme = mTemp.getTheme(f.getPath());
                                String drawerBackground = mTemp.getDrawerBackground(f.getPath());
                                String drawerForeground = mTemp.getDrawerForeground(f.getPath());
                                final String wallpaperLocation = mTemp.getWallpaperPath(f.getPath());
                                final String iconPackPackage = mTemp.getIconPackPackage(f.getPath());

                                if(versionCode > Build.getVersionCode()) {
                                    mTemp.showMessage("Update Softy to use this template.");
                                    return false;
                                }
                                mDialog.setProgress(10);
                                switch(drawerBackground)
                                {
                                    case "":
                                        int blankColor = Color.BLACK;
                                        mEditor.putInt(Data.Drawer.DRAWER_BACKGROUND, blankColor);
                                        //Set the text color for dark/light background
                                        isColorDark(blankColor, Data.Drawer.DRAWER_BACKGROUND);
                                        break;
                                    default:
                                        //Interpret the hex code as integer
                                        if(drawerBackground.contains(",")){
                                            mTemp.showMessage("I only support hex. Converting to hex");
                                            drawerBackground = String.format("#%02x%02x%02x", drawerBackground);
                                        }
                                        int color = Color.parseColor(drawerBackground);
                                        int red = Color.red(color);
                                        int green = Color.green(color);
                                        int blue = Color.blue(color);
                                        mEditor.putInt(Data.Drawer.DRAWER_BACKGROUND, Color.rgb(red, green, blue));
                                        isColorDark(Color.rgb(red, green, blue), Data.Drawer.DRAWER_BACKGROUND);
                                        break;
                                }

                                mDialog.setProgress(50);
                                switch(drawerForeground)
                                {
                                    case "":
                                        int blankColor = Color.WHITE;
                                        mEditor.putInt(Data.Drawer.DRAWER_FOREGROUND, blankColor);
                                        //Set the text color for dark/light background
                                        isColorDark(blankColor, Data.Drawer.DRAWER_FOREGROUND);
                                        break;

                                    default:
                                        if(drawerForeground.contains(",")){
                                            mTemp.showMessage("I only support hex. Converting to hex");
                                            drawerForeground = String.format("#%02x%02x%02x", drawerForeground);
                                        }

                                        int color = Color.parseColor(drawerForeground);
                                        int red = Color.red(color);
                                        int green = Color.green(color);
                                        int blue = Color.blue(color);

                                        mEditor.putInt(Data.Drawer.DRAWER_FOREGROUND, Color.rgb(red, green, blue));
                                        isColorDark(Color.rgb(red, green, blue), Data.Drawer.DRAWER_FOREGROUND);
                                        break;
                                }

                                mDialog.setProgress(75);
                                switch(theme)
                                {
                                    case Data.DARK:
                                        mEditor.putString(Data.TEMP_THEME, Data.DARK);
                                        break;

                                    case Data.LIGHT:
                                        mEditor.putString(Data.TEMP_THEME, Data.LIGHT);
                                        break;

                                    default:

                                        break;
                                }
                                if(wallpaperLocation.startsWith("url"))
                                {
                                    try {
                                        int randomInt = ((int)(Math.random() * 1000)+0);
                                        if(new File(Data.Environment.DIRECTORY_WALLPAPERS+"/"+"Wallpaper_"+randomInt+".jpg").exists())
                                        {
                                            randomInt = ((int) (Math.random() * 6000)+0);
                                        }
                                        downloadToWallpaper(Uri.parse(wallpaperLocation.replace("url:","").trim()), randomInt, f.getPath());
                                        Handler mHandler = new Handler();
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Bitmap mWallpaper = BitmapFactory.decodeFile("/sdcard/Pictures/"+mTemp.getWallpaperName(f.getPath())+".jpg");
                                                try {
                                                    setWallpaper(mWallpaper);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, 3000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else if(wallpaperLocation.startsWith("path"))
                                {
                                    try {
                                        Bitmap mBitmap = BitmapFactory.decodeFile(wallpaperLocation.replace("path:",""));
                                        setWallpaper(mBitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    //noop
                                }

                                switch(iconPackPackage)
                                {
                                    case "com.natewren.linesfree":
                                        if(!isAppInstalled("com.natewren.linesfree"))
                                        {
                                            final AlertDialog mInstallDialog = new AlertDialog.Builder(Template.this)
                                                    .setTitle("Install Icon Pack")
                                                    .setMessage("An icon pack required by this template is not installed. Do you wish to install it?")
                                                    .setPositiveButton("Yes, install", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            try
                                                            {
                                                                startActivity(new Intent(Intent.ACTION_MAIN, Uri.parse("market://details?id="+iconPackPackage)));
                                                            }catch(ActivityNotFoundException anfe)
                                                            {
                                                                startActivity(new Intent(Intent.ACTION_MAIN, Uri.parse("https://play.google.com/store/apps/details?id="+iconPackPackage)));
                                                                anfe.printStackTrace();
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("No, go on", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            return;
                                                        }
                                                    })
                                                    .show();
                                        }

                                        mEditor.putString("theme", iconPackPackage);
                                    case "none":

                                    default:
                                        if(!isAppInstalled(iconPackPackage))
                                        {
                                            final AlertDialog mInstallDialog = new AlertDialog.Builder(Template.this)
                                                    .setTitle("Install Icon Pack")
                                                    .setMessage("An icon pack required by this template is not installed. Do you wish to install it?")
                                                    .setPositiveButton("Yes, install", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            try
                                                            {
                                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+iconPackPackage)));
                                                            }catch(ActivityNotFoundException anfe)
                                                            {
                                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+iconPackPackage)));
                                                                anfe.printStackTrace();
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("No, go on", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            return;
                                                        }
                                                    })
                                                    .show();
                                        }

                                        mEditor.putString("theme", iconPackPackage);
                                        mEditor.putString("applied_recent",iconPackPackage);
                                        break;
                                }
                                mDialog.setProgress(100);
                                mDialog.dismiss();
                                mEditor.commit();
                                return false;
                            }

                        });

                        if(mList.getPreferenceCount() > 0 )
                            mList.removePreference(mTempPrefs);

                        mList.addPreference(mTempPrefs);
                    }
                }else
                {
                    listTemplatesAt(mList,filePath);
                }
            }
        }
        else
        {
            main.mkdirs();
            Preference mWriteTemps = new Preference(this);
            mWriteTemps.setTitle("Create a sample template");
            mWriteTemps.setSummary("Create a template to use as a sample");
            mWriteTemps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        new Templates(Template.this).writeTemplate();
                        add();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                private void add() throws IOException, ClassNotFoundException {
                    String fileName = "Softy.xml";
                    final String filePath = "/sdcard/Softy/Templates/Softy.xml";
                    final File f = new File(filePath);
                    //Check if the template was used
                    //If so, stop it from being added
                    final Templates mTemp = new Templates(Template.this).read(filePath);
                    Preference mTempPrefs = new Preference(Template.this);
                    mTempPrefs.setTitle(fileName.replace(".xml", ""));
                    mTempPrefs.setSummary(mTemp.getSummary(filePath));
                    mTempPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            mTemp.showMessage("Using "+mTemp.getTemplateName(f.getPath())+" as a template");
                            try {
                                Creator maker = new Creator("/sdcard/Softy/used.xml")
                                        .init()
                                        .addExternalNode("USED_TEMPLATE", mTemp.getTemplateName(f.getPath()))
                                        .build();
                            } catch (TransformerException e) {
                                e.printStackTrace();
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            }
                            ProgressDialog mDialog = initializeProgressDialog("Using "+mTemp.getTemplateName(f.getPath())+" as a template");
                            mDialog.setCanceledOnTouchOutside(false);
                            mDialog.setCancelable(false);
                            mDialog.show();
                            SharedPreferences mSharedPrefs = getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                            int versionCode = mTemp.getMin(f.getPath());
                            String theme = mTemp.getTheme(f.getPath());
                            String drawerBackground = mTemp.getDrawerBackground(f.getPath());
                            String drawerForeground = mTemp.getDrawerForeground(f.getPath());
                            final String wallpaperLocation = mTemp.getWallpaperPath(f.getPath());
                            final String iconPackPackage = mTemp.getIconPackPackage(f.getPath());

                            if(versionCode > Build.getVersionCode()) {
                                mTemp.showMessage("Update Softy to use this template.");
                                return false;
                            }
                            mDialog.setProgress(10);
                            switch(drawerBackground)
                            {
                                case "":
                                    int blankColor = Color.BLACK;
                                    mEditor.putInt(Data.Drawer.DRAWER_BACKGROUND, blankColor);
                                    //Set the text color for dark/light background
                                    isColorDark(blankColor, Data.Drawer.DRAWER_BACKGROUND);
                                    break;
                                default:
                                    //Interpret the hex code as integer
                                    if(drawerBackground.contains(",")){
                                        mTemp.showMessage("I only support hex. Converting to hex");
                                        drawerBackground = String.format("#%02x%02x%02x", drawerBackground);
                                    }
                                    int color = Color.parseColor(drawerBackground);
                                    int red = Color.red(color);
                                    int green = Color.green(color);
                                    int blue = Color.blue(color);
                                    mEditor.putInt(Data.Drawer.DRAWER_BACKGROUND, Color.rgb(red, green, blue));
                                    isColorDark(Color.rgb(red, green, blue), Data.Drawer.DRAWER_BACKGROUND);
                                    break;
                            }

                            mDialog.setProgress(50);
                            switch(drawerForeground)
                            {
                                case "":
                                    int blankColor = Color.WHITE;
                                    mEditor.putInt(Data.Drawer.DRAWER_FOREGROUND, blankColor);
                                    //Set the text color for dark/light background
                                    isColorDark(blankColor, Data.Drawer.DRAWER_FOREGROUND);
                                    break;

                                default:
                                    if(drawerForeground.contains(",")){
                                        mTemp.showMessage("I only support hex. Converting to hex");
                                        drawerForeground = String.format("#%02x%02x%02x", drawerForeground);
                                    }

                                    int color = Color.parseColor(drawerForeground);
                                    int red = Color.red(color);
                                    int green = Color.green(color);
                                    int blue = Color.blue(color);

                                    mEditor.putInt(Data.Drawer.DRAWER_FOREGROUND, Color.rgb(red, green, blue));
                                    isColorDark(Color.rgb(red, green, blue), Data.Drawer.DRAWER_FOREGROUND);
                                    break;
                            }

                            mDialog.setProgress(75);
                            switch(theme)
                            {
                                case Data.DARK:
                                    mEditor.putString(Data.TEMP_THEME, Data.DARK);
                                    break;

                                case Data.LIGHT:
                                    mEditor.putString(Data.TEMP_THEME, Data.LIGHT);
                                    break;

                                default:

                                    break;
                            }

                            if(wallpaperLocation.startsWith("url"))
                            {
                                try {
                                    int randomInt = ((int)(Math.random() * 1000)+0);
                                    if(new File(Data.Environment.DIRECTORY_WALLPAPERS+"/"+"Wallpaper_"+randomInt+".jpg").exists())
                                    {
                                        randomInt = ((int) (Math.random() * 6000)+0);
                                    }
                                    downloadToWallpaper(Uri.parse(wallpaperLocation.replace("url:","").trim()), randomInt, f.getPath());
                                    Handler mHandler = new Handler();
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Bitmap mWallpaper = BitmapFactory.decodeFile("/sdcard/Pictures/"+mTemp.getWallpaperName(f.getPath())+".jpg");
                                            try {
                                                setWallpaper(mWallpaper);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, 3000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else if(wallpaperLocation.startsWith("path"))
                            {
                                try {
                                    Bitmap mBitmap = BitmapFactory.decodeFile(wallpaperLocation.replace("path:",""));
                                    setWallpaper(mBitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                //noop
                            }

                            switch(iconPackPackage)
                            {
                                case "com.natewren.linesfree":
                                    if(!isAppInstalled("com.natewren.linesfree"))
                                    {
                                        final AlertDialog mInstallDialog = new AlertDialog.Builder(Template.this)
                                                .setTitle("Install Icon Pack")
                                                .setMessage("An icon pack required by this template is not installed. Do you wish to install it?")
                                                .setPositiveButton("Yes, install", new DialogInterface.OnClickListener() {
                                                    @Override

                                                    public void onClick(DialogInterface dialog, int which) {
                                                        try
                                                        {
                                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+iconPackPackage)));
                                                        }catch(ActivityNotFoundException anfe)
                                                        {
                                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+iconPackPackage)));
                                                            anfe.printStackTrace();
                                                        }
                                                    }
                                                })
                                                .setNegativeButton("No, go on", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        return;
                                                    }
                                                })
                                                .show();
                                    }

                                    mEditor.putString("theme", iconPackPackage);
                                case "none":

                                default:
                                    if(!isAppInstalled(iconPackPackage))
                                    {
                                        final AlertDialog mInstallDialog = new AlertDialog.Builder(Template.this)
                                                .setTitle("Install Icon Pack")
                                                .setMessage("An icon pack required by this template is not installed. Do you wish to install it?")
                                                .setPositiveButton("Yes, install", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        try
                                                        {
                                                            startActivity(new Intent(Intent.ACTION_MAIN, Uri.parse("market://details?id="+iconPackPackage)));
                                                        }catch(ActivityNotFoundException anfe)
                                                        {
                                                            startActivity(new Intent(Intent.ACTION_MAIN, Uri.parse("https://play.google.com/store/apps/details?id="+iconPackPackage)));
                                                            anfe.printStackTrace();
                                                        }
                                                    }
                                                })
                                                .setNegativeButton("No, go on", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        return;
                                                    }
                                                })
                                                .show();
                                    }

                                    mEditor.putString("theme", iconPackPackage);
                                    mEditor.putString("applied_recent",iconPackPackage);
                                    break;
                            }
                            mDialog.setProgress(100);
                            mDialog.dismiss();
                            mEditor.commit();
                            return false;
                        }

                    });

                    if(mList.getPreferenceCount() > 0 )
                        mList.removePreference(mTempPrefs);

                    mList.addPreference(mTempPrefs);
                }
            });
            mList.addPreference(mWriteTemps);
        }
    }

    private boolean isAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }

    private ProgressDialog initializeProgressDialog(String title) {
        ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setTitle(title);
        mDialog.setMax(100);
        return mDialog;
    }


    public void isColorDark(int color, String preference){
        getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putInt(preference, color).commit();
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness<0.5){
            getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putInt(Data.DRAWER_TEXT_COLOR, Color.BLACK).commit();
        }else{
            getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putInt(Data.DRAWER_TEXT_COLOR, Color.WHITE).commit();
        }
    }

    private void copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0 ,read);
        }
    }

    private long downloadToWallpaper (Uri uri, int randomInt, String path) {

        long downloadReference;

        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Downloading wallpaper");

        //Setting description of request
        request.setDescription("Downloading wallpaper");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        if(!(new File(Data.Environment.DIRECTORY_WALLPAPERS).exists())){
            new File(Data.Environment.DIRECTORY_WALLPAPERS).mkdirs();
        }
        request.setDestinationInExternalPublicDir(Data.Environment.DIRECTORY_WALLPAPERS, mTemp.getWallpaperName(path)+".jpg");
        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);
        return downloadReference;
    }
    public void showAndAdd(final Context mContext, PreferenceCategory mList) throws PackageManager.NameNotFoundException {
        new Build().setContext(this);
        PackageManager mManager = mContext.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = mManager.queryIntentActivities(intent, 0);
        for(int i = 0; i < list.size(); i++)
        {
            Preference app = new Preference(this);
            ApplicationInfo mInfo = mManager.getApplicationInfo(list.get(i).resolvePackageName, PackageManager.GET_META_DATA);
            Bundle appBundle = mInfo.metaData;
            String name = appBundle.getString("Softy.NAME");
            Build.PropReader.writeToProp("AppTemplate",name);
            Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
        }
    }
    public static class Getter extends as
    {

        public Getter showToast(Context mContext) throws PackageManager.NameNotFoundException {
            PackageManager mManager = mContext.getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> list = mManager.queryIntentActivities(intent, 0);
            for(int i = 0; i < list.size(); i++)
            {
                ApplicationInfo mInfo = mManager.getApplicationInfo(list.get(i).activityInfo.packageName, PackageManager.GET_META_DATA);
                Bundle appBundle = mInfo.metaData;
                String name = appBundle.getString("Softy.NAME");
                String summary = appBundle.getString("Softy,SUMMARY");
                //Required for newer versions installed,
                //Not needed for LEGACY versions
                //For versions using new code
                int version = appBundle.getInt("Softy.MIN_VERSION");
                if(version >= Build.getVersionCode())
                {
                    Toast.makeText(mContext, "Unsupported version, update Softy to install this template", Toast.LENGTH_SHORT);
                    return this;
                }

                int back = appBundle.getInt("Softy.BACKGROUND");
                int fore = appBundle.getInt("Softy.FOREGROUND");
                String theme = appBundle.getString("Softy.THEME");

                notify(name, "New app template for Softy");
            }
            return this;
        }

    }
}
