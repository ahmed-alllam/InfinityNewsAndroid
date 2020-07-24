package com.bitnews.bitnews.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.adapters.MainViewPagerAdapter;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.ui.viewmodels.CategoryViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    CategoryViewModel categoryViewModel;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        ViewPager2 mainViewPager = findViewById(R.id.mainViewPager);
        TabLayout categoriesTabLayout = findViewById(R.id.categoriesTabLayout);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getCategoriesLiveData().observe(this, response -> {
            findViewById(R.id.progressBar3).setVisibility(View.INVISIBLE);

            if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                List<Category> categories = response.getitem().getItems();
                MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(this, categories);
                mainViewPager.setAdapter(mainViewPagerAdapter);
                new TabLayoutMediator(categoriesTabLayout, mainViewPager, true, ((tab, position) -> {
                    tab.setText(categories.get(position).getTitle());
                })).attach();

                dynamicSetTabLayoutMode(categoriesTabLayout);
            }
        });

        categoryViewModel.getFavouriteCategories(this);
    }

    public void onActionBarItemSelected(View view) {
        switch (view.getId()) {
            case R.id.navigationButton:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.searchButton:
                // start search activity
                break;
        }
    }

    private void dynamicSetTabLayoutMode(TabLayout tabLayout) {
        int tabsWidth = calculateTotalTabsWidth(tabLayout);
        int layoutWidth = tabLayout.getWidth();
        if (tabsWidth <= layoutWidth) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    private int calculateTotalTabsWidth(TabLayout tabLayout) {
        int tabWidth = 0;
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            final View view = tabLayout.getChildAt(i);
            view.measure(0, 0);
            tabWidth += view.getMeasuredWidth();
        }
        return tabWidth;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }
}
