package com.example.staminotif;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//This class was made to handle everything in/out of the database.
public class TrackerUpdater {

    //Instantiating variables
    private TrackerDao trackerDao;
    public List<Tracker> trackers;
    public SharedPreferences sharedPreferences;
    public Context context;
    private TrackerDatabase db;

    public TrackerUpdater(Context context) {
        //Setting variables
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file), context.MODE_PRIVATE);
        this.trackers = new ArrayList<>();
        this.db = TrackerDatabase.getDatabase(context);
        this.trackerDao = db.trackerDao();
        updateTrackers();
    }

    //This function updates and returns all trackers, this is the main function that runs most of the apps functions.
    public List<Tracker> updateTrackers() {
        //Get all trackers that were saved to the database
        trackers = getFromDatabase();
        //Go through each of them
        for (int i = 0; i < trackers.size(); i++) {
            //Update their currsta values.
            trackers.get(i).updateCounter();
        }
        trackers = saveToDatabase();
        return trackers;
    }

    //method that saves to database
    public List<Tracker> saveToDatabase() {
        //Inserting all trackers (which overrides old versions of them)
        trackerDao.insertTrackers(trackers);
        return trackers;
    }

    //Method that gets from database
    public List<Tracker> getFromDatabase() {
        return trackerDao.getAllTrackers();
    }

    //Method that allows editing of trackers that already exist
    public void edit(int pos) {
        //Getting the tracker to edit
        Tracker tracker = trackers.get(pos);
        //Making an intent to set up new app
        Intent intent = new Intent(context, SetUpNewApp.class);
        //Allowing this method to start new activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Putting the tracker in as an extra parcelable
        intent.putExtra("tracker", tracker);
        //Putting the position as an extra int.
        intent.putExtra("position", pos);
        //Start the intent.
        context.startActivity(intent);
    }

    //Deleting from the database
    public List<Tracker> delete(int adapterPosition) {
        trackerDao.delete(trackers.get(adapterPosition));
        trackers = getFromDatabase();
        return trackers;
    }

    //Method for setting a favourite tracker
    public List<Tracker> favourite(int adapterPosition) {
        //Getting all updated trackers
        updateTrackers();
        //For each tracker, set their favourite value to false, but the one that is being set to true/
        for (int i = 0; i < trackers.size(); i++) {
            if (i != adapterPosition) {
                trackers.get(i).setFavourite(false);
            }
            else {
                trackers.get(adapterPosition).setFavourite(!trackers.get(adapterPosition).isFavourite());
            }
        }
        //Save changes to database and return
        trackers = saveToDatabase();
        return trackers;
    }

    //Method to update a particular tracker
    public List<Tracker> update(Tracker tracker) {
        //Getting from database, updating, saving to database.
        trackerDao.update(tracker);
        trackers = getFromDatabase();
        return trackers;
    }

    //Method to get favourite tracker used in action receiver
    public Tracker getFavourite() {
        trackers = getFromDatabase();
        for (Tracker tracker : trackers) {
            if (tracker.isFavourite()) {
                return tracker;
            }
        }
        return null;
    }
}
