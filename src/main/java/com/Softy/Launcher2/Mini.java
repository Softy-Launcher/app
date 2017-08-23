package com.Softy.Launcher2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.Softy.Launcher2.Classes.Build;
import com.Softy.Launcher2.Classes.Version;
import com.Softy.Launcher2.Receivers.a;
import com.Softy.Launcher2.Services.as;
import com.Softy.Services.GoodHub.LoginTask;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.android.launcher3.AppsCustomizePagedView;
import com.android.launcher3.Launcher;

public class Mini extends Launcher {
    private a a;
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);

        try {

            doLaunch(b);

            Build.Hardware.writeHardware(this);
            //addAnim();
            Toast.makeText(this,LoginTask.getCookieValue("user", "https://goodhub.000webhostapp.com/home.php"),Toast.LENGTH_SHORT);
            if(shouldPreviewApps == true)
            {
                showAllApps(true, AppsCustomizePagedView.ContentType.Applications, true);
            }

            Toast.makeText(this, "Please give me a few as I load. Continue once I disappear", Toast.LENGTH_SHORT).show();


            SharedPreferences mAdPrefs = getSharedPreferences(Data.ADS_NAME, Context.MODE_PRIVATE);
            boolean hasUserPaid = mAdPrefs.getBoolean(Data.HAS_PAID,  true);
            if(!hasUserPaid)
            {
                getRandomAd();
            }else
            {
                if(Version.isNewVersion(this))
                {
                    Toast.makeText(this, "A new version is out right now!", Toast.LENGTH_SHORT).show();
                }
            }
            if(getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getBoolean("is_new",false) == true)
            {
                if(getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getBoolean("applied_color", true) == true)
                {
                    Intent i = new Intent("softy.intent.action.APPLY_LAUNCHER_SETTINGS");
                    i.setPackage(this.getPackageName());
                    startService(i);

                    Intent is = new Intent(this, as.class);
                    startService(is);

                    IntentFilter filter = new IntentFilter();
                    filter.addAction("softy.intent.action.START_APPLY_LAUNCHER_SETTINGS");

                    registerReceiver(new a(), filter);
                }
            }
        }catch(NullPointerException e)
        {
            Log.e("UnknownError", e.getMessage());
            Toast.makeText(this, "An error occurred. ", Toast.LENGTH_SHORT).show();
            onCreate(b);
        }
        getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putBoolean("is_new", false).commit();
    }

    public void addAnim()
    {
        CustomContentCallbacks ccc = new CustomContentCallbacks() {
            @Override
            public void onShow(boolean fromResume) {
                Toast.makeText(Mini.this, "Showing animation", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHide() {

            }

            @Override
            public void onScrollProgressChanged(float progress) {

            }

            @Override
            public boolean isScrollingAllowed() {
                return false;
            }
        };
        addToCustomContentPage((RelativeLayout) getLayoutInflater().inflate(R.layout.sprite_animation, null), ccc, "Sprite");
    }

    private static boolean shouldPreviewApps = false;
    public static boolean previewApps(boolean shouldPreview)
    {
        Mini.shouldPreviewApps = shouldPreview;
        return shouldPreview;
    }
    @Override
    public void onResume()
    {
        super.onResume();

        //write hardware information for debugging purposes
        Build.Hardware.writeHardware(this);

        Intent i = new Intent("softy.intent.action.APPLY_LAUNCHER_SETTINGS");
        i.setPackage(this.getPackageName());
        startService(i);

        IntentFilter filter = new IntentFilter();
        filter.addAction("softy.intent.action.START_APPLY_LAUNCHER_SETTINGS");

        registerReceiver(new a(), filter);
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
        return ((int)(Math.random() * 2000) + 0);
    }
}
