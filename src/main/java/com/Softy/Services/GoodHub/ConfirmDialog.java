package com.Softy.Services.GoodHub;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.R;

/**
 * Created by Admin on 4/30/2017.
 */
public class ConfirmDialog extends Dialog {
    private static String user;
    private Context mContext;
    public ConfirmDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public static void setUser(String user)
    {
        ConfirmDialog.user = user;
    }
    private static String pass;
    public static void setPass(String pass)
    {
        ConfirmDialog.pass = pass;
    }

    @Override
    public void onCreate(Bundle b)
    {
        setContentView(R.layout.email_dialog);
        SharedPreferences mSharedPrefs = getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor e = mSharedPrefs.edit();
        final AutoCompleteTextView verify = (AutoCompleteTextView) findViewById(R.id.acct_verify);
        final AutoCompleteTextView email = (AutoCompleteTextView) findViewById(R.id.acct_email);
        Button finish = (Button) findViewById(R.id.finish);

        finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(email.getText().equals("") && verify.getText().equals(""))
                {
                    Snackbar.make(v, "Both can not be empty!", Snackbar.LENGTH_SHORT).show();
                }else if(email.getText().equals("")&&!(verify.getText().equals("")))
                {
                    Snackbar.make(v, "Email can not be empty!", Snackbar.LENGTH_SHORT).show();
                }else if(!(email.getText().equals("")) && verify.equals(""))
                {
                    Snackbar.make(v, "Verify can not be empty!", Snackbar.LENGTH_SHORT).show();
                }else{
                    e.putString(Data.GoodHub.USER, user).commit();
                    e.putString(Data.GoodHub.EMAIL, email.getText().toString()).commit();
                    e.putString(Data.GoodHub.PASS, pass).commit();

                    RegisterTask registerTask = new RegisterTask(mContext, email.getText().toString(), user, pass, verify.getText().toString());
                    registerTask.execute();
                    dismiss();
                }
            }
        });
        setCanceledOnTouchOutside(true);
    }
}