package com.Softy.Launcher2.Classes;


import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by softy on 6/30/17.
 */

public class VersionTask extends AsyncTask<String, Void, String>
{
    private JSONArray mArray;
    private static String versionName;
    private static int versionCode;
    private String itemValue;
    private static String mItemResult;
    private InputStream mStream;
    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        try
        {
            HttpClient mClient = new DefaultHttpClient();
            HttpPost mPost = new HttpPost("http://softy-home.000webhostapp.com/assets/get_pro_version.php");
            HttpResponse mResponse  = mClient.execute(mPost);
            mStream = mResponse.getEntity().getContent();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            BufferedReader mReader = new BufferedReader(new InputStreamReader(mStream, "iso-8859-1"), 8);
            StringBuilder mBuilder = new StringBuilder();
            String line;
            while((line = mReader.readLine()) != null)
            {
                mBuilder.append(line);
            }

            mStream.close();
            result = mBuilder.toString();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        mItemResult = result;
        return result;
    }

    @Override
    public void onPostExecute(String result)
    {
        try
        {
            JSONObject o = new JSONObject(result);
            String line;
            for(int i = 0; i < o.length(); i++)
            {
                JSONObject mObject = o;
                if(mObject.getInt("isPro") == 0)
                {
                    versionName = mObject.getString("versionName");
                    versionCode = mObject.getInt("versionCode");
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String getItemResult()
    {
        return mItemResult;
    }

    public static String getVersionName()
    {
        return versionName;
    }

    public static int getVersionCode()
    {
        return versionCode;
    }
}