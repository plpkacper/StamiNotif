package com.example.staminotif;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TrackerUpdater {

    public List<Tracker> trackers;
    public SharedPreferences sharedPreferences;
    public Context context;

    public TrackerUpdater(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file), context.MODE_PRIVATE);
        this.trackers = new ArrayList<>();
        updateTrackers();
    }

    public List<Tracker> updateTrackers() {
        decodePrefs();
        for (int i = 0; i < trackers.size(); i++) {
            trackers.get(i).updateCounter();
        }
        saveToPrefs();
        return trackers;
    }

    public List<Tracker> saveToPrefs() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.clear();
        prefsEditor.apply();
        Gson gson = new Gson();
        if (trackers.size() > 0) {
            for (int i = 0; i < trackers.size(); i++) {
                String json = gson.toJson(trackers.get(i));
                prefsEditor.putString("Tracker" + i, json);
            }
            prefsEditor.putInt("size", trackers.size());
            prefsEditor.apply();
        }
        else {
            Log.d("stamina", "No values to save to savedPreferences");
        }
        return trackers;
    }

    public List<Tracker> decodePrefs() {
        trackers.clear();
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
        return trackers;
    }

    public void edit(int adapterPosition) {
        Tracker tracker = trackers.get(adapterPosition);
        Intent intent = new Intent(context, SetUpNewApp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("tracker", tracker);
        intent.putExtra("position", adapterPosition);
        context.startActivity(intent);
    }

    public List<Tracker> delete(int adapterPosition) {
        trackers.remove(adapterPosition);
        saveToPrefs();
        decodePrefs();
        return trackers;
    }

    public List<Tracker> favourite(int adapterPosition) {
        updateTrackers();
        for (int i = 0; i < trackers.size(); i++) {
            if (i != adapterPosition) {
                trackers.get(i).setFavourite(false);
            }
            else {
                trackers.get(adapterPosition).setFavourite(!trackers.get(adapterPosition).isFavourite());
            }
        }
        return trackers;
    }
}
