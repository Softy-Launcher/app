package com.Softy.Services.GoodHub;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.Softy.Launcher2.R;

/**
 * Created by softy on 6/22/17.
 */

public class AccountManager extends GoodHub{
    @Override
    public void onCreate(Bundle savedState)
    {
        super.onCreate(savedState);
        showManager();
    }
    @Override
    public boolean shouldShowManager()
    {
        return true;
    }

    @Override
    public void showManager()
    {
        //Show the account manager activity
        setContentView(R.layout.goodhub_account_manager);

        final AutoCompleteTextView user = (AutoCompleteTextView) findViewById(R.id.acct_manage_user);
        final AutoCompleteTextView email = (AutoCompleteTextView) findViewById(R.id.acct_manage_email);
        final AutoCompleteTextView pass = (AutoCompleteTextView) findViewById(R.id.acct_manage_pass);

        Button cancel = (Button) findViewById(R.id.cancel_manage);
        Button confirm = (Button) findViewById(R.id.confirm_manage);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancelling the change
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = user.getText().toString();
                String mail = email.getText().toString();
                String pass = user.getText().toString();
                ManageAccount mAccount = new ManageAccount(AccountManager.this, name, mail, pass);
                mAccount.execute();
                //finish();
            }
        });
    }
}
