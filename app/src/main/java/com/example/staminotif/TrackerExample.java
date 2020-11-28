package com.example.staminotif;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class TrackerExample implements Parcelable {

    private String name;
    private int currSta;
    private int recharge;
    private int maxSta;
    private String url;
    private int id;

    public TrackerExample(int recharge, String url, String name, int maxSta) {
        this.name = name;
        this.currSta = 0;
        this.recharge = recharge;
        this.maxSta = maxSta;
        this.url = url;
        this.id = 0;
    }

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