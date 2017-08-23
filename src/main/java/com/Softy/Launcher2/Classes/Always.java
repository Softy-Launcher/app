package com.Softy.Launcher2.Classes;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;

import java.util.Locale;

/**
 * Created by mcom on 3/2/17.
 */

public class Always{
    private static Activity mActivity;
    public static Always set(Activity mActivity){
        Always a = new Always();
        Always.mActivity = mActivity;
        return a;
    }

    public void startRecognition(String text){
        Intent main = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        main.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        main.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        main.putExtra(RecognizerIntent.EXTRA_PROMPT, text);
        try{
            mActivity.startActivityForResult(main, 10);
        }catch(ActivityNotFoundException anfe){
            anfe.printStackTrace();
        }
    }
}
