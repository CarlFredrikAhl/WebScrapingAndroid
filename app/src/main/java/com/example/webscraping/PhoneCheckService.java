package com.example.webscraping;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PhoneCheckService extends JobService {
    static final int JOB_ID = 1000;

    @Override
    public boolean onStartJob(JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("PhoneCheckService", "onStartJob");

                //Check for new phones and send notification if new phone
                try {
                    String url = "https://www.gsmarena.com/";
                    Document doc = Jsoup.connect(url).get();
                    Elements phonesData = doc.select(".module-phones-link");
                    String latestPhone = "";
                    latestPhone = phonesData.get(0).text();

                    String latestPhoneStorage = PhoneStorage.getLatestPhone(getApplicationContext());

                    //Save to sharedpreferences
                    //if(!latestPhone.equals(latestPhoneStorage)) {
                    //Send notification that new phone has come out

                    String name = phonesData.get(0).text();
                    String imgUrl = phonesData.select("img").eq(0).attr("src");
                    String phoneUrl = phonesData.eq(0).attr("href");
                    String completePhoneUrl = url + phoneUrl;

                    //Visit each individual phone link to extract data about them
                    Document phoneSpecsDoc = Jsoup.connect(completePhoneUrl).get();
                    String screenSize = phoneSpecsDoc.select("td:contains(Size)").next().text();
                    String screenResolution = phoneSpecsDoc.select("td:contains(Resolution)").next().text();
                    String screenType = phoneSpecsDoc.select("td:contains(Type)").next().text();
                    String phoneDimensions = phoneSpecsDoc.select("td:contains(Dimensions)").next().text();
                    String phoneWidth = "-";

                    //Check if the dimensions are known
                    if(phoneDimensions.contains("x")) {
                        phoneWidth = phoneDimensions.split(" x ")[1] + " mm";
                    }

                    Log.i("PHONE_TAG", "Name: " + name + ", screen size: " + screenSize + "screen resolution: " + screenResolution
                            + "screen type" + screenType + "phone width: " + phoneWidth);

                    PhoneModel newPhone = new PhoneModel(name, imgUrl, screenSize, screenResolution, screenType, phoneWidth);

                    Picasso.get().load(newPhone.getImageUrl()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    showNotification(newPhone, bitmap);
                                }
                            });
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            // Handle failure
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            // Prepare for loading
                        }
                    });

                    //}

                } catch (IOException ioe) {

                }

                jobFinished(params, false);

            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }


    private void showNotification(PhoneModel phone, Bitmap bitmap) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout_expanded);
        remoteViews.setTextViewText(R.id.notificationName, phone.getName());
        remoteViews.setTextViewText(R.id.notificationSize, phone.getScreenSize());
        remoteViews.setTextViewText(R.id.notificationResolution, phone.getResolution());
        remoteViews.setTextViewText(R.id.notificationWidth, phone.getPhoneWidth());
        remoteViews.setTextViewText(R.id.notificationType, phone.getScreenType());
        remoteViews.setImageViewBitmap(R.id.notification_img, bitmap);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "channel_id")
                .setSmallIcon(R.drawable.android_phone_icon)
                .setCustomBigContentView(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, notification.build());
    }

}

