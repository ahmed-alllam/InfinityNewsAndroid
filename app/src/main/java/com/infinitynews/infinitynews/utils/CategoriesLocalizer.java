package com.infinitynews.infinitynews.utils;

import android.content.Context;
import android.content.res.Configuration;

import com.infinitynews.infinitynews.R;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CategoriesLocalizer {
    public static String getLocalizedCategoryTitle(Context context, String englishTitle) {
        int position = getEnglishStringsList(context).indexOf(englishTitle);

        if (position >= 0)
            return getCurrentLocaleStringsList(context).get(position);

        return "NaN";
    }

    private static List<String> getEnglishStringsList(Context context) {
        Configuration englishConfiguraion = new Configuration();
        englishConfiguraion.setLocale(new Locale("en"));
        return Arrays.asList(context.createConfigurationContext(englishConfiguraion)
                .getResources().getStringArray(R.array.categories_list));
    }

    private static List<String> getCurrentLocaleStringsList(Context context) {
        return Arrays.asList(context.getResources().getStringArray(R.array.categories_list));
    }
}
