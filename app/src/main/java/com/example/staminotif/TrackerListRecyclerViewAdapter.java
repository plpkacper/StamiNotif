package com.example.staminotif;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TrackerListRecyclerViewAdapter extends RecyclerView.Adapter<TrackerListRecyclerViewAdapter.TrackerViewHolder> {

    //Instantiating variables
    private Context context;
    private volatile List<Tracker> trackerList;
    private TrackerUpdater trackerUpdater;

    public TrackerListRecyclerViewAdapter(Context context) {
        //Setting variables
        this.context = context;
        this.trackerUpdater = new TrackerUpdater(this.context);
        this.trackerList = trackerUpdater.updateTrackers();
    }

    @NonNull
    @Override
    public TrackerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        //Making a view holder
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            itemView = LayoutInflater.from(context).inflate(R.layout.game_list_view_item_expanded_horizontal, parent, false);
        }
        else {
            itemView = LayoutInflater.from(context).inflate(R.layout.game_list_view_item_expanded, parent, false);
        }

        TrackerViewHolder viewHolder = new TrackerViewHolder(itemView, this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackerViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        //Setting the instances colour to be different if it is a favourite
        if (trackerList.get(position).isFavourite()) {
            holder.trackerView.setBackgroundColor(Color.parseColor("#a1c5ff"));
        }
        else {
            holder.trackerView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        //Setting the icon image to be either from a directory or from a local drawable
        ImageView imageView = holder.trackerView.findViewById(R.id.iv_icon);
        if (!trackerList.get(position).getImageResource().equals("")) {
            Drawable d = Drawable.createFromPath(trackerList.get(position).getImageResource());
            imageView.setImageDrawable(d);
        }
        if (trackerList.get(position).getImageId() != 0) {
            imageView.setImageResource(trackerList.get(position).getImageId());
        }
        //Setting values for all the elements
        TextView appName = holder.trackerView.findViewById(R.id.tv_item_app_name);
        appName.setText(trackerList.get(position).getName());
        ProgressBar progressBar = holder.trackerView.findViewById(R.id.pb_showstamina);
        progressBar.setMax(trackerList.get(position).getMaxSta());
        progressBar.setProgress(trackerList.get(position).getCurrSta());
        TextView staminaAmount = holder.trackerView.findViewById(R.id.tv_showstamina);
        staminaAmount.setText(trackerList.get(position).getCurrSta() + "/" + trackerList.get(position).getMaxSta());

        //Getting the decrement buttons
        Button decrement1 = holder.trackerView.findViewById(R.id.but_sta_one);
        Button decrement5 = holder.trackerView.findViewById(R.id.but_sta_five);
        Button decrement10 = holder.trackerView.findViewById(R.id.but_sta_ten);

        //Giving each button an OnClickListener
        decrement1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackerList.get(position).decrementSta1();
                trackerUpdater.saveToDatabase();
                trackerList = trackerUpdater.updateTrackers();
                notifyItemChanged(position);
            }
        });

        decrement5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackerList.get(position).decrementSta5();
                trackerUpdater.saveToDatabase();
                trackerList = trackerUpdater.updateTrackers();
                notifyItemChanged(position);
            }
        });

        decrement10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackerList.get(position).decrementSta10();
                trackerUpdater.saveToDatabase();
                trackerList = trackerUpdater.updateTrackers();
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trackerList.size();
    }

    class TrackerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private View trackerView;
        private TrackerListRecyclerViewAdapter adapter;

        public TrackerViewHolder(@NonNull View TrackerView, TrackerListRecyclerViewAdapter adapter) {
            super(TrackerView);
            this.trackerView = TrackerView;
            this.adapter = adapter;
            //TrackerView.setOnClickListener(this);
            TrackerView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Log.d("stamina", "onClick: " + trackerList.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View view) {

            //When the tracker is held for a couple seconds it makes a popup menu.
            PopupMenu popupMenu = new PopupMenu(context, trackerView);
            popupMenu.getMenuInflater().inflate(R.menu.main_recyclerview_popup_menu, popupMenu.getMenu());

            //handling options in a menu item click listener
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    CharSequence title = menuItem.getTitle();
                    //If title equals something, do a function on trackerUpdater. Notify if a change has been made and if necessary.
                    if (title.equals("Edit")) {
                        trackerUpdater.edit(getAdapterPosition());
                    }
                    else if (title.equals("Delete")) {
                        trackerUpdater.delete(getAdapterPosition());
                        trackerList = trackerUpdater.updateTrackers();
                        adapter.notifyDataSetChanged();
                    }
                    else if (title.equals("Favourite")) {
                        trackerList = trackerUpdater.favourite(getAdapterPosition());
                        trackerUpdater.saveToDatabase();
                        trackerList = trackerUpdater.updateTrackers();
                        Log.d("stamina", "onMenuItemClick: " + trackerList.get(getAdapterPosition()));
                        adapter.notifyDataSetChanged();
                    }
                    return false;
                }
            });
            //Show the pop up menu
            popupMenu.show();
            //return from the function
            return false;
        }
    }
}





















