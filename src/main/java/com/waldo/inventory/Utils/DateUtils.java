package com.waldo.inventory.Utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    private static final String shortDateAndTimeStr = "--/--/---- --:--";
    private static final String shortDateStr = "--/--/----";
    private static final String shortTimeStr = "--:--";
    private static final String longDateAndTimeStr = "--- ---, ---- --:--";
    private static final String longDateStr = "--- ---, ----";

    private static final SimpleDateFormat shortDateAndTime = new SimpleDateFormat("dd/MM/YYYY HH:mm");
    private static final SimpleDateFormat shortDate = new SimpleDateFormat("dd/MM/YYYY");
    private static final SimpleDateFormat shortTime = new SimpleDateFormat("HH:mm:ss");

    private static final SimpleDateFormat longDateAndTime = new SimpleDateFormat("ddd MMM, yyyy HH:mm");
    private static final SimpleDateFormat longDate = new SimpleDateFormat("ddd MMM, yyyy");



    public static String formatDateTime(Date date) {
        if (date != null) {
            return shortDateAndTime.format(date);
        } else {
            return shortDateAndTimeStr;
        }
    }

    public static String formatTime(Date date) {
        if (date != null) {
            return shortTime.format(date);
        } else {
            return shortTimeStr;
        }
    }

    public static String formatDate(Date date) {
        if (date != null) {
            return shortDate.format(date);
        } else {
            return shortDateStr;
        }
    }

    public static String formatDateTimeLong(Date date) {
        if (date != null) {
            return longDateAndTime.format(date);
        } else {
            return longDateAndTimeStr;
        }
    }

    public static String formatDateLong(Date date) {
        if (date != null) {
            return longDate.format(date);
        } else {
            return longDateStr;
        }
    }


    public static Date now() {
        return new Date(Calendar.getInstance().getTime().getTime());
    }



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
