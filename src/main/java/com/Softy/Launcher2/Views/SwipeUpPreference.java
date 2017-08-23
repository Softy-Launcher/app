package com.Softy.Launcher2.Views;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.R;

/**
 * Created by softy on 6/16/17.
 */

public class SwipeUpPreference extends Preference {

    public SwipeUpPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SwipeUpDialog mSwipeUpDialog = new SwipeUpDialog(getContext());
                mSwipeUpDialog.show();
                return false;
            }
        });
    }

    public SwipeUpPreference(Context context) {
        super(context);
    }

    public static class SwipeUpDialog extends Dialog
    {

        public SwipeUpDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        public void onCreate(Bundle bundle)
        {
            super.onCreate(bundle);
            setContentView(R.layout.swipe_up_dialog);
            SharedPreferences sharedPrefs = getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
            final SharedPreferences.Editor e = sharedPrefs.edit();
            Switch appsSelected = (Switch) findViewById(R.id.swipe_up_apps);
            appsSelected.setText("Open app drawer");
            Switch notifSelected = (Switch) findViewById(R.id.swipe_up_notif);
            notifSelected.setText("Open an app");

            appsSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        e.putString("swipe_up","open_apps").commit();
                        dismiss();
                    }else
                    {
                        e.putString("swipe_up","").commit();
                        dismiss();
                    }
                }
            });

            notifSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        e.putString("swipe_up", "");
                        dismiss();
                    }else
                    {
                        e.putString("swipe_down", "");
                        dismiss();
                    }
                }
            });
        }
    }
}
