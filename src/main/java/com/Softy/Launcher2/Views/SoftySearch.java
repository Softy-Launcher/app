package com.Softy.Launcher2.Views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by mcom on 2/3/17.
 */

public class SoftySearch extends LinearLayout{
    public SoftySearch(Context context) {
        super(context);
        initSearch();
    }

    public SoftySearch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSearch();
    }

    public SoftySearch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSearch();
    }

    public SoftySearch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSearch();
    }

    private void initSearch(){

    }
}
