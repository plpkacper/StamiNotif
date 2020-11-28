package com.example.staminotif;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "trackerExamples")
public class TrackerExample implements Parcelable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int ID;
    private String name;
    private int currSta;
    private int recharge;
    private int maxSta;
    private String url;
    @ColumnInfo(name = "imageResourceId")
    private int id;

    public TrackerExample(int recharge, String url, String name, int maxSta) {
        this.name = name;
        this.currSta = 0;
        this.recharge = recharge;
        this.maxSta = maxSta;
        this.url = url;
        this.id = 0;
    }
    @Ignore
    public TrackerExample(int recharge, int id, String name, int maxSta) {
        this.name = name;
        this.currSta = 0;
        this.recharge = recharge;
        this.maxSta = maxSta;
        this.id = id;
        this.url = "";
    }

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

    public String getUrl() {
        return url;
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

    public void setUrl(String url) {
        this.url = url;
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

    public String getImageUrl() {
        return url;
    }

    protected TrackerExample(Parcel in) {
        name = in.readString();
        currSta = in.readInt();
        recharge = in.readInt();
        maxSta = in.readInt();
        url = in.readString();
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
        dest.writeString(url);
        dest.writeInt(id);
    }

    @Override
    public String toString() {
        return "TrackerExample{" +
                "name='" + name + '\'' +
                ", currSta=" + currSta +
                ", recharge=" + recharge +
                ", maxSta=" + maxSta +
                ", url='" + url + '\'' +
                ", id=" + id +
                '}';
    }
}