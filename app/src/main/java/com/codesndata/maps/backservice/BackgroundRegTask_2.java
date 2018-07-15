package com.codesndata.maps.backservice;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Project: Locator
 * Created by: Collins Mbwika
 * Date: 23-Feb-17
 * Time: 12:20
 */

public class BackgroundRegTask_2 extends AsyncTask<String,Void,String> {


    @Override
    protected void onProgressUpdate(Void... values) {

    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {


        //Private (Phone) IP: 192.168.43.58
        //Local (Emulator) IP: 10.0.2.2
        String location_data_url = "http://192.168.43.58/elitestech/Locator/insertdata.php";
        String phone_info_url = "http://192.168.43.58/elitestech/Locator/insert_phone_data.php";

        String function = params[0];//192.168.43.58

        if (function.equals("locationInfo")) {
            String serial = params[1];
            String latitude = String.valueOf(Double.parseDouble(params[2]));
            String longitude = String.valueOf(Double.parseDouble(params[3]));
            String altitude = String.valueOf(Double.parseDouble(params[4]));
            String speed = String.valueOf(Float.parseFloat(params[5]));
            String bearing = String.valueOf(Float.parseFloat(params[6]));
            String provider = params[7];
            String error_margin = String.valueOf(Float.parseFloat(params[8]));
            String time = params[9];
            String extra = params[10];

            try {
                URL url = new URL(location_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("serial", "UTF-8") + "=" + URLEncoder.encode(serial, "UTF-8") + "&" +
                        URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8") + "&" +
                        URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8") + "&" +
                        URLEncoder.encode("altitude", "UTF-8") + "=" + URLEncoder.encode(altitude, "UTF-8") + "&" +
                        URLEncoder.encode("speed", "UTF-8") + "=" + URLEncoder.encode(speed, "UTF-8") + "&" +
                        URLEncoder.encode("bearing", "UTF-8") + "=" + URLEncoder.encode(bearing, "UTF-8") + "&" +
                        URLEncoder.encode("provider", "UTF-8") + "=" + URLEncoder.encode(provider, "UTF-8") + "&" +
                        URLEncoder.encode("error_margin", "UTF-8") + "=" + URLEncoder.encode(error_margin, "UTF-8") + "&" +
                        URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&" +
                        URLEncoder.encode("extra", "UTF-8") + "=" + URLEncoder.encode(extra, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }else if (function.equals("phoneInfo")) {


            //Post phone information
            String serial = params[1];
            String brand = params[2];
            String model = params[3];
            String os_version = params[4];
            String api_version = params[5];
            String incremental = params[6];
            String device_id = params[7];
            String kernel = params[8];
            String e_board = params[9];
            String fingerprint = params[10];
            String user = params[11];
            String codename = params[12];
            String base_os = params[13];
            String securityPatch = params[14];
            String device_type = params[15];
            String IMEI = params[16];
            String baseband_version = params[17];

            try {
                URL url = new URL(phone_info_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data =
                        URLEncoder.encode("serial", "UTF-8") + "=" + URLEncoder.encode(serial, "UTF-8") + "&" +
                                URLEncoder.encode("brand", "UTF-8") + "=" + URLEncoder.encode(brand, "UTF-8") + "&" +
                                URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(model, "UTF-8") + "&" +
                                URLEncoder.encode("os_version", "UTF-8") + "=" + URLEncoder.encode(os_version, "UTF-8") + "&" +
                                URLEncoder.encode("api_version", "UTF-8") + "=" + URLEncoder.encode(api_version, "UTF-8") + "&" +
                                URLEncoder.encode("incremental", "UTF-8") + "=" + URLEncoder.encode(incremental, "UTF-8") + "&" +
                                URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(device_id, "UTF-8") + "&" +
                                URLEncoder.encode("kernel", "UTF-8") + "=" + URLEncoder.encode(kernel, "UTF-8") + "&" +
                                URLEncoder.encode("e_board", "UTF-8") + "=" + URLEncoder.encode(e_board, "UTF-8") + "&" +
                                URLEncoder.encode("fingerprint", "UTF-8") + "=" + URLEncoder.encode(fingerprint, "UTF-8") + "&" +
                                URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8") + "&" +
                                URLEncoder.encode("codename", "UTF-8") + "=" + URLEncoder.encode(codename, "UTF-8") + "&" +
                                URLEncoder.encode("base_os", "UTF-8") + "=" + URLEncoder.encode(base_os, "UTF-8") + "&" +
                                URLEncoder.encode("securityPatch", "UTF-8") + "=" + URLEncoder.encode(securityPatch, "UTF-8") + "&" +
                                URLEncoder.encode("device_type", "UTF-8") + "=" + URLEncoder.encode(device_type, "UTF-8") + "&" +
                                URLEncoder.encode("IMEI", "UTF-8") + "=" + URLEncoder.encode(IMEI, "UTF-8") + "&" +
                                URLEncoder.encode("baseband_version", "UTF-8") + "=" + URLEncoder.encode(baseband_version, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
