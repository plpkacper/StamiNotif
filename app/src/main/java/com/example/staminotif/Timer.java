package com.example.staminotif;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Timer implements Parcelable {

    private long startDate;
    private long currentDate;

    Timer() {
        /*For testing purposes
        String fakeDate = "2020/11/09 10:00:00";
        this.formatter2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            this.startDate = formatter2.parse(fakeDate);
        } catch (ParseException e) {

        }
        */
        this.startDate = new Date().getTime();
    }

    Timer(Long value) {
        this.startDate = value;
    }

    public int getDifference(){
        this.currentDate = new Date().getTime();
        long difference = Math.abs(currentDate - startDate);
        long differenceMinutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        int diff = (int)differenceMinutes;
        //Log.d("stamina", "Difference In Minutes: " + (diff));
        return diff;
    }

    public void updateDate() {
        this.startDate = new Date().getTime();
    }

    public long getStartDate() {
        return startDate;
    }

    protected Timer(Parcel in) {
        startDate = in.readLong();
        currentDate = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startDate);
        dest.writeLong(currentDate);
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