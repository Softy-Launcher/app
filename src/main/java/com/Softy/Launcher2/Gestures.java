package com.Softy.Launcher2;

import android.content.Context;
import android.view.View;

/**
 * Created by mcom on 2/26/17.
 */

public class Gestures {
    private Context mContext;
    private View mView;
    private View[] mList;
    public Gestures set(Context mContext, View mView){
        this.mContext = mContext;
        this.mView = mView;
        return this;
    }

    public Gestures set(Context mContext){
        this.mContext = mContext;
        return this;
    }

    public void enableView(){
        mView.setEnabled(true);
    }

    public void enableView(View mView){
        this.mView = mView;
        mView.setEnabled(true);
    }

    public void disableView(){
        mView.setEnabled(false);
    }

    public void disableView(View mView){
        this.mView = mView;
        mView.setEnabled(false);
    }

    public String getProPackage(){
        return "com.Softy.Launcher.MiniUI.PRO";
    }
}