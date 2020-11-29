package com.example.staminotif;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppGridRecyclerViewAdapter extends RecyclerView.Adapter<AppGridRecyclerViewAdapter.TrackerExampleViewHolder> {

    //Initialising local variables
    private Context context;
    private List<TrackerExample> examplesList;

    public AppGridRecyclerViewAdapter(Context context, List<TrackerExample> examplesList) {
        //Constructing and setting variable values
        this.context = context;
        this.examplesList = examplesList;
    }

    @NonNull
    @Override
    public TrackerExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Creating view holders
        View itemView = LayoutInflater.from(context).inflate(R.layout.choose_app_view_item, parent, false);
        TrackerExampleViewHolder viewHolder = new TrackerExampleViewHolder(itemView, this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppGridRecyclerViewAdapter.TrackerExampleViewHolder holder, int position) {
        //Getting and setting all the parts of the layout
        TextView appName = holder.TrackerExampleView.findViewById(R.id.tv_example_name);
        appName.setText(examplesList.get(position).getName());
        ImageView appIcon = holder.TrackerExampleView.findViewById(R.id.iv_background_icon);
        //Setting by resource or by directory depending on what kind of tracker it is. If created custom it will have a directory, if it has the default icon it will have a resource instead.
        if (examplesList.get(position).getId() != 0) {
            appIcon.setImageResource(examplesList.get(position).getId());
        }
        else {
            Drawable d = Drawable.createFromPath(examplesList.get(position).getImageDir());
            appIcon.setImageDrawable(d);
        }
        //This is to set up landscape vs vertical mode.
        //Getting width of the screen then setting the app icon sizes to however many are set to fit in a screen divided by the width of the screen..
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            appIcon.getLayoutParams().height = width / 5;
            appIcon.getLayoutParams().width = width / 5;
        }
        else {
            appIcon.getLayoutParams().height = width / 3;
            appIcon.getLayoutParams().width = width / 3;
        }
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
            //If a tracker example is clicked, parcel up the example and send it to the next activity.
            Intent intent = new Intent(context, SetUpNewApp.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("trackerExample", examplesList.get(getAdapterPosition()));
            context.startActivity(intent);
            //Finish this activity so if you press the back button on any app you don't go back to here instead of the main screen.
            ((Activity)context).finish();
        }
    }
}