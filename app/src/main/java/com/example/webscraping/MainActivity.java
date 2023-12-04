package com.example.webscraping;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    Button retrieveBtn;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        retrieveBtn = findViewById(R.id.retrieveBtn);
        retrieveBtn.setOnClickListener(view -> {
            retrieveBtn.setVisibility(View.INVISIBLE);

            //Retrieve data from web scraping
            WebScraper webScraper = new WebScraper();
            webScraper.execute();
        });
    }
}

class WebScraper extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            String url = "https://www.gsmarena.com/";
            Document doc = Jsoup.connect(url).get();
            Elements phonesData = doc.select(".module-phones-link");

            for(int i = 0; i < 5; i++) {
                String name = phonesData.get(i).text();
                Log.i("PHONE_TAG", name);
            }
        } catch (IOException ioe) {
            //d
        }
        return null;
    }
}