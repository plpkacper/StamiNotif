package com.example.staminotif;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TrackerWorker extends Worker {

    List<Tracker> trackers;
    SharedPreferences sharedPreferences;

    public TrackerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file), MODE_PRIVATE);
        decodePrefs();
        Log.d("stamina", "Started trackerWorker");
        //Testing purposes only
        trackers = new ArrayList<>();
        Tracker example1 = new Tracker("Dokkan", 10, 200, 5);
        trackers.add(example1);
    }

    @NonNull
    @Override
    public Result doWork() {

        updateTrackers();
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        updateTrackers();
        Log.d("stamina", "onStopped: We got stopped bruv");
    }

    public void updateTrackers() {
        decodePrefs();
        Log.d("stamina", "We be updating shiz yo");
        for (int i = 0; i < trackers.size(); i++) {
            trackers.get(i).updateCounter();
        }
        saveToPrefs();
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
                    Log.d("stamina", "Decoding");
                    trackers.add(obj);
                }
            }
        }
        else {
            Log.d("stamina", "No values in shared preferences file");
        }
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
}
