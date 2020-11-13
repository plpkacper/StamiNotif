package com.example.staminotif;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class SetUpNewApp extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_up_new_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            Log.d("TEST1", "onOptionsItemSelected: We done baby");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_new_app);

        TrackerExample example = getIntent().getExtras().getParcelable("trackerExample");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Set Up " + example.getName() + " App");

        EditText rechargeTime = findViewById(R.id.et_staminarecharge);
        rechargeTime.setText(Integer.toString(example.getRecharge()));
        Log.d("TEST1", "onCreate: " + example.toString());
    }
}