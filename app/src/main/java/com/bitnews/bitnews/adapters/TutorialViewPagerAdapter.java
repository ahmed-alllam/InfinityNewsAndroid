package com.bitnews.bitnews.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bitnews.bitnews.ui.fragments.TutorialFragment;

public class TutorialViewPagerAdapter extends FragmentStateAdapter {
    private static final int PAGES_NUM = 4;

    public TutorialViewPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new TutorialFragment(position);
    }

    @Override
    public int getItemCount() {
        return PAGES_NUM;
    }
}
