package com.Softy.Launcher2.Icons;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.R;

/**
 * Created by softy on 7/6/17.
 */

public class IconPicker extends Activity {

    @Override
    public void onCreate(Bundle mInstance)
    {
        super.onCreate(mInstance);
        setContentView(R.layout.icon_picker_activity);

        SharedPreferences mPrefs = getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor e = mPrefs.edit();

        Intent mainIntent = getIntent();
        ComponentName mName = mainIntent.getComponent();
        final String callerPackageName = getApplicationPackage(mainIntent);
        String callerApplicationName = getApplicationName(callerPackageName);
        Drawable callerApplicationIcon = getApplicationIcon(callerPackageName);

        TextView mIconText = (TextView) findViewById(R.id.icon_pack_name);
        TextView mIconPack = (TextView) findViewById(R.id.icon_pack_package);
        ImageView mIconImage = (ImageView) findViewById(R.id.icon_pack_icon);
        Button mIconApply = (Button) findViewById(R.id.icon_pack_apply);

        mIconText.setText(callerApplicationName);
        mIconPack.setText(callerApplicationName);
        mIconImage.setBackground(callerApplicationIcon);

        mIconApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.putString("theme", callerPackageName);
                e.commit();
            }
        });
    }

    private String getApplicationPackage(Intent mainIntent) {
        try
        {
            return mainIntent.getComponent().getPackageName();
        }catch(Exception e)
        {
            e.printStackTrace();
            return getPackageName();
        }
    }

    private String getApplicationName(String callerPackageName) {
        PackageManager mManager = getPackageManager();
        ApplicationInfo mInfo;
        try
        {
            mInfo = mManager.getApplicationInfo(callerPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getApplicationInfo().name;
        }
        return (String)(mInfo != null ? mManager.getApplicationLabel(mInfo) : "(unknown)");
    }

    private Drawable getApplicationIcon(String callerPackageName) {
        try {
            return getPackageManager().getApplicationIcon(callerPackageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getResources().getDrawable(R.mipmap.ic_launcher);
        }
    }
}
