package com.example.webscraping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PhoneAdapter phoneAdapter;
    ArrayList<PhoneModel> phoneModels = new ArrayList<>();

    String latestPhoneStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        phoneAdapter = new PhoneAdapter(phoneModels, this);
        recyclerView.setAdapter(phoneAdapter);

        AlarmManagerHelper.startAlarm(getApplicationContext());

//        TestNotification testNotification = new TestNotification();
//        testNotification.execute();

        //Retrieve data from web scraping
        WebScraper webScraper = new WebScraper();
        webScraper.execute();

        latestPhoneStorage = PhoneStorage.getLatestPhone(getApplicationContext());

        //Toast.makeText(getApplicationContext(), latestPhoneStorage, Toast.LENGTH_SHORT).show();
    }

    class TestNotification extends AsyncTask<Void, Void, PhoneModel> {

        @Override
        protected PhoneModel doInBackground(Void... voids) {
            try {
                String url = "https://www.gsmarena.com/";
                Document doc = Jsoup.connect(url).get();
                Elements phonesData = doc.select(".module-phones-link");
                String latestPhone = "";
                latestPhone = phonesData.get(0).text();


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

                    return newPhone;

            } catch (IOException ioe) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(PhoneModel newPhone) {
            if(newPhone != null) {
                try {
                    Picasso.get().load(newPhone.getImageUrl()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            showNotification(newPhone, bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                } catch (Exception e) {}
            }
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

    class WebScraper extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            phoneAdapter.notifyDataSetChanged();
        }
        @Override
        protected Void doInBackground(Void... values) {
            try {
                String url = "https://www.gsmarena.com/";
                Document doc = Jsoup.connect(url).get();
                Elements phonesData = doc.select(".module-phones-link");
                String latestPhone = "";
                latestPhone = phonesData.get(0).text();

                //Save to sharedpreferences
                if(!latestPhone.equals(latestPhoneStorage)) {
                    PhoneStorage.saveLatestPhone(getApplicationContext(), latestPhone);
                }

                for(int i = 0; i < 5; i++) {
                    String name = phonesData.get(i).text();
                    String imgUrl = phonesData.select("img").eq(i).attr("src");
                    String phoneUrl = phonesData.eq(i).attr("href");
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

                    //Add the phones to phone models
                    phoneModels.add(new PhoneModel(name, imgUrl, screenSize, screenResolution, screenType, phoneWidth));
                    publishProgress();
                }

            } catch (IOException ioe) {

            }

            return null;
        }
    }
}
