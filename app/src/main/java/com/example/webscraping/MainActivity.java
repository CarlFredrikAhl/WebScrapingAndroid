package com.example.webscraping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        phoneAdapter = new PhoneAdapter(phoneModels, this);
        recyclerView.setAdapter(phoneAdapter);

        //Retrieve data from web scraping
        WebScraper webScraper = new WebScraper();
        webScraper.execute();
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
                    //String[] test = phoneDimensions.split(" x ");
                    String phoneWidth = phoneDimensions.split(" x ")[1];

                    Log.i("PHONE_TAG", "Name: " + name + ", screen size: " + screenSize + "screen resolution: " + screenResolution
                    + "screen type" + screenType + "phone width: " + phoneWidth);

                    //Add the phones to phone models
                    phoneModels.add(new PhoneModel(name, imgUrl, "test", "test", "test", "test"));
                    publishProgress();
                }
            } catch (IOException ioe) {
                //d
            }
            return null;
        }
    }
}
