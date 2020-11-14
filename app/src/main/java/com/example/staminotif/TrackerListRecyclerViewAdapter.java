package com.example.staminotif;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class TrackerListRecyclerViewAdapter extends RecyclerView.Adapter<TrackerListRecyclerViewAdapter.TrackerViewHolder> {

    private Context context;
    private volatile List<Tracker> trackerList;
    private TrackerUpdater trackerUpdater;

    public TrackerListRecyclerViewAdapter(Context context, List<Tracker> trackerList) {
        this.context = context;
        this.trackerUpdater = new TrackerUpdater(this.context);
        this.trackerList = trackerUpdater.updateTrackers();
    }

    @NonNull
    @Override
    public TrackerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.game_list_view_item_expanded, parent, false);
        TrackerViewHolder viewHolder = new TrackerViewHolder(itemView, this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackerViewHolder holder, final int position) {
        TextView appName = holder.trackerView.findViewById(R.id.tv_item_app_name);
        appName.setText(trackerList.get(position).getName());
        ProgressBar progressBar = holder.trackerView.findViewById(R.id.pb_showstamina);
        progressBar.setMax(trackerList.get(position).getMaxSta());
        progressBar.setProgress(trackerList.get(position).getCurrSta());
        TextView staminaAmount = holder.trackerView.findViewById(R.id.tv_showstamina);
        staminaAmount.setText(trackerList.get(position).getCurrSta() + "/" + trackerList.get(position).getMaxSta());

        Button decrement1 = holder.trackerView.findViewById(R.id.button__1sta);
        Button decrement5 = holder.trackerView.findViewById(R.id.button__5sta);
        Button decrement10 = holder.trackerView.findViewById(R.id.button__10sta);

        decrement1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackerList.get(position).decrementSta1();
                trackerList.get(position).setExpanded(true);
                notifyItemChanged(position);
                trackerUpdater.saveToPrefs();
                trackerUpdater.updateTrackers();
            }
        });

        decrement5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackerList.get(position).decrementSta5();
                trackerList.get(position).setExpanded(true);
                notifyItemChanged(position);
                trackerUpdater.saveToPrefs();
                trackerUpdater.updateTrackers();
            }
        });

        decrement10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackerList.get(position).decrementSta10();
                trackerList.get(position).setExpanded(true);
                notifyItemChanged(position);
                trackerUpdater.saveToPrefs();
                trackerUpdater.updateTrackers();
            }
        });

        trackerList = trackerUpdater.updateTrackers();
        boolean isExpanded = trackerList.get(position).isExpanded();
        Log.d("test", position + " " + isExpanded);
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return trackerList.size();
    }

    class TrackerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private View trackerView;
        private TrackerListRecyclerViewAdapter adapter;
        ConstraintLayout expandableLayout;

        public TrackerViewHolder(@NonNull View TrackerView, TrackerListRecyclerViewAdapter adapter) {
            super(TrackerView);
            this.trackerView = TrackerView;
            this.adapter = adapter;
            TrackerView.setOnClickListener(this);
            TrackerView.setOnLongClickListener(this);
            expandableLayout = trackerView.findViewById(R.id.expandableLayout);
        }

        @Override
        public void onClick(View view) {
            trackerList.get(getAdapterPosition()).setExpanded(!trackerList.get(getAdapterPosition()).isExpanded());
            notifyItemChanged(getAdapterPosition());
            trackerUpdater.saveToPrefs();
        }

        @Override
        public boolean onLongClick(View view) {
            Log.d("stamina", "onLongClick: Long Clicked on " + getAdapterPosition());

            PopupMenu popupMenu = new PopupMenu(context, trackerView);
            popupMenu.getMenuInflater().inflate(R.menu.main_recyclerview_popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    CharSequence title = menuItem.getTitle();
                    if (title.equals("Edit")) {
                        trackerUpdater.edit(getAdapterPosition());
                    }
                    else if (title.equals("Delete")) {
                        trackerList = trackerUpdater.delete(getAdapterPosition());
                        adapter.notifyDataSetChanged();
                    }
                    return false;
                }
            });
            popupMenu.show();
            return false;
        }
    }
}





















