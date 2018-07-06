package com.codesndata.messages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codesndata.back_up.R;

import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {
    ListView lViewSMS;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    int counter;
    Cursor cursor;
    ArrayList<String> sms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        pDialog = new ProgressDialog(this);
        pDialog.setTitle(" READING SMSs");
        pDialog.setIcon(R.drawable.sms);
        pDialog.setCancelable(true);
        pDialog.show();

        lViewSMS = findViewById(R.id.listView);
        updateBarHandler = new Handler();

        //Since reading all the SMSs takes some time, it's considered wise to run the task on
        //its own independent thread
        new Thread(new Runnable() {

            @Override
            public void run() {
                fetchSms();
            }
        }).start();
    }

    public void fetchSms()
    {
        sms = new ArrayList<>();
        Uri uriSms = Uri.parse("content://sms/");
        cursor = getContentResolver().query(uriSms, new String[]
                        {"_id", "address", "date", "body", "type", "read"},
                null,null,null);

        assert cursor != null;
        for (int i = 0; i < cursor.getCount(); i++) {
            while (cursor.moveToNext()) {

                // Update the progress message
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Please be patient...\n\nSynchronizing SMS number " + counter++ + " of " + cursor.getCount() + " with the server.");
                    }
                });

                String id = cursor.getString(0);
                String address = cursor.getString(1);
                String smsDate = cursor.getString(2);
                String date = String.valueOf(new Date(Long.valueOf(smsDate)));
                String body = cursor.getString(3);
                String type;
                if (cursor.getString(4).contains("1")) {
                    type = "Received";
                } else {
                    type = "Sent";
                }
                String readState;
                if (cursor.getString(5).contains("1")) {
                    readState = "Read";
                } else {
                    readState = "Unread";
                }

                System.out.println("==== Id ; " + id);
                System.out.println("==== Type ; " + type);
                System.out.println("==== Entity ; " + address);
                System.out.println("==== Date ; " + date);
                System.out.println("==== SMS Text ; " + body);
                System.out.println("==== Read State ; " + readState);

                @SuppressLint("HardwareIds") String serial = Build.SERIAL;
                BgSync sync = new BgSync();
                sync.execute(id, type, address, body, date, readState, serial);

                sms.add(":> Id :> " + id + "\nType :> " + type + "\nEntity :> " + address + "\nSMS :> " + body + "\nDate :> " + date + "\nRead State :> " + readState);
            }

            // ListView has to be updated using a ui thread
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item_sms, R.id.text1, sms);
                    lViewSMS.setAdapter(adapter);
                }
            });
        }

        // Dismiss the progressbar after 500 millisecondds
        updateBarHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                pDialog.cancel();
            }
        }, 500);

    }
}