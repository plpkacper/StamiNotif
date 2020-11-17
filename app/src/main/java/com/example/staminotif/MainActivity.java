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
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private volatile List<Tracker> trackers;
    public TrackerUpdater trackerUpdater;
    public RecyclerView.Adapter adapter;
    public ScheduledExecutorService executorService;

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

        createDirectory();

        //Made custom class with 3 functions that are often used by other classes
        trackerUpdater = new TrackerUpdater(getApplicationContext());

        /*
        trackers = new ArrayList<>();

        Tracker example1 = new Tracker("Dokkan", 10, 200, 1);

        trackers.add(example1);

        trackers = trackerUpdater.saveToPrefs();
        */

        //Creating initial list of tracker objects
        trackers = new ArrayList<>();
        //Populating & updating the tracker list
        trackers = trackerUpdater.updateTrackers();

        //Check for intent from setting up new app
        Intent intent = getIntent();
        if (intent.hasExtra("tracker")){
            //If there is an app to be added add it by sending the bundle of information to the method
            addApp(intent.getExtras().getBundle("tracker"));
        }

        //Create the reyclerview
        RecyclerView recyclerView = findViewById(R.id.rv_show_app_instances);
        adapter = new TrackerListRecyclerViewAdapter(getApplicationContext(), trackers);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Kills trackerWorker instance if one exists. Start scheduled executor service
        WorkManager.getInstance(getApplicationContext()).cancelUniqueWork("tracker");
        startWorker();
    }

    private void createDirectory() {
        File mediaStorageDir = new File(getFilesDir(), "/.stamiNotif");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("stamina", "failed to create directory");
            }
        }
    }

    private void addApp(Bundle bundle) {
        Tracker tracker;
        try {
            tracker = new Tracker(bundle.getString("name", "This somehow did not work"), bundle.getInt("currSta", 0), bundle.getInt("maxSta", 1), bundle.getInt("recharge", 1), bundle.getString("imageResource", ""));
        }
        catch (RuntimeException e) {
            tracker = new Tracker(bundle.getString("name", "This somehow did not work"), bundle.getInt("currSta", 0), bundle.getInt("maxSta", 1), bundle.getInt("recharge", 1));
        }

        if (bundle.containsKey("replace")) {
            trackers.set(bundle.getInt("replace"), tracker);
        }
        else {
            trackers.add(tracker);
        }
        trackers = trackerUpdater.saveToPrefs();
    }

    private void startWorker() {
        //Change this to executorService for scheduled execution
        /*
        final Handler handler = new Handler();
        final Thread r = new Thread() {
            public void run() {
                //This line is just so that the notify data change will work. As the adapter itself only watches changes in list length, so now that the list is empty and then filled again, it can be changed.
                trackers.clear();
                //Populating & updating the tracker list
                trackers = trackerUpdater.updateTrackers();
                //Notify the recyclerview adapter that the data inside the list it is displaying has changed
                //adapter.notifyDataSetChanged();
                for (int i = 0; i < trackers.size(); i++) {
                    adapter.notifyItemChanged(i);
                }
                // Call handler function that runs this thread every 60 seconds
                handler.postDelayed(this, 60000);
            }
        };
        r.start();
         */

        executorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture scheduledFuture = executorService.schedule(new Runnable() {
            @Override
            public void run() {
                trackers.clear();
                //Populating & updating the tracker list
                trackers = trackerUpdater.updateTrackers();
                //Notify the recyclerview adapter that the data inside the list it is displaying has changed
                //adapter.notifyDataSetChanged();
                for (int i = 0; i < trackers.size(); i++) {
                    adapter.notifyItemChanged(i);
                }
            }
        }, 1, TimeUnit.MINUTES);
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("stamina", "The activity is being killed: kill executorservice and start the periodic work request");
        executorService.shutdown();
        //Code used to start a periodic work request when the app is killed
        PeriodicWorkRequest updateTrackerRequest = new PeriodicWorkRequest.Builder(TrackerWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("tracker", ExistingPeriodicWorkPolicy.KEEP, updateTrackerRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

























