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

        TestNotification testNotification = new TestNotification();
        testNotification.execute();

        //Retrieve data from web scraping
//        WebScraper webScraper = new WebScraper();
//        webScraper.execute();
//
//        latestPhoneStorage = PhoneStorage.getLatestPhone(getApplicationContext());

        //Toast.makeText(getApplicationContext(), latestPhone, Toast.LENGTH_SHORT).show();
    }

    class TestNotification extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
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

                // Load the image in the background
                Bitmap bitmap = Picasso.get().load(newPhone.getImageUrl()).get();

                String message = newPhone.getName() + "\n" +
                        newPhone.getScreenSize() + "\n" +
                        newPhone.getResolution() + "\n" +
                        newPhone.getPhoneWidth() + "\n" +
                        newPhone.getScreenType();

                // Show notification on the UI thread
                showNotification(message, bitmap);


            } catch (IOException ioe) {

            }

            return null;
        }

        private void showNotification(String message, Bitmap bitmap) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    //Build notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "channel_id")
                            .setSmallIcon(R.drawable.android_phone_icon)
                            .setContentTitle("New phone!")
                            .setContentText(message)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    //Show notification
                    notificationManager.notify(1, builder.build());
                }
            });
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
