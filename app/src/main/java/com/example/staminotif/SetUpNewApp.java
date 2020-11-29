package com.example.staminotif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class SetUpNewApp extends AppCompatActivity {

    private EditText rechargeTime;
    private EditText maxSta;
    private EditText currSta;
    private EditText name;
    private ImageView imageView;
    private String imageDir;
    private EditText searchBox;
    private Button search;
    private Boolean edit;
    private Boolean favourite;
    private int position;
    private int tID;
    private int imageId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_up_new_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            submitApp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitApp() {
        int exceptionCounter = 0;
        Bundle tracker = new Bundle();

        try {
            if (name.getText().toString() != null) {
                tracker.putString("name", name.getText().toString());
            }
        }
        catch (NumberFormatException e){
            Toast.makeText(getApplicationContext(), "Please Enter A Name", Toast.LENGTH_SHORT).show();
            exceptionCounter++;
        }
        try {
            if (Integer.parseInt(rechargeTime.getText().toString()) != 0) {
                tracker.putInt("recharge", Integer.parseInt(rechargeTime.getText().toString()));
            }
        }
        catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Please Set Recharge Time", Toast.LENGTH_SHORT).show();
            exceptionCounter++;
        }
        try {
            if (Integer.parseInt(maxSta.getText().toString()) != 0) {
                tracker.putInt("maxSta", Integer.parseInt(maxSta.getText().toString()));
            }
        }
        catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Please Set Max Stamina", Toast.LENGTH_SHORT).show();
            exceptionCounter++;
        }
        try {
            if (Integer.parseInt(currSta.getText().toString()) > Integer.parseInt(maxSta.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Current stamina set above max stamina", Toast.LENGTH_SHORT).show();
                exceptionCounter++;
            }
            if (Integer.parseInt(currSta.getText().toString()) != 0) {
                tracker.putInt("currSta", Integer.parseInt(currSta.getText().toString()));
            }
        }
        catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Please Set Current Stamina", Toast.LENGTH_SHORT).show();
            exceptionCounter++;
        }
        if (tID >= 0) {
            tracker.putInt("id", tID);
        }
        if (!imageDir.equals("")) {
            tracker.putString("imageResource", imageDir);
        }
        if (imageId != 0) {
            tracker.putInt("imageId", imageId);
        }
        if (edit) {
            tracker.putInt("replace", position);
            tracker.putBoolean("favourite", favourite);
        }
        if (exceptionCounter == 0) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("tracker", tracker);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_new_app);

        edit = false;
        favourite = false;
        tID = 0;
        imageId = 0;
        imageDir = "";

        name = findViewById(R.id.et_name);
        rechargeTime = findViewById(R.id.et_staminarecharge);
        maxSta = findViewById(R.id.et_maxsta);
        currSta = findViewById(R.id.et_currsta);
        imageView = findViewById(R.id.iv_app_icon);
        search = findViewById(R.id.button_search);
        searchBox = findViewById(R.id.et_search);
        ActionBar actionBar = getSupportActionBar();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBox.getText().toString() != "") {
                    lookForApp(searchBox.getText().toString());
                }
            }
        });

        if (getIntent().getExtras().containsKey("trackerExample")) {
            TrackerExample example = getIntent().getExtras().getParcelable("trackerExample");

            if (example.getName() != null) {
                actionBar.setTitle("Set Up " + example.getName());
            }
            else {
                actionBar.setTitle("Set Up Your App");
            }

            if (example.getRecharge() > 0) {
                rechargeTime.setText(Integer.toString(example.getRecharge()));
            }
            if (example.getMaxSta() > 0) {
                maxSta.setText(Integer.toString(example.getMaxSta()));
            }
            if (!example.getName().equals(null)) {
                name.setText(example.getName());
            }
            //Not the right way to do it I imagine
            if (!example.getImageUrl().equals("")) {
                Drawable d = Drawable.createFromPath(example.getImageUrl());
                imageDir = example.getImageUrl();
                imageView.setImageDrawable(d);
            }
            else if (example.getId() != 0) {
                imageId = example.getId();
                imageView.setImageResource(example.getId());
            }
        }
        else if (getIntent().getExtras().containsKey("tracker")) {
            edit = true;
            Tracker tracker = getIntent().getExtras().getParcelable("tracker");
            position = getIntent().getExtras().getInt("position");
            if (tracker.getTID() >= 0) {
                tID = tracker.getTID();
            }
            if (tracker.getName() != null) {
                actionBar.setTitle("Edit " + tracker.getName());
            }
            else {
                actionBar.setTitle("Edit Your App");
            }
            if (tracker.getRecharge() > 0) {
                rechargeTime.setText(Integer.toString(tracker.getRecharge()));
            }
            if (tracker.getMaxSta() > 0) {
                maxSta.setText(Integer.toString(tracker.getMaxSta()));
            }
            if (tracker.getCurrSta() >= 0) {
                currSta.setText(Integer.toString(tracker.getCurrSta()));
            }
            if (!tracker.getName().equals(null)) {
                name.setText(tracker.getName());
            }
            if (!tracker.getImageResource().equals("")) {
                setImage(tracker.getImageResource());
                imageDir = tracker.getImageResource();
            }
            if (tracker.getImageId() != 0) {
                imageId = tracker.getImageId();
                imageView.setImageResource(tracker.getImageId());
            }
            if (tracker.isFavourite()) {
                favourite = true;
            }
        }

    }

    private String saveImage(String name) {
        imageView.setDrawingCacheEnabled(true);
        Drawable drawable = imageView.getDrawable();

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
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

    //FIX
    private void setImage(String dir) {
        Log.d("stamina", "setImage: " + dir);
        Drawable d = Drawable.createFromPath(dir);
        imageView.setImageDrawable(d);
    }

    private void lookForApp(String text) {
        Log.d("stamina", "lookForApp: " + text);
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(flags);

        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                //Log.d("stamina", "System App");
            }
            else {
                Log.d("stamina", "User Installed App");
                Log.d("stamina", "Installed package :" + packageInfo.packageName);
                Log.d("stamina", "Name : " + pm.getApplicationLabel(packageInfo));
                if (pm.getApplicationLabel(packageInfo).toString().toLowerCase().contains(text.toLowerCase())) {
                    try {
                        Drawable drawable = getApplicationContext().getPackageManager().getApplicationIcon(packageInfo.packageName);
                        imageView.setImageDrawable(drawable);
                        saveImage(pm.getApplicationLabel(packageInfo).toString());
                    }
                    catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "App not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}