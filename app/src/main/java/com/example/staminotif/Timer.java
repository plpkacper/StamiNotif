package com.example.staminotif;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Timer {

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

    public int getDifference(){
        this.currentDate = new Date().getTime();
        long difference = Math.abs(currentDate - startDate);
        long differenceMinutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        int diff = (int)differenceMinutes;
        Log.d("stamina", "Difference In Minutes: " + (diff));
        return diff;
    }

    public void updateDate() {
        this.startDate = new Date().getTime();
    }
}
