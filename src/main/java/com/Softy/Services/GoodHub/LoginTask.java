package com.Softy.Services.GoodHub;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.CookieManager;

import com.Softy.Launcher2.Data;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 4/30/2017.
 */

public class LoginTask extends AsyncTask<Void, Void, String> {
    private static Context mContext;
    private static String email;
    private static String user;
    private static String pass;
    private static String verify;

    public LoginTask(Context mContext, String user, String pass)
    {
        LoginTask.mContext = mContext;
        LoginTask.user = user;
        LoginTask.pass = pass;
    }
    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://goodhub.000webhostapp.com/login.php");
            List<NameValuePair> list = new ArrayList<NameValuePair>(2);
            list.add(new BasicNameValuePair("username", user));
            list.add(new BasicNameValuePair("password", pass));
            post.setEntity(new UrlEncodedFormEntity(list));

            //execute
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),5);
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }
            is.close();
            result = sb.toString();
        }catch(ClientProtocolException cpe)
        {
        }catch(IOException ioe)
        {
        }

        return result;
    }

    @Override
    public void onPostExecute(String endResult)
    {
        //Toast.makeText(mContext, CookieManager.getInstance().getCookie("https://goodhub.000webhostapp.com").toString(), Toast.LENGTH_LONG).show();
        mContext.getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putString("user", user).commit();
        mContext.getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putString("email", email).commit();
        mContext.getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).edit().putString("pass", pass).commit();
    }


    public static String getCookieValue(String cookieName, String url)
    {
        return "All cookies: " + CookieManager.getInstance().getCookie(url);
    }

    public static String getUser(Context c)
    {
        return c.getSharedPreferences(Data.NAME, Context.MODE_PRIVATE).getString("user", "");
    }
}
