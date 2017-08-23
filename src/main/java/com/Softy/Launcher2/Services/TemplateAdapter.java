package com.Softy.Launcher2.Services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Softy.Launcher2.Classes.TemplateData;
import com.Softy.Launcher2.R;

/**
 * Created by mcom on 2/21/17.
 */

public class TemplateAdapter extends BaseAdapter{
    private Context mContext;
    private TemplateData[] mTemp;
    public TemplateAdapter(Context mContext, TemplateData mTemp[]){
        this.mContext = mContext;
        this.mTemp = mTemp;
    }
    @Override
    public int getCount() {
        return mTemp.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class Holder{
        TextView name;
        TextView summary;
    }
    private Holder mHolder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            mHolder = new Holder();

            convertView = li.inflate(R.layout.template_list, null, false);

            mHolder.name = (TextView) convertView.findViewById(R.id.template_name);
            mHolder.summary = (TextView) convertView.findViewById(R.id.template_summary);

            convertView.setTag(mHolder);
        }else
            mHolder = (Holder) convertView.getTag();

        mHolder.name.setText(mTemp[position].name);
        mHolder.summary.setText(mTemp[position].summary);
        return convertView;
    }
}
