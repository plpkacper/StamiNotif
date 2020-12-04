package com.example.staminotif;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Initialising variables
    private TrackerExampleDao trackerExampleDao;
    private List<Tracker> trackers;
    private SharedPreferences sharedPreferences;
    public TrackerUpdater trackerUpdater;
    public RecyclerView.Adapter adapter;
    public ScheduledExecutorService executorService;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getting a menu inflater to animate the main activity on-hold menu
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Adding a button into the action bar to access the settings activity.
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Made custom class with 3 functions that are often used by other classes
        trackerUpdater = new TrackerUpdater(getApplicationContext());
        //Populating & updating the tracker list
        trackers = trackerUpdater.updateTrackers();
        sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
        //Getting database instance and getting the dao.
        TrackerExampleDatabase db = TrackerExampleDatabase.getDatabase(this);
        this.trackerExampleDao = db.trackerExampleDao();
        //Getting examples from the API
        getExamples();

        //Getting the floating action button and giving it an intent to start the chooseapp activity.
        FloatingActionButton fab = findViewById(R.id.fab_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChooseApp.class);
                startActivity(intent);
            }
        });

        //Check for intent from setting up a new app
        Intent intent = getIntent();
        if (intent.hasExtra("tracker")){
            //If there is an app to be added add it by sending the bundle of information to the method
            addApp(intent.getExtras().getBundle("tracker"));
        }

        //Create the reyclerview dependant on the orientation of the device. If the device is landscape the recyclerview is a grid view, else the rv is a linear layout.
        RecyclerView recyclerView = findViewById(R.id.rv_show_app_instances);
        adapter = new TrackerListRecyclerViewAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
        else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        //Kills trackerWorker instance if one exists. Start scheduled executor service
        WorkManager.getInstance(getApplicationContext()).cancelUniqueWork("tracker");
        startWorker();
        //Get rid of notifications if there are any (only on android versions M+)
        deleteNotifications();
    }

    //This method runs if there is an extra in the intent when starting this activity
    private void addApp(Bundle bundle) {
        //Create and instantiate new tracker with values
        Tracker tracker = new Tracker(bundle.getInt("id", 0), bundle.getString("name", "This somehow did not work"), bundle.getInt("currSta", 0), bundle.getInt("maxSta", 1), bundle.getInt("recharge", 1), bundle.getString("imageResource", ""), bundle.getBoolean("favourite"), bundle.getInt("imageId", 0));

        //If the tracker should replace a previous tracker (when a tracker is updated by the user)
        if (bundle.containsKey("replace")) {
            trackerUpdater.update(tracker);
        }
        //Else just add the tracker as a new tracker.
        else {
            trackers.add(tracker);
            //Saving the local trackers variable to database.
            trackerUpdater.saveToDatabase();
        }
        //Getting all trackers back from the database. (this is done mostly for reliability, as there is a worker thread in the background which could be messing with the tracker update).
        trackers = trackerUpdater.updateTrackers();
    }

    //This method starts an executorservice
    private void startWorker() {
        //Defining a new scheduled thread pool with 1 thread.
        executorService = Executors.newScheduledThreadPool(1);
        //Scheduling this executor to run every minute
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                //This is here to give the adapter a nudge about changes going on. Otherwise the UI will not update
                trackers.clear();
                //Populating & updating the tracker list
                trackers = trackerUpdater.updateTrackers();
                //Notify the recyclerview items that the data inside the list it is displaying has changed
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
    protected void onResume() {
        super.onResume();
        //Resetting trackerUpdater to reset values set inside it.
        trackerUpdater = new TrackerUpdater(getApplicationContext());
        //Kills trackerWorker instance if one exists. Start scheduled executor service & delete notifications
        WorkManager.getInstance(getApplicationContext()).cancelUniqueWork("tracker");
        deleteNotifications();
        startWorker();
    }

    //If the android version is higher or equal to M
    private void deleteNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get notification manager and cancel all notifications.
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.cancelAll();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d("stamina", "The activity is being killed: kill executorservice and start the periodic work request");
        //Using a method to kill the thread that is running that updates the UI.
        executorService.shutdown();
        //Start periodic work request that runs the trackerworker.
        PeriodicWorkRequest updateTrackerRequest = new PeriodicWorkRequest.Builder(TrackerWorker.class, 15, TimeUnit.MINUTES).build();
        //This will also keep an old instance of the worker around without replacing it.
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("tracker", ExistingPeriodicWorkPolicy.KEEP, updateTrackerRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //This method will run when the activity starts to update the trackerexample database.
    private void getExamples() {
        //Url to the json file
        String url = "https://staminotif.firebaseio.com/TrackerExample.json";
        //Getting the long value from the sharedpreferences file that will provide us with how much time has passed
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final long prevRequest = sharedPreferences.getLong("prevRequest", 0L);
        //Getting the difference in time between now and when the previous request was made
        long diff = new Date().getTime() - prevRequest;
        //Log.d("exampleDatabase", "Difference for database update: " + diff);
        //If the difference is over a week long
        if (diff > 604800000L) {
            //Delete all values in the trackerexample database to avoid duplicates.
            trackerExampleDao.nukeTable();
            //Show the user that the example database is being updated
            Toast.makeText(getApplicationContext(), "Updating Example Database", Toast.LENGTH_SHORT).show();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Run the method that parses the response.
                            parseResponse(response);
                            //Put in the sharedprefs file the new update time.
                            editor.putLong("prevRequest", new Date().getTime());
                            editor.apply();
                            //Show the user that updating the database is completed.
                            Toast.makeText(getApplicationContext(), "Updating Database Complete", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Updating Database FAILED", Toast.LENGTH_SHORT).show();
                            Log.d("internet", "onErrorResponse: " + error.getLocalizedMessage());
                        }
                    });
            //Make and add request to queue
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
    }

    //This method parses the response from the api call in getExamples
    private void parseResponse(String response) {
        try {
            //Create jsonobject form the response string
            JSONObject examplesObject = new JSONObject(response);
            //Iterate through the values in the jsonobject
            for (Iterator<String> iterator = examplesObject.keys(); iterator.hasNext();) {
                String listName = iterator.next();
                //Get example instance from the list and get string variables from it
                JSONObject example = examplesObject.getJSONObject(listName);
                String name = example.getString("name");
                int maxSta = example.getInt("maxSta");
                int recharge = example.getInt("recharge");
                String url = example.getString("url");
                //Initialising the bitmap that will run the asynctask
                Bitmap bitmap = null;
                try {
                    //Getting the bitmap from the asynctask
                    bitmap = new DownloadExamples().execute(url).get();
                }
                catch (Exception ignored) {}
                finally {
                    String dir = "";
                    //If the bitmap isn't null save the bitmap to local files
                    if (bitmap != null) {
                        dir = saveImage(name, bitmap);
                    }
                    //Create a new trackerexample and insert into the database.
                    TrackerExample trackerExample = new TrackerExample(recharge, dir, name, maxSta);
                    trackerExampleDao.insert(trackerExample);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //This method saves an image from a bitmap to the files folder within the apps data.
    private String saveImage(String name, Bitmap bitmap) {
        //Initialise directory
        String imageDir = "";
        try {
            //Give the file a name based on the example name and create file output stream using directory created
            File file = new File(getFilesDir(), name + "Icon.png");
            FileOutputStream fOut = new FileOutputStream(file);

            //compress the bitmap into a png.
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            //get image directory.
            imageDir = getFilesDir() + File.separator + name + "Icon.png";
            //Log.d("stamina", "FILE SAVE SUCCEEDED");
        }
        catch (Exception e) {
            //Log.d("stamina", "FILE SAVE FAILED");
        }
        return imageDir;
    }
}

























