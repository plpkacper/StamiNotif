package com.example.staminotif;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List trackerList;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Tracker> trackerList = new ArrayList<>();

        addButton = findViewById(R.id.button_add_new);
        addButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_add_new) {
            Intent intent = new Intent(getApplicationContext(), ChooseApp.class);
            startActivity(intent);
        }
    }
}