package com.Softy.Launcher2.Classes;

import android.app.Activity;

/**
 * Created by mcom on 3/29/17.
 */

public class Block {
    private static Activity mActivity;
    public static void setActivity(Activity mActivity){
        Block.mActivity = mActivity;
    }

    /**private static BlockList mBlock;
    private static Context mContext;
    private static BlockList mList;
    private static List<String> string;
    private static List<String> pName;
    public Block(Activity mActivity){
        this.mContext = mActivity;
    }
    public Block(Context mActivity, BlockList mList){
        this.mContext = mActivity;
        this.mList = mList;
    }

    public static void searchBlockList(String text){
        Intent intent= new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        string = new ArrayList<>();
        pName = new ArrayList<>();
        String caseSensative = mActivity.getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getString("case-sensitive", "");
        List<ResolveInfo> mResolve = mActivity.getPackageManager().queryIntentActivities(intent,0);

        Apps[] app = new Apps[mResolve.size()];
        for(int i = 0; i < mResolve.size(); i ++){
            String mActivityName = mResolve.get(i).loadLabel(mActivity.getPackageManager()).toString();
            switch(caseSensative){
                case "case-sensitive":
                    if(mActivityName.contains(text)){
                        app[i] = new Apps();
                        app[i].mIcon = mResolve.get(i).loadIcon(mActivity.getPackageManager());
                        app[i].label = mResolve.get(i).loadLabel(mActivity.getPackageManager()).toString();
                        app[i].packageName = mResolve.get(i).resolvePackageName;
                        string.add(mActivityName);
                        pName.add(app[i].packageName);
                        ((BlockList)mActivity.findViewById(R.id.search_block_list)).setVisibility(View.VISIBLE);
                        ((AppsCustomizePagedView)mActivity.findViewById(R.id.apps_customize_pane_content)).setVisibility(View.GONE);
                    }
                    break;
                case "case-insensitive":
                    if(Pattern.compile(Pattern.quote(text), Pattern.CASE_INSENSITIVE).matcher(mActivityName).find()){
                        app[i] = new Apps();
                        app[i].mIcon = mResolve.get(i).loadIcon(mActivity.getPackageManager());
                        app[i].label = mResolve.get(i).loadLabel(mActivity.getPackageManager()).toString();
                        app[i].packageName = mResolve.get(i).resolvePackageName;
                        string.add(mActivityName);
                        pName.add(app[i].packageName);
                        ((BlockList)mActivity.findViewById(R.id.search_block_list)).setVisibility(View.VISIBLE);
                        ((AppsCustomizePagedView)mActivity.findViewById(R.id.apps_customize_pane_content)).setVisibility(View.GONE);
                    }
                    break;
                case "":
                    mActivity.getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putString("case-sensitive", "case-insensitive").commit();
                    searchBlockList(text);
                    break;
            }
        }

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), android.R.layout.simple_list_item_1, string);
        ((BlockList) mActivity.findViewById(R.id.search_block_list)).setAdapter(mAdapter);
        mBlock = (BlockList)mActivity.findViewById(R.id.search_block_list);
        //Will apply user click to open app & hide blocklist
        applyUserClickEvent();
    }

    private static void applyUserClickEvent(){
        mBlock = (BlockList) mActivity.findViewById(R.id.search_block_list);
        mBlock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = string.get(position);
                String packageName = pName.get(position);
                Intent launch = mActivity.getPackageManager().getLaunchIntentForPackage(packageName);
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(launch);
            }
        });
    }

    public static void applyUserTouchEvent(final View v){
        ((AppsCustomizeTabHost) v.findViewById(R.id.app_tab_host)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppsCustomizePagedView) v.findViewById(R.id.apps_customize_pane_content)).setVisibility(View.VISIBLE);
                ((BlockList)v.findViewById(R.id.search_block_list)).setVisibility(View.GONE);
            }
        });
    }**/
}
