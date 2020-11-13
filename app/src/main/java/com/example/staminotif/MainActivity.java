package com.example.staminotif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Tracker> trackers;
    private boolean workerStarted;
    private SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_new) {
            Intent intent = new Intent(getApplicationContext(), ChooseApp.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file), MODE_PRIVATE);
        /*
        trackers = new ArrayList<>();

        Tracker example1 = new Tracker("Dokkan", 10, 200, 1);

        trackers.add(example1);

        saveToPrefs();
        */
        Log.d("stamina", "Decoding");
        decodePrefs();

        RecyclerView recyclerView = findViewById(R.id.rv_show_app_instances);
        RecyclerView.Adapter adapter = new TrackerListRecyclerViewAdapter(getApplicationContext(), trackers);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("TEST1", "We started the recyclerview");
        //Kills trackerWorker instance to apply changes to it
        WorkManager.getInstance(getApplicationContext()).cancelUniqueWork("tracker");
        PeriodicWorkRequest updateTrackerRequest = new PeriodicWorkRequest.Builder(TrackerWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("tracker", ExistingPeriodicWorkPolicy.KEEP, updateTrackerRequest);
    }


    @Override
    public void onClick(View view) {

    }

    public void saveToPrefs() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.clear();
        prefsEditor.apply();
        Gson gson = new Gson();
        if (trackers.size() > 0) {
            for (int i = 0; i < trackers.size(); i++) {
                String json = gson.toJson(trackers.get(i));
                Log.d("stamina", "Saved " + trackers.get(i).getName());
                prefsEditor.putString("Tracker" + i, json);
            }
            prefsEditor.putInt("size", trackers.size());
            prefsEditor.apply();
        }
        else {
            Log.d("stamina", "No values to save to savedPreferences");
        }
    }


    public void decodePrefs() {
        trackers = new ArrayList<>();

        Gson gson = new Gson();
        int amountOfTrackers = sharedPreferences.getInt("size", 0);
        if (amountOfTrackers > 0) {
            for (int i = 0; i < amountOfTrackers; i++) {
                String json = sharedPreferences.getString("Tracker" + i, "FAILED");
                if (json.equals("FAILED")) {
                    Log.d("stamina", "Something went terribly wrong in decoding");
                }
                else {
                    Tracker obj = gson.fromJson(json, Tracker.class);
                    trackers.add(obj);
                }
            }
        }
        else {
            Log.d("stamina", "No values in shared preferences file");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

























