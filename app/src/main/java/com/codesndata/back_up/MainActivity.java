package com.codesndata.back_up;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    GridLayout grid_layout;
    private static final String TAG = "back_up";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid_layout = findViewById(R.id.grid_layout);

        //set Event
        setSingleEvent(grid_layout);
        //check Internet availability
        isInternetOn();
    }

    private void setSingleEvent(GridLayout grid_layout) {
        //loop all child items
        for(int i = 0; i < grid_layout.getChildCount(); i++){
            //
            CardView cardView = (CardView) grid_layout.getChildAt(i);
            final int child_no = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(child_no == 0){
                        getMessages();
                    }
                    if(child_no == 1){
                        getContacts();
                    }
                    if(child_no == 2){
                        getCallLogs();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void getContacts(){
        //Load contacts
        Intent contacts = new Intent(this, com.codesndata.contacts.MainActivity.class);
        startActivity(contacts);
    }

    public void getCallLogs(){
        //Load call logs
        Intent callLogs = new Intent(this, com.codesndata.calllogs.MainActivity.class);
        startActivity(callLogs);
    }

    public void getMessages(){
        initialize();
        //Load SMSs
        Intent sms = new Intent(this, com.codesndata.messages.MainActivity.class);
        startActivity(sms);
    }

    private void initialize() {

        if (!hasReadSmsPermission(Manifest.permission.READ_SMS)) {
            requestPermission(Manifest.permission.READ_SMS);
        }

    }
    public final void isInternetOn() {
        Log.d(TAG, "Checking Internet...");

        // get Connectivity Manager object to check connection
        getBaseContext();
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

        // Check for network connections
        assert connec != null;
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

            Log.d(TAG, "Internet ON");

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Log.d(TAG, "Internet OFF");
            showAlert();
        }
    }

    public void showAlert(){

        Log.d(TAG, "Switching Internet ON");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_action_info);
        builder.setTitle("INTERNET REQUIRED");
        builder.setMessage("\nPlease, switch ON Internet to enable connection to the server.");
        builder.setCancelable(true);
        builder.setPositiveButton("SWITCH ON", new android.content.DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                startActivity(intent);
            }

        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    // Check whether user has phone contacts manipulation permission or not.
    private boolean hasReadSmsPermission(String permission) {
        boolean ret = false;

        // If android sdk version is bigger than 23 the need to check run time permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // return phone read contacts permission grant status.
            int hasPermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            // If permission is granted then return true.
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                ret = true;
            }
        } else {
            ret = true;
        }
        return ret;
    }

    // Request a runtime permission to app user.
    private void requestPermission(String permission) {
        String requestPermissionArray[] = {permission};
        ActivityCompat.requestPermissions(this, requestPermissionArray, 1);
    }

}
