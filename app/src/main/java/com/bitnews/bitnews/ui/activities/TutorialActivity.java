package com.bitnews.bitnews.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.viewpager2.widget.ViewPager2;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.adapters.TutorialViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TutorialActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new TutorialViewPagerAdapter(this));
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
        }).attach();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 30, 0);
            tab.requestLayout();
        }

        Button getStartedButton = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener((v) -> {
            Intent intent = new Intent(this, LoginSignupActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
