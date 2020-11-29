package com.example.staminotif;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//Dao for the tracker database, most methods aren't used but could be in the future.
@Dao
public interface TrackerDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void insert(Tracker tracker);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void insertTrackers(List<Tracker> tracker);

    @Query("SELECT * FROM TRACKERS")
    public List<Tracker> getAllTrackers();

    @Query("SELECT * FROM trackers where name LIKE :name")
    public List<Tracker> findTrackerByName(String name);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void update(Tracker tracker);

    @Update
    public void updateTrackers(List<Tracker> tracker);

    @Delete
    public void delete(Tracker tracker);

    @Delete
    public void deleteTrackers(Tracker... tracker);

    @Query("DELETE FROM trackers")
    public void nukeTable();
}
