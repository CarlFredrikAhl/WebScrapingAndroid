package com.example.webscraping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Start the service
        Intent serviceIntent = new Intent(context, PhoneCheckService.class);
        context.startService(serviceIntent);
    }
}
