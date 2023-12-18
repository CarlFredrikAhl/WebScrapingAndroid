package com.example.webscraping;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PhoneCheckService extends JobIntentService {
    static final int JOB_ID = 1000;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //Check for new phones and send notification if new phone
        try {
            String url = "https://www.gsmarena.com/";
            Document doc = Jsoup.connect(url).get();
            Elements phonesData = doc.select(".module-phones-link");
            String latestPhone = "";
            latestPhone = phonesData.get(0).text();

            String latestPhoneStorage = PhoneStorage.getLatestPhone(getApplicationContext());

            //Save to sharedpreferences
            if(!latestPhone.equals(latestPhoneStorage)) {
                //Send notification and update app icon to show another phone has come out
                showNotification(latestPhone);
            }

        } catch (IOException ioe) {

        }
    }

    static void enqueWork(Context context, Intent work) {
        enqueueWork(context, PhoneCheckService.class, JOB_ID, work);
    }

    private void showNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        //Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.notification_active)
                .setContentTitle("New phone!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Show notification
        notificationManager.notify(1, builder.build());
    }
}
