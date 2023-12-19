package com.example.webscraping;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "onReceive");
        //Start the service
        JobInfo jobInfo = new JobInfo.Builder(PhoneCheckService.JOB_ID,
                new ComponentName(context, PhoneCheckService.class))
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(AlarmManagerHelper.ALARM_INTERVAL)
                .build();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if(jobScheduler != null) {
            jobScheduler.schedule(jobInfo);
        }
    }
}
