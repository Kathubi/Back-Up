package com.codesndata.calllogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

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
        pDialog.setCancelable(true);
        pDialog.getProgress();
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
                Log.d(TAG, "initialize() >> initializing loader...");
                getLoaderManager().initLoader(URL_LOADER, null, MainActivity.this);
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
    public void onBackPressed() {
        pDialog.cancel();
        Intent home = new Intent(this, com.codesndata.back_up.MainActivity.class);
        startActivity(home);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset()");
        // do nothing
    }
}
