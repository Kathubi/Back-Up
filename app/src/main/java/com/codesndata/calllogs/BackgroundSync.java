package com.codesndata.calllogs;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Collins on 5/16/2018 @ 01:27 PM.
 * Package Name: com.codesndata.calllogs
 * Project Name : CallLogs
 */

public class BackgroundSync extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
//        alertDialog.setTitle("Server Response");

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    public String doInBackground(String... params) {
        String post_url = "http://192.168.43.58/elitestech/Locator/login/sync_call_logs.php";

        try {
            String name = params[0];
            String number = params[1];
            String serial = params[2];
            String callType = params[3];
            String callDate = params[4];
            String callDuration = params[5];
            String geocode = params[6];

            URL url = new URL(post_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            try {
                String data =
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                                URLEncoder.encode("number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8") + "&" +
                                URLEncoder.encode("serial", "UTF-8") + "=" + URLEncoder.encode(serial, "UTF-8") + "&" +
                                URLEncoder.encode("callType", "UTF-8") + "=" + URLEncoder.encode(callType, "UTF-8") + "&" +
                                URLEncoder.encode("callDate", "UTF-8") + "=" + URLEncoder.encode(callDate, "UTF-8") + "&" +
                                URLEncoder.encode("callDuration", "UTF-8") + "=" + URLEncoder.encode(callDuration, "UTF-8") + "&" +
                                URLEncoder.encode("geocode", "UTF-8") + "=" + URLEncoder.encode(geocode, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                IS.close();

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } catch (NetworkOnMainThreadException | IOException e) {
            e.getStackTrace();
        }

        return null;
    }
}
