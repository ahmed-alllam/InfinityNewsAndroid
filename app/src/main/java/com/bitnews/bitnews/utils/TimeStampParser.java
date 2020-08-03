package com.bitnews.bitnews.utils;

import android.content.Context;
import android.os.Build;
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

    public static Date getDateFromString(Context context, String timestamp) {
        Locale currentLocale;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            currentLocale = context.getResources().getConfiguration().locale;
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", currentLocale);
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            return inputFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String parseTimeStamp(Context context, String timestamp) {
        return String.valueOf(DateUtils.getRelativeTimeSpanString(
                getDateFromString(context, timestamp).getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
    }
}
