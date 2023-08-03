package com.portabull.utils.dateutils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private DateUtils() {
    }

    public static final String PORTABLE_DEFAULT_DATE = "dd-MM-yyyy HH:mm:ss";

    public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss";

    public static String getDefaultTime() {
        return new SimpleDateFormat(PORTABLE_DEFAULT_DATE).format(new Date());
    }

    public static String getTimeAsString(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static Date getDate(int days, int time) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(days, time);
        return calendar.getTime();
    }

    public static Date addTimeInMins(Integer mins) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, mins);
        return calendar.getTime();
    }

    public static String formatDate(String format, Date date) {
        return new SimpleDateFormat(format).format(date);
    }


}
