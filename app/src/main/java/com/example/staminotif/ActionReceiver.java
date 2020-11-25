package com.example.staminotif;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class ActionReceiver extends BroadcastReceiver {

    private TrackerUpdater trackerUpdater;
    private Tracker fave;
    private int action1;
    private int action2;
    private Context context;
    private PendingIntent pendingIntent;
    private PendingIntent pIntentDecrease1;
    private PendingIntent pIntentDecrease2;
    private static final String PERMANENT_ID = "perm";
    private int faveId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        trackerUpdater = new TrackerUpdater(context);
        fave = trackerUpdater.getFavourite();
        Intent intent1 = new Intent(context, MainActivity.class);
        this.pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
        try {
            action1 = intent.getIntExtra("button1", 0);
        }
        catch (Exception e) {
            action1 = 0;
        }
        try {
            action2 = intent.getIntExtra("button2", 0);
        }
        catch (Exception e) {
            action2 = 0;
        }
        String pressed = intent.getStringExtra("pressed");
        Log.d("buttonsNotification", "onReceive: " + pressed);
        if(pressed.equals("button1")){
            performAction1(action1);
        }
        else if(pressed.equals("button2")){
            performAction2(action2);
        }
    }

    public void performAction1(int action1){
        Log.d("buttonsNotification", "performAction1: We did it?");
        if (fave != null) {
            fave.decrementStaValue(action1);
            trackerUpdater.saveToDatabase();
            trackerUpdater.updateTrackers();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                updateNotification();
            }
        }
        else {
            Toast.makeText(context, "This action wasn't possible", Toast.LENGTH_SHORT).show();
        }
    }

    public void performAction2(int action2){
        Log.d("buttonsNotification", "performAction2: We did it?");
        if (fave != null) {
            fave.decrementStaValue(action2);
            trackerUpdater.saveToDatabase();
            trackerUpdater.updateTrackers();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                updateNotification();
            }
        }
        else {
            Toast.makeText(context, "This action wasn't possible", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateNotification() {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        Intent intentAction1 = new Intent(context,ActionReceiver.class);
        intentAction1.putExtra("button1", action1);
        intentAction1.putExtra("pressed", "button1");
        intentAction1.putExtra("button2", action2);
        Intent intentAction2 = new Intent(context,ActionReceiver.class);
        intentAction2.putExtra("button2", action2);
        intentAction2.putExtra("button1", action1);
        intentAction2.putExtra("pressed", "button2");
        pIntentDecrease1 = PendingIntent.getBroadcast(context,1,intentAction1,PendingIntent.FLAG_UPDATE_CURRENT);
        pIntentDecrease2 = PendingIntent.getBroadcast(context,2,intentAction2,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builderPerm = new NotificationCompat.Builder(context, PERMANENT_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(fave.getName())
                .setContentText(fave.getCurrSta() + "/" + fave.getMaxSta())
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.minus, "-" + action1, pIntentDecrease1)
                .addAction(R.drawable.minus, "-" + action2, pIntentDecrease2)
                .setOngoing(true);
        notificationManager.notify(faveId, builderPerm.build());
    }
}