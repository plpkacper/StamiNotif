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

    private TrackerDao trackerDao;
    public List<Tracker> trackers;
    public SharedPreferences sharedPreferences;
    public Context context;
    private TrackerDatabase db;

    public TrackerUpdater(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file), context.MODE_PRIVATE);
        this.trackers = new ArrayList<>();
        this.db = TrackerDatabase.getDatabase(context);
        this.trackerDao = db.trackerDao();
        updateTrackers();
    }

    public List<Tracker> updateTrackers() {
        getFromDatabase();
        for (int i = 0; i < trackers.size(); i++) {
            trackers.get(i).updateCounter();
        }
        saveToDatabase();
        return trackers;
    }

    public List<Tracker> saveToDatabase() {
        trackerDao.insertTrackers(trackers);
        trackers = trackerDao.getAllTrackers();
        return trackers;
    }

    public List<Tracker> getFromDatabase() {
        return trackerDao.getAllTrackers();
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
        //trackers.remove(adapterPosition);
        trackerDao.delete(trackers.get(adapterPosition));
        trackers = getFromDatabase();
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
        trackers = saveToDatabase();
        return trackers;
    }

    public List<Tracker> update(Tracker tracker) {
        trackerDao.update(tracker);
        trackers = getFromDatabase();
        trackers = saveToDatabase();
        return trackers;
    }
}
