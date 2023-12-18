package com.example.webscraping;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
                String phoneWidth = "";

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
                        String message = newPhone.getName() + "\n" +
                                newPhone.getScreenSize() + "\n" +
                                newPhone.getResolution() + "\n" +
                                newPhone.getPhoneWidth() + "\n" +
                                newPhone.getScreenType();

                        showNotification(message, bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }

        } catch (IOException ioe) {

        }
    }

    static void enqueWork(Context context, Intent work) {
        enqueueWork(context, PhoneCheckService.class, JOB_ID, work);
    }

    private void showNotification(String message, Bitmap bitmap) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        //Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.android_phone_icon)
                .setContentTitle("New phone!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Show notification
        notificationManager.notify(1, builder.build());
    }
}
