package com.codesndata.contacts;

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
 * Created by Collins on 4/23/2018 @ 07:28 PM.
 * Package Name: com.codesndata.readcontacts
 * Project Name : Read Contacts
 */


public class BgSync extends AsyncTask<String, Void, String> {


    BgSync() {

    }

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
        String post_url = "http://192.168.43.58/elitestech/Locator/login/post_contacts.php";

        String IMEI = params[0];
        String serial = params[1];
        String name = params[2];
        String phoneNumber = params[3];
        String email = params[4];
        String birthday = params[5];

        try {


            URL url = new URL(post_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            try {
                String data = URLEncoder.encode("IMEI", "UTF-8") + "=" + URLEncoder.encode(IMEI, "UTF-8") + "&" +
                        URLEncoder.encode("serial", "UTF-8") + "=" + URLEncoder.encode(serial, "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode(phoneNumber, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("birthday", "UTF-8") + "=" + URLEncoder.encode(birthday, "UTF-8");
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

