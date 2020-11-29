package com.example.staminotif;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class TrackerWorker extends Worker {

    //Instantiate all variables
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
    private PendingIntent pendingIntent;
    private SharedPreferences sharedPreferences;
    private int button1value;
    private int button2value;
    private PendingIntent pIntentDecrease1;
    private PendingIntent pIntentDecrease2;

    public TrackerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        //Set all variables
        super(context, workerParams);
        this.trackerUpdater = new TrackerUpdater(context);
        this.trackerUpdater.getFromDatabase();
        this.context = context;
        this.faveId = 0;
        this.notificationId = 1;
        this.sentTrackers = new ArrayList<>();
        //Create intent and pendingintent using it (for when the notification is tapped to open this app).
        Intent intent = new Intent(context, MainActivity.class);
        this.pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        //Getting sharedprefs and getting notification button settings from it.
        sharedPreferences = context.getSharedPreferences("default", MODE_PRIVATE);
        this.button1value = sharedPreferences.getInt("notif1", 10);
        this.button2value = sharedPreferences.getInt("notif2", 25);
        Log.d("worker", "Started trackerWorker");
        //Method that creates the notification channels we want.
        createNotificationChannels();
    }

    @NonNull
    @Override
    //This function runs every 15 minutes (as set by mainActivity when it is stopped). It updates the trackers and sends out notifications.
    public Result doWork() {
        trackers = trackerUpdater.updateTrackers();
        sendNotification();
        return Result.success();
    }

    //Method that sends notifications.
    private void sendNotification() {
        Log.d("notifications", "sendNotification: Sending!");
        //initially setting permExists to false
        boolean permExists = false;
        //For each tracker in trackers
        for (int i = 0; i < trackers.size(); i++) {
            //Getting the favourite tracker and updating/creating its notification
            if (trackers.get(i).isFavourite()) {
                permExists = true;
                Log.d("notifications", "sendNotification: Update Perm!");
                updatePermanent(i);
            }
            //Checking if the tracker is at max and hasn't had a notification sent yet.
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
        //If the favourite tracker doesn't exist cancel it's notification.
        if (!permExists) {
            notificationManager.cancel(faveId);
        }
    }

    private void updatePermanent(int position) {
        //Creating intents and builder for the permanent notification
        Intent intentAction1 = new Intent(context, NotificationActionReceiver.class);
        intentAction1.putExtra("button1", button1value);
        intentAction1.putExtra("pressed", "button1");
        intentAction1.putExtra("button2", button2value);
        Intent intentAction2 = new Intent(context, NotificationActionReceiver.class);
        intentAction2.putExtra("button2", button2value);
        intentAction2.putExtra("button1", button1value);
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
                .addAction(R.drawable.minus, "-" + button1value, pIntentDecrease1)
                .addAction(R.drawable.minus, "-" + button2value, pIntentDecrease2)
                .setOngoing(true);

        //If the notification hasn't been sent, send it. If it has, update it. This if does the same thing on both sides.
        Log.d("notifications", "sendNotification: Sending perm!");
        if (notifyMgr == null) {
            notificationManager.notify(faveId, builderPerm.build());
        }
        else {
            //This runs when an old version of android (pre android Oreo) is used
            notifyMgr.notify(faveId, builderPerm.build());
        }

    }

    private void createNotificationChannels() {
        //Create the different notification channels with their values.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name_perm);
            String description = context.getString(R.string.channel_description_perm);
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel1 = new NotificationChannel(PERMANENT_ID, name, importance);
            channel1.setDescription(description);
            //Most of these settings don't work. But we keep them here in case they ever want to work.
            channel1.setShowBadge(false);
            channel1.setSound(null, null);
            channel1.enableVibration(false);
            channel1.setLockscreenVisibility(NotificationManager.IMPORTANCE_NONE);
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(TEMP_ID, name, importance);
            channel.setDescription(description);
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        else {
            //Create the old version of the notification manager.
            notifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
    }

    @Override
    //If the preiodic work request doesn't work, delete all the notifications and update the user.
    public void onStopped() {
        super.onStopped();
        trackers = trackerUpdater.updateTrackers();
        notificationManager.cancelAll();
        Toast.makeText(getApplicationContext(), "StamiNotif Service Killed", Toast.LENGTH_SHORT).show();
        Log.d("worker", "onStopped: Service Stopped");
    }
}
