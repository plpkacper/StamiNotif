package com.example.staminotif;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//An asynchronous task to download bitmaps from the URl's stored in my API. This stops execution of the parsing of the api to wait on a returned bitmap
//(on a different thread so the UI thread is unimpeded)
public class DownloadExamples extends AsyncTask<String, Integer, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... strings) {
        //Try make a connection with the url and start stream then return once a bitmap is created
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        //If any errors come up, return null
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
