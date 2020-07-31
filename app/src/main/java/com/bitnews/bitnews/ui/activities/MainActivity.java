package com.bitnews.bitnews.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bitnews.bitnews.R;
import com.bitnews.bitnews.adapters.MainViewPagerAdapter;
import com.bitnews.bitnews.data.models.Category;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.ui.fragments.PostsFragment;
import com.bitnews.bitnews.ui.viewmodels.CategoryViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    CategoryViewModel categoryViewModel;
    DrawerLayout drawerLayout;
    ViewPager2 mainViewPager;
    TabLayout categoriesTabLayout;
    MainViewPagerAdapter mainViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        mainViewPager = findViewById(R.id.mainViewPager);
        categoriesTabLayout = findViewById(R.id.categoriesTabLayout);
        Toolbar toolbar = findViewById(R.id.mainToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.open_nav_drawer,
                R.string.close_nav_drawer);
        toogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();


        categoriesTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (mainViewPagerAdapter != null) {
                    PostsFragment postsFragment = mainViewPagerAdapter.getFragmentAt(tab.getPosition());
                    if (postsFragment != null)
                        postsFragment.scrollToTop();
                }
            }
        });
        categoriesTabLayout.setTabTextColors(Color.GRAY, getResources().getColor(R.color.colorAccent));

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getCategoriesLiveData().observe(this, response -> {
            findViewById(R.id.progressBar3).setVisibility(View.INVISIBLE);

            if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                onSuccessfulResponse(response.getitem().getItems());
            }
        });

        categoryViewModel.getFavouriteCategories(this);
    }

    private void onSuccessfulResponse(List<Category> categories) {
        mainViewPagerAdapter = new MainViewPagerAdapter(this, categories);
        mainViewPager.setAdapter(mainViewPagerAdapter);
        new TabLayoutMediator(categoriesTabLayout, mainViewPager, true, ((tab, position) -> {
            tab.setText(categories.get(position).getTitle());
        })).attach();

        dynamicSetTabLayoutMode(categoriesTabLayout);
    }

    public void searchButtonClickListener(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
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
