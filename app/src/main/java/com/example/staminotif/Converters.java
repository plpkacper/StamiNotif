package com.example.staminotif;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

public class Converters {

    @TypeConverter
    public static Timer fromTimer(Long value) {
        return value == null ? null : new Timer(value);
    }

    @TypeConverter
    public static Long toTimer(Timer timer) {
        return timer.getStartDate();
    }

}
