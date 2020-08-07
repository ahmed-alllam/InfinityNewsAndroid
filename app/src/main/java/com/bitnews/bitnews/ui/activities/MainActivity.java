package com.bitnews.bitnews.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.bitnews.bitnews.data.models.User;
import com.bitnews.bitnews.data.network.APIResponse;
import com.bitnews.bitnews.ui.fragments.PostsFragment;
import com.bitnews.bitnews.ui.viewmodels.CategoryViewModel;
import com.bitnews.bitnews.ui.viewmodels.UserViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ViewPager2 mainViewPager;
    private TabLayout categoriesTabLayout;
    private MainViewPagerAdapter mainViewPagerAdapter;
    private UserViewModel userViewModel;
    private boolean isNavigationViewClickable;
    private boolean isLoggedIn;

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
                toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        toogle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        categoriesTabLayout.addOnTabSelectedListener(getTabSelectedListener());
        categoriesTabLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> dynamicallySetTabLayoutMode(categoriesTabLayout));
        categoriesTabLayout.setTabTextColors(Color.GRAY, getResources().getColor(R.color.colorAccent));

        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getCategoriesLiveData().observe(this, response -> {
            findViewById(R.id.progressBar3).setVisibility(View.INVISIBLE);

            if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                onSuccessfulResponse(response.getitem().getItems());
            }
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, response -> {
            isNavigationViewClickable = true;

            if (response.getStatus() == APIResponse.Status.SUCCESFUL) {
                User user = response.getitem();
                isLoggedIn = !user.isGuest();
                if (isLoggedIn) {
                    bindNavigationViewHeader(navigationView.getHeaderView(0), user);
                    navigationView.getMenu().findItem(R.id.editProfile).setVisible(true);
                    navigationView.getMenu().findItem(R.id.logout).setVisible(true);
                }
            }
        });
        userViewModel.getCurrentUser(getApplicationContext());

        categoryViewModel.getFavouriteCategories(getApplicationContext());
    }

    private void onSuccessfulResponse(List<Category> categories) {
        mainViewPagerAdapter = new MainViewPagerAdapter(this, categories);
        mainViewPager.setAdapter(mainViewPagerAdapter);
        new TabLayoutMediator(categoriesTabLayout, mainViewPager, true, ((tab, position) -> {
            tab.setText(categories.get(position).getTitle());
        })).attach();
    }

    public void searchButtonClickListener(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private TabLayout.OnTabSelectedListener getTabSelectedListener() {
        return new TabLayout.OnTabSelectedListener() {
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
        };
    }

    private void dynamicallySetTabLayoutMode(TabLayout tabLayout) {
        int tabsWidth = calculateTotalTabsWidth(tabLayout);

        int layoutWidth = tabLayout.getWidth();

        if (tabsWidth != 0 && layoutWidth != 0) {
            if (tabsWidth <= layoutWidth) {
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
            } else {
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
        }
    }

    private int calculateTotalTabsWidth(TabLayout tabLayout) {
        int tabWidth = 0;

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tabWidth += tab.view.getWidth();
            }
        }
        return tabWidth;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (!isNavigationViewClickable)
            return false;

        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.newsSources:
                intent = new Intent(this, SourcesActivity.class);
                break;
            case R.id.changeCategoies:
                intent = new Intent(this, ChooseCategoriesActivity.class);
                break;
            case R.id.editProfile:
                intent = new Intent(this, EditProfileActivity.class);
                break;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.logout:
                userViewModel.logoutUser(getApplicationContext()).observe(this, o -> {
                    Intent loginSingupIntent = new Intent(this, LoginSignupActivity.class);
                    startActivity(loginSingupIntent);
                    finishAffinity();
                });
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }

        return true;
    }

    public void onNavigationHeaderClicked(View view) {
        if (isNavigationViewClickable && !isLoggedIn) {
            Intent intent = new Intent(this, LoginSignupActivity.class);
            intent.putExtra("fromMainActivity", true);
            startActivity(intent);
        }
    }

    private void bindNavigationViewHeader(View headerView, User user) {
        ImageView userImage = headerView.findViewById(R.id.userImage);
        if (user.getProfilePhoto() != null) {
            Glide.with(this)
                    .load(user.getProfilePhoto())
                    .placeholder(R.drawable.ic_launcher_background)
                    .dontAnimate()
                    .into(userImage);
        }

        TextView userName = headerView.findViewById(R.id.userName);
        userName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));

        TextView userEmail = headerView.findViewById(R.id.userEmail);
        userEmail.setVisibility(View.VISIBLE);
        userEmail.setText(user.getUsername());
    }

    @Override
    public void onBackPressed() {
        PostsFragment postsFragment = mainViewPagerAdapter.getFragmentAt(mainViewPager.getCurrentItem());

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers();
        else if (!postsFragment.isAtTop()) {
            postsFragment.scrollToTop();
        } else
            super.onBackPressed();
    }
}
