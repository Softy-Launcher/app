package com.Softy.Services.GoodHub;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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
 * Created by softy on 6/22/17.
 */

public class ManageAccount extends AsyncTask<Void, Void, String>{
    private Context mContext;
    private String user;
    private String email;
    private String pass;

    public ManageAccount(Context mContext, String user, String email, String pass)
    {
        this.mContext = mContext;
        this.user = user;
        this.email = email;
        this.pass = pass;
    }
    @Override
    protected String doInBackground(Void... voids) {
        String result = "";
        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://goodhub.000webhostapp.com/settings.php");
            List<NameValuePair> list = new ArrayList<>(4);
            int rand = ((int)(Math.random() * 10) + 5);
            list.add(new BasicNameValuePair("user", user));
            list.add(new BasicNameValuePair("email", email));
            list.add(new BasicNameValuePair("pass", pass));
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
    public void onPostExecute(String result)
    {
        Toast.makeText(mContext, "Changed user info", Toast.LENGTH_SHORT).show();
    }
}
