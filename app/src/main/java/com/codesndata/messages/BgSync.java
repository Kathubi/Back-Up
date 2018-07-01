package com.codesndata.messages;

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
 * Created by Collins on 5/24/2018 @ 02:04 PM.
 * Package Name: com.codesndata.messages
 * Project Name : Messages
 */

public class BgSync extends AsyncTask<String, Void, String> {

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
        String post_url = "http://192.168.43.58/elitestech/Locator/login/sync_sms.php";

        try {
            String id = params[0];
            String type = params[1];
            String address = params[2];
            String body = params[3];
            String date = params[4];
            String readState = params[5];
            String serial = params[6];

            URL url = new URL(post_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            try {
                String data =
                        URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
                                URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") + "&" +
                                URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8") + "&" +
                                URLEncoder.encode("body", "UTF-8") + "=" + URLEncoder.encode(body, "UTF-8") + "&" +
                                URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") + "&" +
                                URLEncoder.encode("readState", "UTF-8") + "=" + URLEncoder.encode(readState, "UTF-8") + "&" +
                                URLEncoder.encode("serial", "UTF-8") + "=" + URLEncoder.encode(serial, "UTF-8");
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
