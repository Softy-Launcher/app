package com.Softy.Launcher2.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.Softy.Launcher2.Classes.Accounts;
import com.Softy.Launcher2.Classes.RandomMessage;
import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.R;
import com.Softy.Launcher2.Views.MessagePreference;
import com.Softy.Launcher2.Views.SwipeDownPreference;
import com.Softy.Launcher2.Views.SwipeUpPreference;
import com.Softy.Services.GoodHub.AccountManager;
import com.Softy.Services.GoodHub.GoodHub;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Admin on 5/6/2017.
 */

public class General extends PreferenceActivity implements Preference.OnPreferenceClickListener
{
    private static int REQUEST_PACKS = 65535;
    private BillingProcessor mProcessor;
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
        addPreferencesFromResource(R.xml.general);
        mProcessor = new BillingProcessor(this, Data.BASE_64, "8291-7473-1329", new BillingProcessor.IBillingHandler(){

            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                Toast.makeText(General.this, productId, Toast.LENGTH_SHORT).show();
                if(productId.equals("com.softy.pro_feature"))
                {
                    SharedPreferences mProPrefs = getSharedPreferences(Data.PRO_NAME, Context.MODE_PRIVATE);
                    SharedPreferences mAdsPrefs= getSharedPreferences(Data.ADS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor mProEditor = mProPrefs.edit();
                    SharedPreferences.Editor mAdsEditor = mAdsPrefs.edit();

                    mProEditor.putBoolean(Data.PRO_FEATURES, true).commit();
                    mAdsEditor.putBoolean(Data.FIRST_TIME_PAID, true).commit();
                    mAdsEditor.putBoolean(Data.HAS_PAID, true).commit();

                    Toast.makeText(General.this, "You're now a PRO user!", Toast.LENGTH_SHORT).show();
                    try
                    {
                        mProcessor.consumePurchase(productId);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPurchaseHistoryRestored() {
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                Toast.makeText(General.this, "An error happened. Error code: "+errorCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onBillingInitialized() {

            }
        });
        mProcessor.initialize();

        loadPrefs();

        ((Preference) findPreference("go_pro")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mProcessor.purchase(General.this, "com.softy.pro_feature");
                Toast.makeText(General.this, "Thank you for going pro.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        boolean hasNowPaid = getSharedPreferences(Data.ADS_NAME, Context.MODE_PRIVATE).getBoolean(Data.HAS_PAID, true);
        if(!hasNowPaid == true)
        {
            //Display message to go pro!
            mMessage.setMessage("Go PRO!");
            mMessage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    mProcessor.purchase(General.this, "com.softy.pro_feature");
                    Toast.makeText(General.this, "Thank you for going pro.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }else
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

    private void loadPrefs()
    {
        //Temporary housing
        mMessage = (MessagePreference) findPreference("message");
        mMessage.setMessage(RandomMessage.getRandomMessage());
        //((PreferenceScreen) findPreference("general_base")).removePreference(((MessagePreference) findPreference("message")));
        GoodHub.shouldOpen = true;
        final CheckBoxPreference dark = (CheckBoxPreference) findPreference("dark_theme");
        final CheckBoxPreference light = (CheckBoxPreference) findPreference("light_theme");

        dark.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putString(Data.TEMP_THEME, Data.DARK).commit();
                light.setChecked(false);
                Toast.makeText(getApplicationContext(), "Applied dark theme", Toast.LENGTH_SHORT);
                return false;
            }
        });
        light.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putString(Data.TEMP_THEME, Data.LIGHT).commit();
                dark.setChecked(false);
                Toast.makeText(getApplicationContext(), "Applied light theme", Toast.LENGTH_SHORT);
                return false;
            }
        });

        ((SwitchPreference) findPreference("qsb")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(preference instanceof  SwitchPreference)
                {
                    SwitchPreference mSwitch = (SwitchPreference) preference;
                    if((mSwitch.isChecked()))
                    {
                        getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putBoolean(Data.Qsb.QSB_ON, true).commit();
                        Toast.makeText(getApplicationContext(), "Showing Google Search Bar", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putBoolean(Data.Qsb.QSB_ON, false).commit();
                        Toast.makeText(getApplicationContext(), "Hiding Google Search Bar", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        ((Preference) findPreference("icon_pack")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_PICK_ACTIVITY);



                Intent anddoes = new Intent(Intent.ACTION_MAIN);
                anddoes.addCategory("com.anddoes.launcher.THEME");

                intent.putExtra(Intent.EXTRA_INTENT, anddoes);

                startActivityForResult(intent, REQUEST_PACKS);
                return false;
            }
        });

        ((Preference) findPreference("swipe_up")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try
                {
                    SwipeUpPreference.SwipeUpDialog up = new SwipeUpPreference.SwipeUpDialog(General.this);
                    up.show();
                }catch(Exception e)
                {
                    Toast.makeText(General.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        ((Preference) findPreference("swipe_down")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try
                {
                    SwipeDownPreference.SwipeDownDialog down = new SwipeDownPreference.SwipeDownDialog(General.this);
                    down.show();
                }catch(Exception e)
                {
                    Toast.makeText(General.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        ((Preference) findPreference("change")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent change = new Intent(General.this, GoodHub.class);
                startActivity(change);
                return false;
            }
        });

        ((Preference) findPreference("manage")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent manage = new Intent(General.this, AccountManager.class);
                startActivity(manage);
                return false;
            }
        });
        String user = new Accounts(this).getUser();
        String pass = new Accounts(this).getPass();

        ((Preference)findPreference("account")).setTitle(user);
        ((Preference)findPreference("account")).setSummary("Password: "+pass);
        ((Preference)findPreference("account")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences mSharedPrefs = getSharedPreferences(Data.NAME, MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                boolean isStoring = mSharedPrefs.getBoolean("store_data", true);
                if(isStoring)
                {
                    mEditor.putBoolean("store_data", false).commit();
                    Toast.makeText(General.this, "Not storing data onto GoodHub account", Toast.LENGTH_SHORT).show();
                }else
                {
                    mEditor.putBoolean("store_data", true).commit();
                    Toast.makeText(General.this, "Storing data onto GoodHub account", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    @Override
    public void onResume()
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
        super.onResume();
        String user = new Accounts(this).getUser();
        String pass = new Accounts(this).getPass();

        ((Preference)findPreference("account")).setTitle(user);
        ((Preference)findPreference("account")).setSummary("Password: "+pass);
        ((Preference)findPreference("account")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences mSharedPrefs = getSharedPreferences(Data.NAME, MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                boolean isStoring = mSharedPrefs.getBoolean("store_data", true);
                if(isStoring)
                {
                    mEditor.putBoolean("store_data", false).commit();
                    Toast.makeText(General.this, "Not storing data onto GoodHub account", Toast.LENGTH_SHORT).show();
                }else
                {
                    mEditor.putBoolean("store_data", true).commit();
                    Toast.makeText(General.this, "Storing data onto GoodHub account", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent main)
    {
        if(resultCode==RESULT_OK)
        {
            if(requestCode==REQUEST_PACKS)
            {
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putString("theme", main.getComponent().getPackageName()).commit();
                getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putBoolean("ic-pack-applied", true).commit();
                Intent restartHome = new Intent(Intent.ACTION_MAIN);
                restartHome.addCategory(Intent.CATEGORY_HOME);
                restartHome.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(restartHome);
            }
        }
        if(!mProcessor.handleActivityResult(requestCode, resultCode, main))
        {
            super.onActivityResult(requestCode, resultCode, main);
        }
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