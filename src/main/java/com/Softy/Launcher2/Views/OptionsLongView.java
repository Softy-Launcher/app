package com.Softy.Launcher2.Views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

import com.Softy.Launcher2.Data;
import com.Softy.Launcher2.R;

/**
 * Created by mcom on 2/8/17.
 */

public class OptionsLongView extends Button{
    public OptionsLongView(Context context) {
        super(context);
        doView();
    }

    public OptionsLongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        doView();
    }

    public OptionsLongView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        doView();
    }

    public OptionsLongView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        doView();
    }

    public void doView(){
        SharedPreferences sharedPrefs = (SharedPreferences) getContext().getSharedPreferences(Data.NAME, Context.MODE_PRIVATE);
        String theme = sharedPrefs.getString(Data.TEMP_THEME, "");
        switch(theme){
            case Data.DARK:
                setBackground(getContext().getResources().getDrawable(R.drawable.round_background_dark));
                setTextColor(Color.WHITE);
                break;
            case Data.LIGHT:
                setBackground(getContext().getResources().getDrawable(R.drawable.round_background_light));
                setTextColor(Color.BLACK);
                break;
        }
    }


}
