package com.example.staminotif;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Set;

public class Settings extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText notif1;
    private EditText notif2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //getApplicationContext();
        sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);

        int value1 = sharedPreferences.getInt("notif1", 0);
        int value2 = sharedPreferences.getInt("notif2", 0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");

        notif1 = findViewById(R.id.et_notif_button_1);
        if (value1 > 0) {
            notif1.setText(Integer.toString(value1));
        }
        notif2 = findViewById(R.id.et_notif_button_2);
        if (value2 > 0) {
            notif2.setText(Integer.toString(value2));
        }
        Button apply = findViewById(R.id.but_apply);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToPrefs();
            }
        });
    }

    private void saveToPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!notif1.getText().toString().equals("")) {
            editor.putInt("notif1", Integer.parseInt(notif1.getText().toString()));
        }
        else {
            editor.putInt("notif1", 1);
        }
        if (!notif2.getText().toString().equals("")) {
            editor.putInt("notif2", Integer.parseInt(notif2.getText().toString()));
        }
        else {
            editor.putInt("notif2", 0);
        }
        editor.apply();
        Toast.makeText(getApplicationContext(), "Applied!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}