package com.example.staminotif;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

//Making a singleton Tracker database
@Database(entities = {Tracker.class}, version = 1)
@TypeConverters({Converters.class})

public abstract class TrackerDatabase extends RoomDatabase {

    public abstract TrackerDao trackerDao();

    private static TrackerDatabase INSTANCE;

    public static TrackerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TrackerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TrackerDatabase.class, "tracker_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
