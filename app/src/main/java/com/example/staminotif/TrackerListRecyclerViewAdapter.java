package com.example.staminotif;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TrackerListRecyclerViewAdapter extends RecyclerView.Adapter<TrackerListRecyclerViewAdapter.TrackerViewHolder> {

    private Context context;
    private List<Tracker> trackerList;

    public TrackerListRecyclerViewAdapter(Context context, List<Tracker> trackerList) {
        Log.d("TEST1", "TrackerListRecyclerViewAdapter: We in here");
        this.context = context;
        this.trackerList = trackerList;
        Log.d("TEST1", trackerList.toString());
    }

    @NonNull
    @Override
    public TrackerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.game_list_view_item, parent, false);
        TrackerViewHolder viewHolder = new TrackerViewHolder(itemView, this);
        Log.d("TEST1", "onCreateViewHolder: MAKING STUFF HAPPEN in the first screen");

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackerViewHolder holder, int position) {

        TextView appName = holder.trackerView.findViewById(R.id.tv_item_app_name);
        appName.setText(trackerList.get(position).getName());
        ProgressBar progressBar = holder.trackerView.findViewById(R.id.pb_showstamina);
        progressBar.setMax(trackerList.get(position).getMaxSta());
        progressBar.setProgress(trackerList.get(position).getCurrSta());
        TextView staminaAmount = holder.trackerView.findViewById(R.id.tv_showstamina);
        staminaAmount.setText(trackerList.get(position).getCurrSta() + "/" + trackerList.get(position).getMaxSta());

    }

    @Override
    public int getItemCount() {
        return trackerList.size();
    }

    class TrackerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View trackerView;
        private TrackerListRecyclerViewAdapter adapter;

        public TrackerViewHolder(@NonNull View TrackerView, TrackerListRecyclerViewAdapter adapter) {
            super(TrackerView);
            this.trackerView = TrackerView;
            this.adapter = adapter;
            TrackerView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("TEST1", "onClick: WE DID IT In the home screen");
        }
    }
}
