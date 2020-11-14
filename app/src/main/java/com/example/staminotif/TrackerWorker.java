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

    TrackerUpdater trackerUpdater;
    List<Tracker> trackers;

    public TrackerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        trackerUpdater = new TrackerUpdater(context);
        trackerUpdater.decodePrefs();
        Log.d("stamina", "Started trackerWorker");
        //Testing purposes only
        /*
        trackers = new ArrayList<>();
        Tracker example1 = new Tracker("Dokkan", 10, 200, 5);
        trackers.add(example1);
         */
    }

    @NonNull
    @Override
    public Result doWork() {
        trackers = trackerUpdater.updateTrackers();
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        trackers = trackerUpdater.updateTrackers();
        Log.d("stamina", "onStopped: We got stopped bruv");
    }
}
