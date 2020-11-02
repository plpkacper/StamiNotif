package com.example.staminotif;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Timer {

    private Date startDate;
    private Date currentDate;
    private SimpleDateFormat formatter;

    Timer() {
        this.formatter = new SimpleDateFormat("HH:mm");
        this.startDate = new Date();
        this.currentDate = new Date();
    }

    public void getTimes() {
        Log.d("stamina", formatter.format(startDate) + "\n" + formatter.format(currentDate));
    }

    public Long getDifference(){
        long difference = Math.abs(currentDate.getTime() - startDate.getTime());
        long differenceMinutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        Log.d("stamina", "Difference In Minutes: " + (differenceMinutes));
        return differenceMinutes;
    }
}
