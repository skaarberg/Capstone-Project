package com.bikefriend.jrs.bikefriend.service;

import android.content.Context;
import android.os.AsyncTask;

import com.bikefriend.jrs.bikefriend.StoreToDatabaseTask;
import com.bikefriend.jrs.bikefriend.provider.BikeDbHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by skaar on 12.11.2017.
 */

public class RequestTask extends AsyncTask<String, String, String> {

    public static final String FETCH_BIKE_STATUS = "fetchBikeStatus";
    public static final String FETCH_SYSTEM_STATUS = "fetchSystemStatus";
    public static final String FETCH_BASE_DATA = "fetchBaseData";
    public static final String SYSTEM_STATUS = "systemStatus";

    String currentRequestType;
    Context context;

    public RequestTask(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection con = null;
        try {
            URL u = new URL(params[0]);
            currentRequestType = params[1];
            con = (HttpURLConnection) u.openConnection();
            con.setRequestProperty("Client-Identifier", "8df875211f543281f3333b6dcff2c1d8");
            con.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();


        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        new StoreToDatabaseTask(context).execute(currentRequestType, result);
    }
}