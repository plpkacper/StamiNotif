package com.example.staminotif;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.concurrent.TimeUnit;

//This class is instantiated in every tracker
public class Timer implements Parcelable {

    //initiate variables
    private long start;
    private long current;

    Timer() {
        //get startDate long (time in milliseconds)
        this.start = new Date().getTime();
    }

    //Used for the conversion back from tracker databse
    Timer(Long value) {
        this.start = value;
    }

    //Getting difference in minutes from the start value to the time now.
    public int getDifference(){
        this.current = new Date().getTime();
        long difference = Math.abs(current - start);
        long differenceMinutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        int diff = (int)differenceMinutes;
        //Log.d("stamina", "Difference In Minutes: " + (diff));
        return diff;
    }

    //This updates the date value so that if the apps stamina is full, after the stamina isn't full anymore it starts counting from when it isn't full and not when it was last full.
    public void updateDate() {
        this.start = new Date().getTime();
    }

    public long getStart() {
        return start;
    }

    //Parcelable functions (that aren't used anymore)
    protected Timer(Parcel in) {
        start = in.readLong();
        current = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(start);
        dest.writeLong(current);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Timer> CREATOR = new Parcelable.Creator<Timer>() {
        @Override
        public Timer createFromParcel(Parcel in) {
            return new Timer(in);
        }

        @Override
        public Timer[] newArray(int size) {
            return new Timer[size];
        }
    };
}