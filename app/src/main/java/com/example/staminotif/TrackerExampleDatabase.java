package com.example.staminotif;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {TrackerExample.class}, version = 1)
@TypeConverters({Converters.class})

public abstract class TrackerExampleDatabase extends RoomDatabase {

    public abstract TrackerExampleDao trackerExampleDao();

    private static TrackerExampleDatabase INSTANCE;

    public static TrackerExampleDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TrackerExampleDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TrackerExampleDatabase.class, "tracker_example_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
