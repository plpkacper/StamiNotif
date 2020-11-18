package com.example.staminotif;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class TrackerWorker extends Worker {

    private static final String TEMP_ID = "temp";
    private static final String PERMANENT_ID = "perm";
    int faveId;
    NotificationManager notificationManager;
    NotificationManager notificationManagerPerm;
    TrackerUpdater trackerUpdater;
    List<Tracker> trackers;
    List<Integer> sentTrackers;
    Context context;
    int notificationId;
    NotificationManager notifyMgr;
    boolean permSent;

    public TrackerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.trackerUpdater = new TrackerUpdater(context);
        this.trackerUpdater.decodePrefs();
        this.context = context;
        this.faveId = 0;
        this.notificationId = 1;
        this.sentTrackers = new ArrayList<>();
        this.permSent = false;
        Log.d("stamina", "Started trackerWorker");
        createNotificationChannels();
    }

    @NonNull
    @Override
    public Result doWork() {
        trackers = trackerUpdater.updateTrackers();
        sendNotification();
        return Result.success();
    }

    private void sendNotification() {
        Log.d("notifications", "sendNotification: Sending!");
        boolean permExists = false;
        for (int i = 0; i < trackers.size(); i++) {
            if (trackers.get(i).isFavourite()) {
                permExists = true;
                Log.d("notifications", "sendNotification: Update Perm!");
                updatePermanent(i);
            }
            else if(trackers.get(i).atMax && !sentTrackers.contains(i)) {
                Log.d("notifications", "sendNotification: Send full!");
                sentTrackers.add(i);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, TEMP_ID)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(trackers.get(i).getName())
                        .setContentText(trackers.get(i).getName() + " is now full")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                if (notifyMgr == null) {
                    notificationManagerPerm.notify(notificationId, builder.build());
                }
                else {
                    notifyMgr.notify(notificationId, builder.build());
                }
                notificationId++;
            }
        }
        if (!permExists) {
            notificationManagerPerm.cancel(faveId);
        }
    }

    private void updatePermanent(int position) {
        Tracker tracker = trackers.get(position);
        NotificationCompat.Builder builderPerm = new NotificationCompat.Builder(context, PERMANENT_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(tracker.getName())
                .setContentText(tracker.getCurrSta() + "/" + tracker.getMaxSta())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);

        if (!permSent) {
            Log.d("notifications", "sendNotification: Sending initial perm!");
            if (notifyMgr == null) {
                notificationManagerPerm.notify(faveId, builderPerm.build());
            }
            else {
                notifyMgr.notify(faveId, builderPerm.build());
            }
            permSent = true;
        }
        else {
            Log.d("notifications", "sendNotification: Updating Perm!");
            if (notifyMgr == null) {
                notificationManagerPerm.notify(faveId, builderPerm.build());
            }
            else {
                notifyMgr.notify(faveId, builderPerm.build());
            }
        }

    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(TEMP_ID, name, importance);
            channel.setDescription(description);
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name_perm);
            String description = context.getString(R.string.channel_description_perm);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(PERMANENT_ID, name, importance);
            channel.setDescription(description);
            notificationManagerPerm = context.getSystemService(NotificationManager.class);
            notificationManagerPerm.createNotificationChannel(channel);
        }
        else {
            notifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
    }

    @Override
    public void onStopped() {
        super.onStopped();
        trackers = trackerUpdater.updateTrackers();
        Log.d("stamina", "onStopped: We got stopped bruv");
    }
}
