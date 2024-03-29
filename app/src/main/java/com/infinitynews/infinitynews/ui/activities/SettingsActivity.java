package com.infinitynews.infinitynews.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.infinitynews.infinitynews.R;
import com.infinitynews.infinitynews.utils.PreferencesManager;

import java.util.Arrays;

public class SettingsActivity extends BaseActivity {
    private String currentLanguage;
    private float currentTextSize;
    private String[] textSizesNamesList;
    private Float[] textSizesValuesList = {0.85f, 1f, 1.1f, 1.2f};
    private TextView textSizeTextView;
    private TextView languageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textSizesNamesList = getResources().getStringArray(R.array.text_sizes);

        currentLanguage = PreferencesManager.getLanguageName(this);
        if (currentLanguage.isEmpty()) {
            currentLanguage = PreferencesManager.getSystemLanguage();
        }
        languageTextView = findViewById(R.id.language);
        languageTextView.setText(currentLanguage);

        currentTextSize = PreferencesManager.getTextSizeScale(this);
        textSizeTextView = findViewById(R.id.textSize);
        int position = Arrays.asList(textSizesValuesList).indexOf(currentTextSize);
        textSizeTextView.setText(textSizesNamesList[position]);
    }

    public void onItemClicked(View view) {
        AlertDialog.Builder dialogBuilder = (view.getId() == R.id.changeLanguageButton)
                ? getChooseLanguageDialog() : getChooseTextSizeDialog();
        dialogBuilder.show();
    }

    private AlertDialog.Builder getChooseLanguageDialog() {
        String[] languagesList = getResources().getStringArray(R.array.languages);
        int selectedPosition = Arrays.asList(languagesList).indexOf(currentLanguage);

        return new AlertDialog.Builder(this)
                .setTitle(R.string.choose_lanuage)
                .setSingleChoiceItems(languagesList, selectedPosition, (dialog, which) -> {
                    if (which != selectedPosition) {
                        currentLanguage = languagesList[which];
                        PreferencesManager.setLanguage(this, currentLanguage);
                        languageTextView.setText(currentLanguage);
                        restartActivity();
                        dialog.dismiss();
                    }
                });
    }

    private AlertDialog.Builder getChooseTextSizeDialog() {
        int selectedPosition = Arrays.asList(textSizesValuesList).indexOf(currentTextSize);

        return new AlertDialog.Builder(this)
                .setTitle(R.string.choose_text_size)
                .setSingleChoiceItems(textSizesNamesList, selectedPosition, (dialog, which) -> {
                    if (which != selectedPosition) {
                        currentTextSize = textSizesValuesList[which];
                        PreferencesManager.setTextSizeScale(this, currentTextSize);
                        textSizeTextView.setText(textSizesNamesList[which]);
                        restartActivity();
                        dialog.dismiss();
                    }
                });
    }

    private void restartActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
