package com.waldo.inventory.Utils;

import java.sql.Date;
import java.util.Calendar;

public class TimeUtils {

    public static Date stripTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long time = calendar.getTimeInMillis();
        return new Date(time);
    }

    public static Date stripDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, 0);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        long time = calendar.getTimeInMillis();
        return new Date(time);
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        long time = calendar.getTimeInMillis();
        return new Date(time);
    }

}
