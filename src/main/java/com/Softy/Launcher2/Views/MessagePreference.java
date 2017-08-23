package com.Softy.Launcher2.Views;

import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.Softy.Launcher2.R;


/**
 * Created by softy on 6/24/17.
 */

public class MessagePreference extends Preference {

    public void show(PreferenceScreen general_base) {
        general_base.addPreference(this);
    }

    private String mMessage = "";
    private Button messageView;
    private TextView mMessageToShow;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MessagePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MessagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MessagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MessagePreference(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(ViewGroup mParent)
    {
        super.onCreateView(mParent);
        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        messageView = (Button) mInflater.inflate(R.layout.settings_message_preference, mParent, false);
        messageView.setText(mMessage);
        return messageView;
    }

    public void setText(String text)
    {
        setMessage(text);
    }

    public void setMessage(String message)
    {
        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        messageView = (Button) mInflater.inflate(R.layout.settings_message_preference, null, false);
        if(mMessage == null || mMessage.equals(""))
        {
            mMessage = getContext().getResources().getString(R.string.title);
        }
        messageView.setText(message);
        this.mMessage = message;
    }

    public String getText()
    {
        return getMessage();
    }

    public String getMessage()
    {
        return mMessage;
    }

    public void dismiss(PreferenceScreen mainScreen)
    {
        if(messageView != null)
        {
            if(mMessageToShow != null)
            {
                mainScreen.removePreference(this);
            }
        }
    }
}