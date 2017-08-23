package com.Softy.Launcher2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.Softy.Launcher2.Classes.Blurify;
import com.Softy.Launcher2.Classes.LockUtils;
import com.Softy.Launcher2.R;
import com.Softy.Launcher2.Services.LockerService;

public class LockScreen extends AppCompatActivity {
    private SharedPreferences sharedPrefs;
    private EditText user;
    private EditText pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        Intent service = new Intent(this, LockerService.class);
        startService(service);
        setContentView(R.layout.activity_lock_screen);

        final LockUtils mLockUtils = new LockUtils();
        mLockUtils.lock(LockScreen.this);

        Bitmap bitmap = ((BitmapDrawable) getWallpaper()).getBitmap();
        ImageView mImageView = (ImageView) findViewById(R.id.iv);

        Blurify.init(this);
        Bitmap src = Blurify.blur(bitmap, 2);

        mImageView.setImageBitmap(src);

        user = (EditText) findViewById(R.id.user_name);
        pass = (EditText) findViewById(R.id.pass_word);

        user.setImeOptions(EditorInfo.IME_ACTION_DONE);
        pass.setImeOptions(EditorInfo.IME_ACTION_DONE);

        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String storedUser = sharedPrefs.getString(Data.USER_NAME, "");
                String storedPass = sharedPrefs.getString(Data.PASS_WORD, "");
                String inputUser = user.getText().toString();
                String inputPass = pass.getText().toString();

                if(inputUser.equals(storedUser)){
                    if(inputPass.equals(storedPass)){
                        mLockUtils.unlock();
                        finish();
                    }else{
                        pass.setTextColor(Color.RED);
                        pass.setText("Incorrect pass");
                    }
                }else{
                    user.setTextColor(Color.RED);
                    user.setText("Incorrect user");
                }
                return false;
            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass.setText("");
                pass.setTextColor(Color.WHITE);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setText("");
                user.setTextColor(Color.WHITE);
            }
        });
    }
}
