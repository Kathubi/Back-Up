package com.codesndata.back_up;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;

public class MainActivity extends AppCompatActivity {

    GridLayout grid_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid_layout = findViewById(R.id.grid_layout);

        //set Event
        setSingleEvent(grid_layout);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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

    public void getCallLogs(){
        //Load call logs
        Intent callLogs = new Intent(this, com.codesndata.calllogs.MainActivity.class);
        startActivity(callLogs);
    }

    public void getMessages(){

        initialize();
        //Load call logs
        Intent sms = new Intent(this, com.codesndata.messages.MainActivity.class);
        startActivity(sms);
    }

    private void initialize() {

        if (!hasReadSmsPermission(Manifest.permission.READ_SMS)) {
            requestPermission(Manifest.permission.READ_SMS);
        }

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
