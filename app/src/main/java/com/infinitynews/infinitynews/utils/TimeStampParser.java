package com.infinitynews.infinitynews.utils;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeStampParser {
    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date());
    }

    public static Date getDateFromString(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return inputFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String parseTimeStamp(String timestamp) {
        return String.valueOf(DateUtils.getRelativeTimeSpanString(
                getDateFromString(timestamp).getTime(), System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL));
    }
}
