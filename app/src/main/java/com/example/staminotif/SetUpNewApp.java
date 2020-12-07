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

    //Initialising variables
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
        //If the done button in the actionbar is pressed run submitApp method.
        int id = item.getItemId();
        if (id == R.id.done) {
            submitApp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitApp() {
        //Initialising exceptions (to stop the mainactivity from being started at the end)
        int exceptionCounter = 0;
        //Creating a new bundle to later set into the intent.
        Bundle tracker = new Bundle();

        //Get text from name field in the activity and if it isn't null put it in the bundle.
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
            //If the user sets the current stamina over the maximum stamina, trigger an exception
            if (Integer.parseInt(currSta.getText().toString()) > Integer.parseInt(maxSta.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Current stamina set above max stamina, this is not possible", Toast.LENGTH_SHORT).show();
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
        Log.d("stamina", "onClick: " + imageDir);
        if (!imageDir.equals("")) {
            tracker.putString("imageResource", imageDir);
        }
        if (imageId != 0 && imageDir.equals("")) {
            tracker.putInt("imageId", imageId);
        }
        if (edit) {
            tracker.putInt("replace", position);
            tracker.putBoolean("favourite", favourite);
        }
        if (exceptionCounter == 0) {
            //If there weren't any exceptions put the bundle in the intent and start the main activity as well as finishing this one.
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("tracker", tracker);
            startActivity(intent);
            finishAffinity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_new_app);

        //Initialising variables
        edit = false;
        favourite = false;
        tID = 0;
        imageId = 0;
        imageDir = "";

        //Getting the different UI editTexts & button
        name = findViewById(R.id.et_name);
        rechargeTime = findViewById(R.id.et_staminarecharge);
        maxSta = findViewById(R.id.et_maxsta);
        currSta = findViewById(R.id.et_currsta);
        imageView = findViewById(R.id.iv_app_icon);
        search = findViewById(R.id.button_search);
        searchBox = findViewById(R.id.et_search);
        //Getting action bar to set the title in it later
        ActionBar actionBar = getSupportActionBar();

        //Adding a onclicklistener to run method with contents of the searchbox
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBox.getText().toString() != "") {
                    imageDir = lookForApp(searchBox.getText().toString());
                }
            }
        });

        //If the tracker is an example, set up the variables that are in the example
        if (getIntent().getExtras().containsKey("trackerExample")) {
            TrackerExample example = getIntent().getExtras().getParcelable("trackerExample");

            if (example.getName() != null) {
                actionBar.setTitle("Set Up " + example.getName() + " App");
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
            if (!example.getImageDir().equals("")) {
                Drawable d = Drawable.createFromPath(example.getImageDir());
                imageDir = example.getImageDir();
                imageView.setImageDrawable(d);
            }
            else if (example.getId() != 0) {
                imageId = example.getId();
                imageView.setImageResource(example.getId());
            }
        }
        //If in the intent there is a tracker, set all the variables that can be edited.
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

    //This method saves a drawable from the UI to a bitmap file in the files folder of the app.
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

    //Setting image from directory to the imageview that shows the selected apps icon.
    private void setImage(String dir) {
        Log.d("stamina", "setImage: " + dir);
        Drawable d = Drawable.createFromPath(dir);
        imageView.setImageDrawable(d);
    }

    //This method looks for an app icon from apps that are on the device
    //(An api was supposed to be used for this but one could not be found, so this is the alternative)
    private String lookForApp(String text) {
        //Log.d("stamina", "lookForApp: " + text);
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;
        final PackageManager pm = getPackageManager();
        //Get a list of the installed apps
        List<ApplicationInfo> packages = pm.getInstalledApplications(flags);
        //Going through all the apps
        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                //Log.d("stamina", "System App");
            }
            //If the app is a user installed one get the icon and save it to a file on the phone and set the directory to be so.
            else {
                //Log.d("stamina", "User Installed App");
                //Log.d("stamina", "Installed package :" + packageInfo.packageName);
                //Log.d("stamina", "Name : " + pm.getApplicationLabel(packageInfo));
                //Set all text to lower case and check if the app found contains a part of the text in the search field.
                if (pm.getApplicationLabel(packageInfo).toString().toLowerCase().contains(text.toLowerCase())) {
                    try {
                        Drawable drawable = getApplicationContext().getPackageManager().getApplicationIcon(packageInfo.packageName);
                        imageView.setImageDrawable(drawable);
                        String imageDir = saveImage(pm.getApplicationLabel(packageInfo).toString());
                        //Returning to stop looking for an app if a similar app has been found
                        return imageDir;
                    }
                    catch (PackageManager.NameNotFoundException e) {

                    }
                }
            }
        }
        //If an app hasn't been found, let the user know.
        Toast.makeText(getApplicationContext(), "App not found", Toast.LENGTH_SHORT).show();
        return "";
    }
}