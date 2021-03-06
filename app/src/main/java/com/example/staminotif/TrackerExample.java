package com.example.staminotif;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//Tracker examples table in the trackerExamples database
@Entity(tableName = "trackerExamples")
public class TrackerExample implements Parcelable {

    //Creating autogenerated primary key
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int ID;

    //Instantiating variables
    private String name;
    private int currSta;
    private int recharge;
    private int maxSta;
    private String dir;
    //Changing the id variable to imageResourceId as it caused confusion for the database with it's own ID.
    @ColumnInfo(name = "imageResourceId")
    private int id;

    //Constructor for a trackerexample that would be made by the database (these always have data directories)
    public TrackerExample(int recharge, String dir, String name, int maxSta) {
        this.name = name;
        this.currSta = 0;
        this.recharge = recharge;
        this.maxSta = maxSta;
        this.dir = dir;
        this.id = 0;
    }

    //Setting ignored for this constructor as this one is only used to create the custom app example in ChooseApp.java
    @Ignore
    public TrackerExample(int recharge, int id, String name, int maxSta) {
        this.name = name;
        this.currSta = 0;
        this.recharge = recharge;
        this.maxSta = maxSta;
        this.id = id;
        this.dir = "";
    }

    //Parcelable functions
    public static final Creator<TrackerExample> CREATOR = new Creator<TrackerExample>() {
        @Override
        public TrackerExample createFromParcel(Parcel in) {
            return new TrackerExample(in);
        }

        @Override
        public TrackerExample[] newArray(int size) {
            return new TrackerExample[size];
        }
    };

    //Getters and setters for the database to use
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCurrSta() {
        return currSta;
    }

    public String getDir() {
        return dir;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrSta(int currSta) {
        this.currSta = currSta;
    }

    public void setRecharge(int recharge) {
        this.recharge = recharge;
    }

    public void setMaxSta(int maxSta) {
        this.maxSta = maxSta;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public int getRecharge() {
        return recharge;
    }

    public int getMaxSta() {
        return maxSta;
    }

    public String getName() {
        return name;
    }

    public String getImageDir() {
        return dir;
    }

    protected TrackerExample(Parcel in) {
        name = in.readString();
        currSta = in.readInt();
        recharge = in.readInt();
        maxSta = in.readInt();
        dir = in.readString();
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(currSta);
        dest.writeInt(recharge);
        dest.writeInt(maxSta);
        dest.writeString(dir);
        dest.writeInt(id);
    }

    @Override
    public String toString() {
        return "TrackerExample{" +
                "name='" + name + '\'' +
                ", currSta=" + currSta +
                ", recharge=" + recharge +
                ", maxSta=" + maxSta +
                ", url='" + dir + '\'' +
                ", id=" + id +
                '}';
    }
}