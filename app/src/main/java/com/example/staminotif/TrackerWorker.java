package com.example.staminotif;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class TrackerWorker extends Worker {

    private static final String TEMP_ID = "temp";
    private static final String PERMANENT_ID = "perm";
    private int faveId;
    private NotificationManager notificationManager;
    private TrackerUpdater trackerUpdater;
    private List<Tracker> trackers;
    private List<Integer> sentTrackers;
    private Context context;
    private int notificationId;
    private NotificationManager notifyMgr;
    private boolean permSent;
    private PendingIntent pendingIntent;
    private SharedPreferences sharedPreferences;
    private int button1;
    private int button2;
    private PendingIntent pIntentDecrease1;
    private PendingIntent pIntentDecrease2;

    public TrackerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.trackerUpdater = new TrackerUpdater(context);
        this.trackerUpdater.getFromDatabase();
        this.context = context;
        this.faveId = 0;
        this.notificationId = 1;
        this.sentTrackers = new ArrayList<>();
        this.permSent = false;
        Intent intent = new Intent(context, MainActivity.class);
        this.pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        sharedPreferences = context.getSharedPreferences("default", MODE_PRIVATE);
        this.button1 = sharedPreferences.getInt("notif1", 10);
        this.button2 = sharedPreferences.getInt("notif2", 25);
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
            if(trackers.get(i).atMax && !sentTrackers.contains(i)) {
                Log.d("notifications", "sendNotification: Send full!");
                sentTrackers.add(i);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, TEMP_ID)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(trackers.get(i).getName())
                        .setContentText(trackers.get(i).getName() + " is now full")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(null)
                        .setContentIntent(pendingIntent)
                        .setVibrate(null);
                if (notifyMgr == null) {
                    notificationManager.notify(notificationId, builder.build());
                }
                else {
                    notifyMgr.notify(notificationId, builder.build());
                }
                notificationId++;
            }
        }
        if (!permExists) {
            notificationManager.cancel(faveId);
        }
    }

    private void updatePermanent(int position) {
        Intent intentAction1 = new Intent(context,ActionReceiver.class);
        intentAction1.putExtra("button1", button1);
        intentAction1.putExtra("pressed", "button1");
        intentAction1.putExtra("button2", button2);
        Intent intentAction2 = new Intent(context,ActionReceiver.class);
        intentAction2.putExtra("button2", button2);
        intentAction2.putExtra("button1", button1);
        intentAction2.putExtra("pressed", "button2");
        pIntentDecrease1 = PendingIntent.getBroadcast(context,1,intentAction1,PendingIntent.FLAG_UPDATE_CURRENT);
        pIntentDecrease2 = PendingIntent.getBroadcast(context,2,intentAction2,PendingIntent.FLAG_UPDATE_CURRENT);
        Tracker tracker = trackers.get(position);
        NotificationCompat.Builder builderPerm = new NotificationCompat.Builder(context, PERMANENT_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(tracker.getName())
                .setContentText(tracker.getCurrSta() + "/" + tracker.getMaxSta())
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.minus, "-" + button1, pIntentDecrease1)
                .addAction(R.drawable.minus, "-" + button2, pIntentDecrease2)
                .setOngoing(true);

        if (!permSent) {
            Log.d("notifications", "sendNotification: Sending initial perm!");
            if (notifyMgr == null) {
                notificationManager.notify(faveId, builderPerm.build());
            }
            else {
                notifyMgr.notify(faveId, builderPerm.build());
            }
            permSent = true;
        }
        else {
            Log.d("notifications", "sendNotification: Updating Perm!");
            if (notifyMgr == null) {
                notificationManager.notify(faveId, builderPerm.build());
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
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel1 = new NotificationChannel(PERMANENT_ID, name, importance);
            channel1.setDescription(description);
            channel1.setShowBadge(false);
            channel1.setSound(null, null);
            channel1.enableVibration(false);
            channel1.setLockscreenVisibility(NotificationManager.IMPORTANCE_NONE);
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
        }
        else {
            notifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
    }

    @Override
    public void onStopped() {
        super.onStopped();
        trackers = trackerUpdater.updateTrackers();
        notificationManager.cancel(faveId);
        Log.d("stamina", "onStopped: Service Stopped");
    }
}
