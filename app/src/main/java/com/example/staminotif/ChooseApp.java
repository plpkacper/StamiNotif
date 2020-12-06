package com.example.staminotif;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChooseApp extends AppCompatActivity {

    //Initialise variables
    private TrackerExampleDao trackerExampleDao;
    private TrackerExampleDatabase db;
    List<TrackerExample> examples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_app);

        //Set action bar text
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle("Choose A Template");;

        //Create new ArrayList for the example list
        examples = new ArrayList<>();

        //Get trackerexample database and dao
        //then get all tracker examples from the trackerexample database
        this.db = TrackerExampleDatabase.getDatabase(this);
        this.trackerExampleDao = db.trackerExampleDao();
        examples = trackerExampleDao.getAllTrackers();

        //Replace with default drawable
        //Set the first example to be a custom example
        //This example doesn't need to be stored in the database because I want it to be in here whether the API to get examples is running or not.
        TrackerExample example1 = new TrackerExample(0, R.drawable.custom , "Custom", 0);
        examples.add(0, example1);
        //Log.d("internet", "onCreate: " + examples.toString());

        //Create recyclerview dependent on whether the phone is landscape or portrait
        RecyclerView recyclerView = findViewById(R.id.rv_show_presets);
        RecyclerView.Adapter adapter = new AppGridRecyclerViewAdapter(getApplicationContext(), examples);
        recyclerView.setAdapter(adapter);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        }
        else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
    }
}