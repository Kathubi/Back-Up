package com.codesndata.calllogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.codesndata.back_up.R;

import java.util.Date;

import static android.provider.CallLog.Calls.DEFAULT_SORT_ORDER;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "CallLog";
    private static final int URL_LOADER = 1;
    private TextView callLogsTextView;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    int counter;
    StringBuilder sb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_call_logs);

        //Show ProgressDialog...
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("READING CALL LOGS");
        pDialog.setMessage("Please be patient...\n\nSynchronizing Call Logs with the server.");
        pDialog.setIcon(R.drawable.call);
        pDialog.getProgress();
        pDialog.setCancelable(true);
        pDialog.show();

        callLogsTextView = findViewById(R.id.call_logs);
        //Handle updates in the progress dialog
        updateBarHandler = new Handler();

        // kick start the task of loading call logs
        initialize();

                //Since reading all the Call Logs will probably take some time, it's considered
               // wise to run the task on its own thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }).start();
        }


    private void initialize() {
        Log.d(TAG, "Permissions check");

        if (!hasPhoneContactsPermission(Manifest.permission.READ_CONTACTS)) {
            requestPermission(Manifest.permission.READ_CONTACTS);
        }
            if (!hasPhoneStatePermission(Manifest.permission.READ_PHONE_STATE)) {
                requestPerm(Manifest.permission.READ_PHONE_STATE);
            } else {

                Log.d(TAG, "initialize() >> initializing loader...");
                getLoaderManager().initLoader(URL_LOADER, null, MainActivity.this);
            }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        Log.d(TAG, "onCreateLoader() >> loaderID : " + loaderID);

        switch (loaderID) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        CallLog.Calls.CONTENT_URI,        // Table to query
                        null,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        DEFAULT_SORT_ORDER             // default order
                );
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor managedCursor) {
        sb = new StringBuilder();

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int display_name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int location = managedCursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        sb.append("<h4>CALL LOGS <h4>");
        sb.append("\n");
        sb.append("\n");

        // Iterate every contact in the phone
        for (int i = 0; i < managedCursor.getCount(); i++) {
            while (managedCursor.moveToNext()) {

                String phNumber = managedCursor.getString(number);
                String name = managedCursor.getString(display_name);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                String callDayTime = String.valueOf(new Date(Long.valueOf(callDate)));
                String callDuration = managedCursor.getString(duration);
                String geocode = managedCursor.getString(location);

                String dir = null;

                int callTypeCode = Integer.parseInt(callType);
                switch (callTypeCode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "Outgoing";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "Incoming";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "Missed";
                        break;

                    case 6:
                        dir = "Blocked";
                        break;

                    case CallLog.Calls.REJECTED_TYPE:
                        dir = "Rejected";
                        break;

                    case CallLog.Calls.VOICEMAIL_TYPE:
                        dir = "Voice mail";
                        break;

                    case CallLog.Calls.ANSWERED_EXTERNALLY_TYPE:
                        dir = "Answered Externally";
                        break;
                }
                // Update the progress message
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Please be patient...\n\nSynchronizing Call Log number " + counter++ + " of " + managedCursor.getCount() + " with the server.");
                    }
                });
                @SuppressLint("HardwareIds") String serial = Build.SERIAL;
                BgSync sync = new BgSync();
                sync.execute(name, phNumber, serial, dir, callDayTime, callDuration, geocode);

                sb.append("<tr>")
                        .append("<td>Display Name: </td>")
                        .append("<td><strong>")
                        .append(name)
                        .append("</strong></td>");
                sb.append("</tr>");
                sb.append("<br/>");
                sb.append("<tr>")
                        .append("<td>Phone Number: </td>")
                        .append("<td><strong>")
                        .append(phNumber)
                        .append("</strong></td>");
                sb.append("</tr>");
                sb.append("<br/>");
                sb.append("<tr>")
                        .append("<td>Call Type:</td>")
                        .append("<td><strong>")
                        .append(dir)
                        .append("</strong></td>");
                sb.append("</tr>");
                sb.append("<br/>");
                sb.append("<tr>")
                        .append("<td>Date & Time:</td>")
                        .append("<td><strong>")
                        .append(callDayTime)
                        .append("</strong></td>");
                sb.append("</tr>");
                sb.append("<br/>");
                sb.append("<tr>")
                        .append("<td>Call Duration (Seconds):</td>")
                        .append("<td><strong>")
                        .append(callDuration)
                        .append("</strong></td>");
                sb.append("</tr>");
                sb.append("<br/>");
                sb.append("<tr>")
                        .append("<td>Geocoded Location:</td>")
                        .append("<td><strong>")
                        .append(geocode)
                        .append("</strong></td>");
                sb.append("</tr>");
                sb.append("<br/>");
                sb.append("<tr>")
                        .append("<td>Phone Serial:</td>")
                        .append("<td><strong>")
                        .append(serial)
                        .append("</strong></td>");
                sb.append("</tr>");
                sb.append("<br/>");
                sb.append("<br/>");
            }
        }
            sb.append("</table>");
//            managedCursor.close();


            // ListView has to be updated using a ui thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Writing Logs on device's display...");
                    callLogsTextView.setText(Html.fromHtml(sb.toString()));
                }
            });

            // Dismiss the progressbar after 500 millisecondds
            updateBarHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Log.d(TAG, "Finished successfully!");
                    pDialog.cancel();
                }
            }, 500);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset()");
        // do nothing
    }

    // Check whether user has phone contacts manipulation permission or not.
    private boolean hasPhoneStatePermission(String permission) {
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
    private void requestPerm(String permission) {
        String requestPermissionArray[] = {permission};
        ActivityCompat.requestPermissions(this, requestPermissionArray, 1);
    }

    // Check whether user has phone contacts manipulation permission or not.
    private boolean hasPhoneContactsPermission(String permission) {
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

    // After user select Allow or Deny button in request runtime permission dialog
    // , this method will be invoked.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int length = grantResults.length;
        if (length > 0) {
            int grantResult = grantResults[0];

            if (grantResult == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "You allowed permission, please click the button again.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "You denied permission.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
