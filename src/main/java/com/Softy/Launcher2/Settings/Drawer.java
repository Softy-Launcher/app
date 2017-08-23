package com.Softy.Launcher2.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.Softy.Launcher2.Classes.RandomMessage;
import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.R;
import com.Softy.Launcher2.Views.MessagePreference;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jrummyapps.android.colorpicker.ColorPreference;

/**
 * Created by Admin on 5/6/2017.
 */
public class Drawer extends PreferenceActivity implements Preference.OnPreferenceClickListener
{
    private static MessagePreference mMessage;

    public static MessagePreference getMessagePreference() {
        return mMessage;
    }

    @Override
    public void onCreate(Bundle bundle)
    {
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
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.drawer);
        mMessage = (MessagePreference) findPreference("message");
        mMessage.setMessage(RandomMessage.getRandomMessage());
         ((ColorPreference) findPreference("background")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putBoolean("applied_color",true).commit();
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putInt(Data.Drawer.DRAWER_BACKGROUND, (Integer) newValue).commit();
                return false;
            }
        });

        ((ColorPreference) findPreference("foreground")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putBoolean("applied_color",true).commit();
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putInt(Data.Drawer.DRAWER_FOREGROUND, (Integer) newValue).commit();
                return false;
            }
        });


        SharedPreferences mAdPrefs = getSharedPreferences(Data.ADS_NAME, Context.MODE_PRIVATE);
        boolean hasUserPaid = mAdPrefs.getBoolean(Data.HAS_PAID,  true);
        if(!hasUserPaid)
        {
            getRandomAd();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //No escape
    }

    public void setToolbar(@Nullable Toolbar toolbar)
    {
        get().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate acd;
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
}
