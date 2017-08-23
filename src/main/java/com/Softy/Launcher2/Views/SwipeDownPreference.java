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

public class SwipeDownPreference extends Preference {

    public SwipeDownPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SwipeDownDialog mSwipeDownDialog = new SwipeDownDialog(getContext());
                mSwipeDownDialog.show();
                return false;
            }
        });
    }

    public SwipeDownPreference(Context context) {
        super(context);
    }

    public static class SwipeDownDialog extends Dialog
    {

        public SwipeDownDialog(@NonNull Context context) {
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
            Switch notifSelected = (Switch) findViewById(R.id.swipe_up_notif);

            appsSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        e.putString("swipe_down","launch_app").commit();
                        dismiss();
                    }else
                    {
                        e.putString("swipe_down","").commit();
                        dismiss();
                    }
                }
            });

            notifSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        e.putString("swipe_down", "open_notif");
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
