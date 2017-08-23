package com.Softy.Launcher2;

import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.Softy.Launcher2.Classes.Blurify;
import com.Softy.Launcher2.R;
import com.Softy.Launcher2.Services.LockerService;

/**
 * Created by mcom on 1/29/17.
 */

public class UserDialog extends Dialog {
    public UserDialog(@NonNull Context context) {
        super(context);
    }

    public UserDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected UserDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    private SharedPreferences sharedPrefs;
    private EditText user;
    private EditText pass;
    private EditText pass_verify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_lock_screen);
        //enable the system lock receiver
        PackageManager mPackageManager = getContext().getPackageManager();
        mPackageManager.setComponentEnabledSetting(new ComponentName(getContext(), Locker.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);


        //Disable lock screen permanently
        KeyguardManager mKeyguardManager = (KeyguardManager) getContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock("IN");
        mKeyguardLock.disableKeyguard();
        Intent service = new Intent(getContext(), LockerService.class);
        getContext().startService(service);
        Bitmap bitmap = ((BitmapDrawable) getContext().getWallpaper()).getBitmap();
        ImageView mImageView = (ImageView) findViewById(R.id.iv);

        Blurify.init(getContext());
        Bitmap src = Blurify.blur(bitmap, 10);

        mImageView.setImageBitmap(src);

        user = (EditText) findViewById(R.id.user_name);
        pass = (EditText) findViewById(R.id.pass_word);
        pass_verify = (EditText) findViewById(R.id.verify);
        pass_verify.setVisibility(View.VISIBLE);

        user.setImeOptions(EditorInfo.IME_ACTION_DONE);
        pass.setImeOptions(EditorInfo.IME_ACTION_DONE);
        pass_verify.setImeOptions(EditorInfo.IME_ACTION_DONE);


        pass_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass_verify.setText("");
                pass_verify.setTextColor(Color.WHITE);
            }
        });

        user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sharedPrefs.edit().putString(Data.USER_NAME, user.getText().toString()).commit();
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sharedPrefs.edit().putString(Data.TEMP_PASS, pass.getText().toString()).commit();
            }
        });

        pass_verify.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String password = pass.getText().toString();
                String temp_pass = sharedPrefs.getString(Data.TEMP_PASS, "");
                if(password.equals(temp_pass)){
                    sharedPrefs.edit().remove(Data.TEMP_PASS);
                    sharedPrefs.edit().putString(Data.PASS_WORD, password).commit();
                    dismiss();
                }else{
                    pass_verify.setText("The passwords are not the same.");
                    pass_verify.setTextColor(Color.RED);
                }
                return false;
            }
        });
    }
}
