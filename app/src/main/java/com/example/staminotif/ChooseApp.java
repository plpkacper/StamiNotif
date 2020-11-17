package com.example.staminotif;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ChooseApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_app);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Choose A Template");

        List<TrackerExample> examples = new ArrayList<>();

        TrackerExample example1 = new TrackerExample(5, "res/drawable/dokkan.png", "Dokkan Battle");

        examples.add(example1);

        RecyclerView recyclerView = findViewById(R.id.rv_show_presets);
        RecyclerView.Adapter adapter = new AppGridRecyclerViewAdapter(getApplicationContext(), examples);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }
}