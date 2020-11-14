package com.example.staminotif;

import android.os.Parcel;
import android.os.Parcelable;

public class TrackerExample implements Parcelable {

    private String name;
    private int currSta;
    private int recharge;
    private int maxSta;
    private int imageName;

    public TrackerExample(int recharge, int imageName, String name) {
        this.name = name;
        this.currSta = 0;
        this.recharge = recharge;
        this.maxSta = 0;
        this.imageName = imageName;
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

    public int getRecharge() {
        return recharge;
    }

    public int getMaxSta() {
        return maxSta;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageName;
    }

    protected TrackerExample(Parcel in) {
        name = in.readString();
        currSta = in.readInt();
        recharge = in.readInt();
        maxSta = in.readInt();
        imageName = in.readInt();
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
        dest.writeInt(imageName);
    }

    @Override
    public String toString() {
        return "TrackerExample{" +
                "name='" + name + '\'' +
                ", currSta=" + currSta +
                ", recharge=" + recharge +
                ", maxSta=" + maxSta +
                ", imageName='" + imageName + '\'' +
                '}';
    }
}