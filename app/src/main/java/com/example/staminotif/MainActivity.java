package com.example.staminotif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TrackerExampleDao trackerExampleDao;
    private TrackerExampleDatabase db;
    private volatile List<Tracker> trackers;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton fab;
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
        sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
        this.db = TrackerExampleDatabase.getDatabase(this);
        this.trackerExampleDao = db.trackerExampleDao();
        getExamples();
        //Made custom class with 3 functions that are often used by other classes
        trackerUpdater = new TrackerUpdater(getApplicationContext());

        fab = findViewById(R.id.fab_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("internet", "onClick: Clicked fab");
                Intent intent = new Intent(getApplicationContext(), ChooseApp.class);
                startActivity(intent);
            }
        });

        //Populating & updating the tracker list
        trackerUpdater.getFromDatabase();
        trackerUpdater.saveToDatabase();
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

    private void addApp(Bundle bundle) {
        Tracker tracker;

        tracker = new Tracker(bundle.getInt("id", 0), bundle.getString("name", "This somehow did not work"), bundle.getInt("currSta", 0), bundle.getInt("maxSta", 1), bundle.getInt("recharge", 1), bundle.getString("imageResource", ""), bundle.getBoolean("favourite"));

        if (bundle.containsKey("replace")) {
            trackerUpdater.update(tracker);
        }
        else {
            trackers.add(tracker);
        }
        trackers = trackerUpdater.saveToDatabase();
    }

    private void startWorker() {
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
    protected void onResume() {
        super.onResume();
        trackerUpdater = new TrackerUpdater(getApplicationContext());
        //Kills trackerWorker instance if one exists. Start scheduled executor service
        WorkManager.getInstance(getApplicationContext()).cancelUniqueWork("tracker");
        startWorker();
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

    private void getExamples() {
        String url = "https://staminotif.firebaseio.com/TrackerExample.json";
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final Long prevRequest = sharedPreferences.getLong("prevRequest", 0L);
        Long diff = new Date().getTime() - prevRequest;
        Log.d("exampleDatabase", "Difference for database update: " + diff);
        if (diff > 604800000) {
            trackerExampleDao.nukeTable();
            Toast.makeText(getApplicationContext(), "Updating Example Database", Toast.LENGTH_SHORT).show();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseResponse(response);
                            editor.putLong("prevRequest", new Date().getTime());
                            editor.apply();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Updating Database FAILED", Toast.LENGTH_SHORT).show();
                            Log.d("internet", "onErrorResponse: " + error.getLocalizedMessage());
                        }
                    });
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
            Toast.makeText(getApplicationContext(), "Updating Database Complete", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseResponse(String response) {
        try {
            JSONObject examplesObject = new JSONObject(response);
            for (Iterator<String> iterator = examplesObject.keys(); iterator.hasNext();) {
                String listName = iterator.next();
                JSONObject example = examplesObject.getJSONObject(listName);
                String name = example.getString("name");
                int maxSta = example.getInt("maxSta");
                int recharge = example.getInt("recharge");
                String url = example.getString("url");
                Bitmap bitmap = null;
                try {
                    bitmap = new DownloadExamples().execute(url).get();
                }
                catch (Exception e) {

                }
                finally {
                    String dir = saveImage(name, bitmap);
                    TrackerExample trackerExample = new TrackerExample(recharge, dir, name, maxSta);
                    trackerExampleDao.insert(trackerExample);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String saveImage(String name, Bitmap bitmap) {
        String imageDir = "";
        try {
            File file = new File(getFilesDir(), name + "Icon.png");

            FileOutputStream fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            imageDir = getFilesDir() + File.separator + name + "Icon.png";
            Log.d("stamina", "FILE SAVE SUCCEEDED");
        }
        catch (Exception e) {
            Log.d("stamina", "FILE SAVE FAILED");
        }

        return imageDir;
    }
}

























