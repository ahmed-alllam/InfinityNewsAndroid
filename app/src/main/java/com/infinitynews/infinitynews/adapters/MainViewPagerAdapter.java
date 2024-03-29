package com.infinitynews.infinitynews.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.infinitynews.infinitynews.data.models.Category;
import com.infinitynews.infinitynews.ui.fragments.PostsFragment;

import java.util.List;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private List<Category> categoriesList;

    public MainViewPagerAdapter(FragmentActivity fa, List<Category> categoriesList) {
        super(fa);
        this.categoriesList = categoriesList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new PostsFragment(categoriesList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }
}
