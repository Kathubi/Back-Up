package com.codesndata.maps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Collins Mbwika on 23-Feb-17 @ 11:31 PM.
 * Package Name: ${PACKAGE_NAME}
 * Project Name : MapMarker
 */

public class BackgroundRegTask extends AsyncTask<String,Void,String> {

    @SuppressLint("StaticFieldLeak")
    private
    Context ctx, context;
    private AlertDialog alertDialog;


    BackgroundRegTask(Context ctx, Context context) {
        this.context = context;
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Server Response");

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Toast.makeText(ctx, "Hold on...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.contains("Success")) {
            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
        } else if (result.contains("Data")) {
            alertDialog.setMessage(result);
            alertDialog.show();

        } else if(result.contains("Serial")){

            try {

                Toast.makeText(ctx, "Redirecting to maps...", Toast.LENGTH_LONG).show();
                Intent land = new Intent(context, MapsActivity.class);
                context.startActivity(land);
            } catch (Exception x) {
                x.printStackTrace();
            }
        } else{
            alertDialog.setTitle(result);
            alertDialog.setMessage("Login failed!");
            alertDialog.show();
            //Toast.makeText(ctx, "Login failed!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected String doInBackground(String... params) {

        //Private (Phone) IP: 192.168.43.58
        //Local (Emulator) IP: 10.0.2.2
        //String reg_url = "http://192.168.43.58/elitestech/Locator/login/register.php";
        String reg_url = "http://192.168.43.58/elitestech/Locator/register_user.php";
        String login_url = "http://192.168.43.58/php/login.php";

        String method = params[0];

        switch (method) {
            case "register": {

                String name = params[1];
                String surname = params[2];
                String email = params[3];
                String serial = params[4];
                String secret_code = params[5];
                String image = params[6];

                try {
                    URL url = new URL(reg_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                    String data =
                            URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                                    URLEncoder.encode("surname", "UTF-8") + "=" + URLEncoder.encode(surname, "UTF-8") + "&" +
                                    URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                                    URLEncoder.encode("serial", "UTF-8") + "=" + URLEncoder.encode(serial, "UTF-8") + "&" +
                                    URLEncoder.encode("secret_code", "UTF-8") + "=" + URLEncoder.encode(secret_code, "UTF-8") + "&" +
                                    URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8");

                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    return "";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "login": {
                String email = params[1];
                String serial = params[2];
                try {
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                            URLEncoder.encode("serial", "UTF-8") + "=" + URLEncoder.encode(serial, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    return "";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
    }

}



