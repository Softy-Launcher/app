package com.Softy.Launcher2.Classes;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import com.Softy.Launcher2.Data;



/**
 * Created by softy on 6/19/17.
 */

public class Accounts extends ContextWrapper
{
    private static SharedPreferences mSharedPrefs;
    private static String mUser = "froggie-style-pepe";
    private static String mPass = "abcgold13";
    public static String USER = "stored-info";
    public static String PASS = "stored-data";
    public Accounts(Context base) {
        super(base);
        mSharedPrefs = getSharedPreferences(Data.NAME, MODE_PRIVATE);
    }

    public Accounts makeUser(String user)
    {
        mUser = user;
        return this;
    }

    public Accounts makePassword(String pass)
    {
        mPass = pass;
        return this;
    }

    public Accounts build() throws Exception {
        //Store the information

        //Store the username
        mSharedPrefs.edit().putString(Data.GoodHub.USER, mUser).commit();

        //Store the password
        mSharedPrefs.edit().putString(Data.GoodHub.PASS, mPass).commit();
        return this;
    }

    public String getUser()
    {
        return mSharedPrefs.getString(Data.GoodHub.USER, "");
    }

    public String getPass()
    {
        return mSharedPrefs.getString(Data.GoodHub.PASS, "");
    }
}