package com.Softy.Launcher2.Views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.Softy.Launcher2.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by softy on 6/24/17.
 */

public class AdsPreference extends LinearLayout
{

    public AdsPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        adAds();
    }

    public AdsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        adAds();
    }

    public AdsPreference(Context context) {
        super(context);
        adAds();
    }

    private void adAds()
    {
        View view  = onCreateView();
        this.addView(view);
    }

    protected View onCreateView()
    {
        //Create a view for ads
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AdView mCustomAdView = (AdView) li.inflate(R.layout.banner_ad_preference, null, false);

        Activity mActivity = (Activity) getContext();

        //Create an ad request
        AdRequest mRequest = new AdRequest.Builder().addTestDevice("").setRequestAgent("android_studio:ad_template").build();
        mCustomAdView.loadAd(mRequest);
        return mCustomAdView;
    }
}
