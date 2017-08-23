package com.Softy.Launcher2.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by mcom on 3/29/17.
 */

public class BlockSearch extends EditText implements View.OnClickListener{
    public BlockSearch(Context context) {
        super(context);
    }

    public BlockSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockSearch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BlockSearch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onClick(View v) {

    }
    /**public BlockSearch(Context context) {
        super(context);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Block(getContext(), (BlockList) findViewById(R.id.app_tab_host))
                        .searchBlockList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                new Block(getContext(), (BlockList) findViewById(R.id.app_tab_host))
                        .searchBlockList(s.toString());
            }
        });
    }

    public BlockSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Block(getContext(), (BlockList) findViewById(R.id.app_tab_host))
                        .searchBlockList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                new Block(getContext(), (BlockList) findViewById(R.id.app_tab_host))
                        .searchBlockList(s.toString());
            }
        });
    }

    public BlockSearch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Block(getContext(), (BlockList) findViewById(R.id.app_tab_host))
                        .searchBlockList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                new Block(getContext(), (BlockList) findViewById(R.id.app_tab_host))
                        .searchBlockList(s.toString());
            }
        });
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.search_blocks){
            ((AppsCustomizePagedView) findViewById(R.id.apps_customize_pane_content)).setVisibility(View.GONE);
            ((BlockList) findViewById(R.id.search_blocks)).setVisibility(View.VISIBLE);
        }
    }**/
}
