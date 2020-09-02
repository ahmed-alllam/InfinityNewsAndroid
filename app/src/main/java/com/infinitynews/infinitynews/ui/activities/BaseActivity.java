package com.infinitynews.infinitynews.ui.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.infinitynews.infinitynews.utils.PreferencesManager;

import java.util.Locale;

class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        applyOverrideConfiguration(new Configuration());
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        String language;
        if (!(language = PreferencesManager.getLanguageCode(this)).isEmpty()) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            overrideConfiguration.setLocale(locale);
            Resources.getSystem().updateConfiguration(overrideConfiguration, null);
        }

        float textSize;
        if ((textSize = PreferencesManager.getTextSizeScale(this)) != 1) {
            overrideConfiguration.fontScale = textSize;
        }

        super.applyOverrideConfiguration(overrideConfiguration);
    }
}
