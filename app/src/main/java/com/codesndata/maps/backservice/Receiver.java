package com.codesndata.maps.backservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.codesndata.maps.MapsActivity;

/**
 * Created by Collins on 3/9/2018 @ 12:07 AM.
 * Package Name: ke.co.elitestech.backservice
 * Project Name : BackService
 */

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent bootCompleted = new Intent(context, MapsActivity.class);
        bootCompleted.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bootCompleted);

        Log.i(Receiver.class.getSimpleName(), "Service Stopped! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, SensorService.class));
    }
}

