package com.Softy.Launcher2.Views;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Softy.Launcher2.Loader;
import com.Softy.Launcher2.R;

/**
 * Created by mcom on 1/22/17.
 */

public class SpriteLoader extends RelativeLayout implements Loader {
    private static boolean shouldAnimate;
    private static Loader l;
    private Transition t;
    public static void setLoader(Loader l){
        SpriteLoader.l = l;
    }
    public SpriteLoader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(shouldAnimate){
            //animate sprite
            TextView tv = (TextView) findViewById(R.id.text_sprite_name);
            ProgressBar pb = (ProgressBar) findViewById(R.id.apps_customize_progress_bar);

            final LinearLayout tvg = (LinearLayout) findViewById(R.id.text_sprite_group);
            final LinearLayout pbg = (LinearLayout) findViewById(R.id.progress_sprite_group);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                final Fade in = new Fade(Fade.IN);
                final Fade out = new Fade(Fade.OUT);

                setTransition(out);

                TransitionManager.beginDelayedTransition(tvg, in);
                TransitionManager.beginDelayedTransition(pbg, in);

                Handler h = new Handler();
                h.postDelayed(new Runnable(){

                    @Override
                    public void run() {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(pbg, out);
                            TransitionManager.beginDelayedTransition(tvg, out);
                        }
                    }
                }, 6000);
            }else{
                //noop
            }
        }else{
            //do not animate sprite
        }
    }

    @Override
    public void setTransition(Transition t) {
        this.t = t;
    }

    @Override
    public Transition getTransition() {
        return t;
    }


    public static void shouldAnimateSprite(boolean shouldAnimate){
        SpriteLoader.shouldAnimate = shouldAnimate;
    }
}
