package com.example.staminotif;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//TrackerExample dao, most methods are unused but could be to implement a search function in the choose app screen.
@Dao
public interface TrackerExampleDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void insert(TrackerExample trackerExample);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void insertTrackers(List<TrackerExample> trackerExampleList);

    @Query("SELECT * FROM trackerExamples")
    public List<TrackerExample> getAllTrackers();

    @Query("SELECT * FROM trackerExamples where name LIKE :name")
    public List<TrackerExample> findTrackerByName(String name);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void update(TrackerExample trackerExample);

    @Update
    public void updateTrackers(List<TrackerExample> trackerExampleList);

    @Delete
    public void delete(TrackerExample trackerExample);

    @Delete
    public void deleteTrackers(TrackerExample... trackerExamples);

    @Query("DELETE FROM trackerExamples")
    public void nukeTable();
}
