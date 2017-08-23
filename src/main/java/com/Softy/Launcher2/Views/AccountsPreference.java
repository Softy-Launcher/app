package com.Softy.Launcher2.Views;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;

/**
 * Created by mcom on 3/11/17.
 */

public class AccountsPreference extends PreferenceCategory{
    public AccountsPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        addAccounts();
    }

    public AccountsPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addAccounts();
    }

    public AccountsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        addAccounts();
    }

    public AccountsPreference(Context context) {
        super(context);
        addAccounts();
    }

    public void addAccounts(){
        AccountManager mManager = AccountManager.get(getContext());
        Account[] mAccounts = mManager.getAccounts();
        for(final Account a : mAccounts){
            Preference mPref = new Preference(getContext());
            mPref.setTitle(a.name);
            mPref.setSummary(a.type);
            mPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences mShared = getContext().getSharedPreferences("accounts.info", Context.MODE_PRIVATE);
                    mShared.edit().putString("account.name", a.name).commit();
                    mShared.edit().putString("account.type", a.type).commit();
                    return false;
                }
            });
            addPreference(mPref);
        }
    }
}
