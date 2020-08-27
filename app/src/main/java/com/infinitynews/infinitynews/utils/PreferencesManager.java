package com.infinitynews.infinitynews.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class PreferencesManager {
    public static final String LANGAUGE_PEREFERENCE = "LANGUAGE";
    public static final String TEXT_SCALE_PEREFERENCE = "TEXT_SCALE";

    public static String getLanguageName(Context context) {
        return getPreference(context, LANGAUGE_PEREFERENCE, "");
    }


    public static String getLanguageCode(Context context) {
        String languageName = getLanguageName(context);
        return convertLanguageNameToCode(languageName);
    }

    public static String convertLanguageNameToCode(String languageName) {
        if (languageName.isEmpty()) {
            return "";
        }

        for (Locale locale : Locale.getAvailableLocales()) {
            if (languageName.equals(locale.getDisplayLanguage(locale))) {
                return locale.getLanguage();
            }
        }

        return Locale.getDefault().getLanguage();
    }

    public static String getSystemLanguage() {
        return Locale.getDefault().getDisplayLanguage(Locale.getDefault());
    }

    public static void setLanguage(Context context, String languageCode) {
        setPreference(context, LANGAUGE_PEREFERENCE, languageCode);
    }

    public static float getTextSizeScale(Context context) {
        return Float.parseFloat(getPreference(context, TEXT_SCALE_PEREFERENCE, String.valueOf(1)));
    }

    public static void setTextSizeScale(Context context, float textSizeScale) {
        setPreference(context, TEXT_SCALE_PEREFERENCE, String.valueOf(textSizeScale));
    }

    private static String getPreference(Context context, String preference, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences("appPreferences", Activity.MODE_PRIVATE);
        return prefs.getString(preference, defaultValue);
    }

    private static void setPreference(Context context, String preference, String value) {
        SharedPreferences prefs = context.getSharedPreferences("appPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(preference, value);
        editor.apply();
    }
}
