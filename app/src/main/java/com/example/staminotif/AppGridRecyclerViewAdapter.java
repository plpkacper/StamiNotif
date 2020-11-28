package com.example.staminotif;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppGridRecyclerViewAdapter extends RecyclerView.Adapter<AppGridRecyclerViewAdapter.TrackerExampleViewHolder> {

    private Context context;
    private List<TrackerExample> examplesList;


    public AppGridRecyclerViewAdapter(Context context, List<TrackerExample> examplesList) {
        this.context = context;
        this.examplesList = examplesList;
    }

    @NonNull
    @Override
    public TrackerExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.choose_app_view_item, parent, false);
        TrackerExampleViewHolder viewHolder = new TrackerExampleViewHolder(itemView, this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppGridRecyclerViewAdapter.TrackerExampleViewHolder holder, int position) {

        TextView appName = holder.TrackerExampleView.findViewById(R.id.tv_example_name);
        appName.setText(examplesList.get(position).getName());
        ImageView appIcon = holder.TrackerExampleView.findViewById(R.id.iv_background_icon);
        if (examplesList.get(position).getId() != 0) {
            appIcon.setImageResource(examplesList.get(position).getId());
        }
        else {
            Drawable d = Drawable.createFromPath(examplesList.get(position).getImageUrl());
            appIcon.setImageDrawable(d);
        }
        //Desperation
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        appIcon.getLayoutParams().height = width/3;
        appIcon.getLayoutParams().width = width/3;
    }

    @Override
    public int getItemCount() {
        return examplesList.size();
    }

    class TrackerExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View TrackerExampleView;
        private AppGridRecyclerViewAdapter adapter;

        public TrackerExampleViewHolder(@NonNull View TrackerExampleView, AppGridRecyclerViewAdapter adapter) {
            super(TrackerExampleView);
            this.TrackerExampleView = TrackerExampleView;
            this.adapter = adapter;
            TrackerExampleView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, SetUpNewApp.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("trackerExample", examplesList.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }
}