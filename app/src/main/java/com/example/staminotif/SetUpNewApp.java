package com.example.staminotif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SetUpNewApp extends AppCompatActivity {

    private EditText rechargeTime;
    private EditText maxSta;
    private EditText currSta;
    private EditText name;
    private ImageView imageName;
    private Boolean edit;
    private int position;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_up_new_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            submitNewApp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitNewApp() {
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
        if (imageName.getDrawable() != null) {
            tracker.putInt("imageResource", imageName.getId());
        }
        if (edit) {
            tracker.putInt("replace", position);
        }
        if (exceptionCounter == 0) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("tracker", tracker);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_new_app);

        edit = false;

        name = findViewById(R.id.et_name);
        rechargeTime = findViewById(R.id.et_staminarecharge);
        maxSta = findViewById(R.id.et_maxsta);
        currSta = findViewById(R.id.et_currsta);
        imageName = findViewById(R.id.iv_app_icon);
        ActionBar actionBar = getSupportActionBar();

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
            if (example.getImageResource() > 0) {
                imageName.setImageResource(example.getImageResource());
            }
        }
        else if (getIntent().getExtras().containsKey("tracker")) {
            edit = true;
            Tracker tracker = getIntent().getExtras().getParcelable("tracker");
            position = getIntent().getExtras().getInt("position");
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
            //Not the right way to do it I imagine
            /*
            if (tracker.getImageResource() > 0) {
                imageName.setImageResource(tracker.getImageResource());
            }
             */
        }


    }
}