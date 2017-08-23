package com.Softy.Services.GoodHub;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 4/30/2017.
 */

public class RegisterTask extends AsyncTask<Void, Void, String> {
    private static Context mContext;
    private static String email;
    private static String user;
    private static String pass;
    private static String verify;

    public RegisterTask(Context mContext, String email, String user, String pass, String verify)
    {
        RegisterTask.mContext = mContext;
        RegisterTask.email = email;
        RegisterTask.user = user;
        RegisterTask.pass = pass;
        RegisterTask.verify = verify;
    }
    @Override
    protected String doInBackground(Void... vparams) {
        String result = "";
        try
        {
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                    HttpVersion.HTTP_1_1);
            HttpClient client = new DefaultHttpClient(params);
            HttpPost post = new HttpPost("https://goodhub.000webhostapp.com/register.php");
            List<NameValuePair> list = new ArrayList<>(3);
            int rand = ((int)(Math.random() * 10) + 5);
            list.add(new BasicNameValuePair("user", user));
            list.add(new BasicNameValuePair("email", email));
            list.add(new BasicNameValuePair("password", pass));
            post.setEntity(new UrlEncodedFormEntity(list, "utf-8"));

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
        Toast.makeText(mContext, "Now try signing in", Toast.LENGTH_SHORT).show();
    }
}
