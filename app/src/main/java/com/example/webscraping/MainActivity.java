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
                    String imgUrl = phonesData.select(".module-phones-link").select("img").eq(i).attr("src");
                    Log.i("PHONE_TAG", "Name: " + name + ", ImgURL: " + imgUrl);

                    //Add the phones to phone models
                    phoneModels.add(new PhoneModel(name, imgUrl));
                    publishProgress();
                }
            } catch (IOException ioe) {
                //d
            }
            return null;
        }
    }
}
