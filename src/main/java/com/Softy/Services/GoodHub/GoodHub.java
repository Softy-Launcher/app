package com.Softy.Services.GoodHub;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.Softy.Launcher2.Classes.Accounts;
import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.Mini;
import com.Softy.Launcher2.R;
import com.Softy.Launcher2.Sprite;

/**
 * Created by Admin on 4/30/2017.
 */

public class GoodHub extends Activity implements AccountInterface{
    private static String userText;
    private static String passText;
    public static boolean shouldOpen  = false;
    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(shouldShowManager() == true)
        {
            showManager();
        }
        if(shouldOpen == true)
        {
            preLogin();
        }
    }

    @Override
    public boolean shouldShowManager() {
        return false;
    }

    @Override
    public void showManager() {

    }

    public void preLogin()
    {
        getAppContent(R.layout.main_goodhub_page);
    }

    protected void getAppContent(int content)
    {
        setContentView(content);

        final AutoCompleteTextView user = getEditor(R.id.acct_user);
        final AutoCompleteTextView pass = getEditor(R.id.acct_pass);


        Button skip = getButton(R.id.skip);
        Button sign_up = getButton(R.id.sign_up);
        Button sign_in = getButton(R.id.sign_in);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String[] nameOne = new String[]{"Turtles",
                            "Cats",
                            "Dogs"};

                    String[] nameTwo = new String[]{"Tier",
                            "Riot",
                            "Dame"};

                    int randomOne = ((int)(Math.random() * 2) + 0);
                    int randomTwo = ((int)(Math.random() * 2) + 0);
                    new Accounts(getApplicationContext()).makeUser(nameOne[randomOne] + " " + nameTwo[randomTwo])
                            .makePassword(nameOne[randomOne] + " " + nameTwo[randomTwo])
                            .build();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                afterLogin();
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ConfirmDialog dialog = new ConfirmDialog(GoodHub.this);
                if(user.getText().equals("") && pass.getText().equals(""))
                {
                    Snackbar.make(user, "Both user and password can't be empty!", Snackbar.LENGTH_SHORT).show();
                }else if(user.getText().equals("") && !(pass.getText().equals("")))
                {
                    Snackbar.make(user, "User can't be empty!", Snackbar.LENGTH_SHORT).show();
                }else if(!(user.getText().equals("")) && pass.getText().equals(""))
                {
                    Snackbar.make(user, "Pass can't be empty!", Snackbar.LENGTH_SHORT).show();
                }else{
                    if(getApplicationContext().getPackageName().equals("com.Softy.Launcher"))
                    {
                        //In all cases, Softy does not create the preference
                        SharedPreferences mProPrefs = getSharedPreferences(Data.PRO_NAME, Context.MODE_PRIVATE);
                        SharedPreferences mAdsPrefs = getSharedPreferences(Data.ADS_NAME, Context.MODE_PRIVATE);

                        mAdsPrefs.edit().putBoolean(Data.HAS_PAID, false).commit();
                        mAdsPrefs.edit().putBoolean(Data.FIRST_TIME_PAID, false).commit();
                    }
                    AlertDialog info = new AlertDialog.Builder(GoodHub.this)
                            .setTitle("Agree?")
                            .setMessage("By signing up, you allow us to store Softy's data onto the cloud")
                            .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface di, int which) {
                                    dialog.setUser(user.getText().toString());
                                    dialog.setPass(pass.getText().toString());
                                    dialog.show();
                                    userText = user.getText().toString();
                                    try {
                                        new Accounts(GoodHub.this).makeUser(userText).makePassword(passText).build();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("Just take me home", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getPackageManager().setComponentEnabledSetting(new ComponentName(GoodHub.this, Mini.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                                    getPackageManager().setComponentEnabledSetting(new ComponentName(GoodHub.this, Sprite.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                                    Intent main = new Intent(Intent.ACTION_MAIN);
                                    main.addCategory(Intent.CATEGORY_HOME);
                                    startActivity(main);
                                }
                            }).show();
                }
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialog dialog = new ConfirmDialog(GoodHub.this);
                if (user.getText().equals("") && pass.getText().equals("")) {
                    Snackbar.make(user, "Both user and password can't be empty!", Snackbar.LENGTH_SHORT).show();
                } else if (user.getText().equals("") && !(pass.getText().equals(""))) {
                    Snackbar.make(user, "User can't be empty!", Snackbar.LENGTH_SHORT).show();
                } else if (!(user.getText().equals("")) && pass.getText().equals("")) {
                    Snackbar.make(user, "Pass can't be empty!", Snackbar.LENGTH_SHORT).show();
                } else {
                    if(getApplicationContext().getPackageName().equals("com.Softy.Launcher"))
                    {
                        //In all cases, Softy does not create the preference
                        SharedPreferences mProPrefs = getSharedPreferences(Data.PRO_NAME, Context.MODE_PRIVATE);
                        SharedPreferences mAdsPrefs = getSharedPreferences(Data.ADS_NAME, Context.MODE_PRIVATE);

                        mAdsPrefs.edit().putBoolean(Data.HAS_PAID, false).commit();
                        mAdsPrefs.edit().putBoolean(Data.FIRST_TIME_PAID, false).commit();
                    }
                    AlertDialog info = new AlertDialog.Builder(GoodHub.this)
                            .setTitle("Agree?")
                            .setMessage("By signing in, you allow us to store Softy's data onto the cloud")
                            .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LoginTask loginTask = new LoginTask(getApplicationContext(), user.getText().toString(), pass.getText().toString());
                                    loginTask.execute();
                                    userText = user.getText().toString();
                                    passText = pass.getText().toString();
                                    try {
                                        new Accounts(GoodHub.this).makeUser(userText).makePassword(passText).build();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //for this app only
                                    getPackageManager().setComponentEnabledSetting(new ComponentName(GoodHub.this, Mini.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                                    getPackageManager().setComponentEnabledSetting(new ComponentName(GoodHub.this, Sprite.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                                    Intent main = new Intent(Intent.ACTION_MAIN);
                                    main.addCategory(Intent.CATEGORY_HOME);
                                    startActivity(main);
                                }
                            })
                            .setNegativeButton("Just take me home", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getPackageManager().setComponentEnabledSetting(new ComponentName(GoodHub.this, Mini.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                                    getPackageManager().setComponentEnabledSetting(new ComponentName(GoodHub.this, Sprite.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                                    Intent main = new Intent(Intent.ACTION_MAIN);
                                    main.addCategory(Intent.CATEGORY_HOME);
                                    startActivity(main);
                                }
                            })
                            .show();
                }
            }
        });
    }


    protected void afterLogin()
    {
        Log.i("LoginDone", "User has logged in");
    }

    private Button getButton(int contentId)
    {
        return (Button) findViewById(contentId);
    }

    private AutoCompleteTextView getEditor(int contentId)
    {
        return (AutoCompleteTextView) findViewById(contentId);
}

    public static String getUser()
    {
        return userText;
    }

    public static String getPass()
    {
        return passText;
    }

    public static void launchWhenOpen(boolean shouldOpen) {
        GoodHub.shouldOpen = shouldOpen;
    }
}
