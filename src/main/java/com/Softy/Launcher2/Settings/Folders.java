package com.Softy.Launcher2.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.Softy.Launcher2.Classes.RandomMessage;
import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.Mini;
import com.Softy.Launcher2.Receivers.c;
import com.Softy.Launcher2.Views.MessagePreference;
import com.Softy.Launcher2.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jrummyapps.android.colorpicker.ColorPreference;

import java.io.File;

/**
 * Created by softy on 7/22/17.
 */

public class Folders extends PreferenceActivity implements Preference.OnPreferenceClickListener {
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
        addPreferencesFromResource(R.xml.folder_settings);
        mMessage = (MessagePreference) findPreference("message");
        randomMessage(mMessage);

        ((ColorPreference) findPreference("folder_back")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putBoolean("applied_color",true).commit();
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putInt(Data.Folder.FOLDER_BACKGROUND, (Integer) newValue).commit();
                return false;
            }
        });

        ((EditTextPreference) findPreference("folder_text")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putString(Data.Folder.FOLDER_HINT, (String) newValue).commit();
                return false;
            }
        });

        ((EditTextPreference) findPreference("max")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putInt(Data.Folder.MAX_APPS, (int) newValue).commit();
                return false;
            }
        });

        SharedPreferences mAdPrefs = getSharedPreferences(Data.ADS_NAME, Context.MODE_PRIVATE);
        boolean hasUserPaid = mAdPrefs.getBoolean(Data.HAS_PAID,  true);
        boolean isGooglePlay = getSharedPreferences(Data.INSTALLER, Context.MODE_PRIVATE).getBoolean("downloadedFromPlay", true);
        if(!hasUserPaid || !isGooglePlay)
        {
            getRandomAd();
        }else
        {
            PreferenceCategory mAbout = (PreferenceCategory) findPreference("about");
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("softy.intent.action.CHANGE_SUGGESTION");

        registerReceiver(new c(mMessage), filter);
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
                    Toast.makeText(Folders.this, "Cleared all app data. Restarting in 4 seconds.", Toast.LENGTH_SHORT).show();
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(Folders.this, Mini.class);
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
        return ((int)(Math.random() * 4) + 0);
    }
}