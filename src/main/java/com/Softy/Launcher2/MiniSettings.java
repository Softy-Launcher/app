package com.Softy.Launcher2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.Softy.Launcher2.Classes.RandomMessage;
import com.Softy.Launcher2.R;
import com.Softy.Launcher2.Receivers.c;
import com.Softy.Launcher2.Settings.Drawer;
import com.Softy.Launcher2.Settings.Folders;
import com.Softy.Launcher2.Settings.General;
import com.Softy.Launcher2.Settings.Workspace;
import com.Softy.Launcher2.Views.MessagePreference;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;

/**
 * Created by mcom on 1/22/17.
 */

public class MiniSettings extends PreferenceActivity implements Preference.OnPreferenceClickListener {
    private static AppCompatDelegate acd;

    private static MessagePreference mMessage;

    public static MessagePreference getMessagePreference() {
        return mMessage;
    }

    @Override
    public void onCreate(Bundle bundle)
    {
        final SharedPreferences sharedPrefs = getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        String theme = sharedPrefs.getString(Data.TEMP_THEME, "");
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
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.settings);
        mMessage = (MessagePreference) findPreference("message");
        if(isDefaultLauncher())
            randomMessage(mMessage);
        else
        {
            mMessage.setMessage("Please set Comely as your default launcher");
            mMessage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //Enable sprite
                    PackageManager mManager = MiniSettings.this.getPackageManager();
                    ComponentName mName = new ComponentName(MiniSettings.this, Sprite.class);
                    mManager.setComponentEnabledSetting(mName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                    Intent spriteIntent = new Intent(Intent.ACTION_MAIN);
                    spriteIntent.addCategory(Intent.CATEGORY_HOME);
                    spriteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(spriteIntent);

                    mManager.setComponentEnabledSetting(mName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    return false;
                }
            });
        }
        SharedPreferences mAdPrefs = getSharedPreferences(Data.ADS_NAME, Context.MODE_PRIVATE);
        boolean hasUserPaid = mAdPrefs.getBoolean(Data.HAS_PAID,  true);
        if(!hasUserPaid)
        {
            getRandomAd();
        }else
        {
            PreferenceCategory mAbout = (PreferenceCategory) findPreference("about");
        }
        ((Preference) findPreference("workspace")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                launch(Workspace.class);
                return false;
            }
        });

        setPreferenceColor((Preference)findPreference("workspace"));

        ((Preference) findPreference("general")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                launch(General.class);
                return false;
            }
        });

        ((Preference) findPreference("drawer")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                launch(Drawer.class);
                return false;
            }
        });

        ((Preference) findPreference("folders")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                launch(Folders.class);
                return false;
            }
        });

        ((SwitchPreference) findPreference("dock")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(preference instanceof SwitchPreference)
                {
                    SwitchPreference mSwitch = (SwitchPreference) preference;
                    if(!(mSwitch.isChecked()))
                    {
                        sharedPrefs.edit().putBoolean(Data.Dock.DOCK_ON, false).commit();
                        Toast.makeText(getApplicationContext(), "Hiding dock", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        sharedPrefs.edit().putBoolean(Data.Dock.DOCK_ON, true).commit();
                        Toast.makeText(getApplicationContext(), "Showing dock", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        ((Preference) findPreference("template")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent main = new Intent(MiniSettings.this, Template.class);
                startActivity(main);
                return false;
            }
        });

        ((Preference) findPreference("version")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int version = info.versionCode;
                    String name = info.versionName;
                    Toast.makeText(MiniSettings.this, "Version: "+version+"\n Code Name: "+name, Toast.LENGTH_SHORT).show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
        ((Preference) findPreference("backup_settings")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new File("/sdcard/Softy/backups").mkdirs();
                Data.exportPreferences();
                Toast.makeText(MiniSettings.this, "Backed up settings to /sdcard/Softy/backups/data.xml", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        ((Preference) findPreference("import_settings")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(MiniSettings.this, "Your preferences file should be called data.xml and should be in /sdcard/Softy/backups", Toast.LENGTH_LONG).show();
                new File("/sdcard/Softy/backups").mkdirs();
                Data.importPreferences();
                return false;
            }
        });

        ((Preference) findPreference("default_launcher")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Enable sprite
                PackageManager mManager = MiniSettings.this.getPackageManager();
                ComponentName mName = new ComponentName(MiniSettings.this, Sprite.class);
                mManager.setComponentEnabledSetting(mName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                Intent spriteIntent = new Intent(Intent.ACTION_MAIN);
                spriteIntent.addCategory(Intent.CATEGORY_HOME);
                spriteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(spriteIntent);

                mManager.setComponentEnabledSetting(mName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                return false;
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("softy.intent.action.CHANGE_SUGGESTION");

        registerReceiver(new c(mMessage), filter);
    }

    boolean isDefaultLauncher()
    {
        //Run a check to see if the current home screen is comely. If not, we'll warn.
        PackageManager localManager = getPackageManager();
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        String packageName = localManager.resolveActivity(launcherIntent, PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        if(packageName.equals(getPackageName()))
            return true;
        else
            return false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //No escape
    }

    private void randomMessage(final MessagePreference mMessage) {
        mMessage.setMessage(RandomMessage.getRandomMessage());
    }

    private void addDeveloperMenu(PreferenceCategory mDevOps) {
        Preference clear = new Preference(this);
        clear.setTitle("Clear app data");
        clear.setSummary("Clear all app data");

        clear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new File("/data/data/"+getPackageName()+"/shared_prefs").delete();
                if(delete("/sdcard/Softy") == true)
                {
                    Toast.makeText(MiniSettings.this, "Cleared all app data. Restarting in 4 seconds.", Toast.LENGTH_SHORT).show();
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(MiniSettings.this, Mini.class);
                            startActivity(i);
                            finish();
                        }
                    }, 4000);
                }
                return false;
            }
        });

        mDevOps.addPreference(clear);
    }
    public boolean delete(String path)
    {
        boolean isClear = false;
        File main = new File(path);
        File[] list = main.listFiles();
        for(File f : list)
        {
            if(f.isFile())
            {
                f.delete();
            }else
            {
                delete(f.getPath());
            }
        }
        if(new File(path).exists())
            isClear = true;

        return isClear;
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void launch(Class myActivity) {
        Intent i = new Intent(this, myActivity);
        startActivity(i);
    }

    public void setToolbar(@Nullable Toolbar toolbar)
    {
        get().setSupportActionBar(toolbar);
    }

    public AppCompatDelegate get()
    {
        if(acd == null)
        {
            acd = AppCompatDelegate.create(this, null);
        }
        return acd;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }
    private static final int INIT_LEVEL = 1;
    private int currentLevel;
    private InterstitialAd mAd;

    public void initialize()
    {
        currentLevel = INIT_LEVEL;
        mAd = newAd();
        load();
        //showAd();
    }

    public InterstitialAd newAd()
    {
        InterstitialAd mNewAd = new InterstitialAd(this);
        mNewAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mNewAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.i("AdLoaded", "Loaded ad");
                showAd();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        load();
                    }
                }, 5000);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.i("AdFailed", "Error code: "+errorCode+".");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        load();
                    }
                }, 5000);
            }

            @Override
            public void onAdClosed() {
                // Do not proceed to the next level ad yet
                goToNextLevel();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        load();
                    }
                }, 5000);
            }
        });
        return mNewAd;
    }

    public void showAd()
    {
        //And then he said 'Let there be ads!'
        if (mAd != null && mAd.isLoaded()) {
            mAd.show();
        } else {
            goToNextLevel();
        }
    }

    public void load()
    {
        AdRequest mReq = new AdRequest.Builder()

                .setRequestAgent("android_studio:ad_template").build();
        mAd.loadAd(mReq);
    }

    public void goToNextLevel()
    {
        mAd = newAd();
        Handler mDelay = new Handler();
        int randomKey = generateRandomKey();
        if((randomKey % 2) == 0)
        {
            mDelay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    load();
                }
            }, 2000);
            mDelay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAd();
                }
            }, 2000);
        }
    }

    public void getRandomAd() {
        int randomKey = generateRandomKey();
        if((randomKey%2) == 0)
        {
            initialize();
        }else
        {
            //Do not show ads
        }
    }

    private int generateRandomKey() {
        return ((int)(Math.random() * 20) + 0);
    }

    public void setPreferenceColor(Preference preference) {
        //Set using Styles.xml
    }
}
