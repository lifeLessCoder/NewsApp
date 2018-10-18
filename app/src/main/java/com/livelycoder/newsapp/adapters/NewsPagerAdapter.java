package com.livelycoder.newsapp.adapters;

import com.livelycoder.newsapp.fragments.BusinessNewsFragment;
import com.livelycoder.newsapp.fragments.ScienceNewsFragment;
import com.livelycoder.newsapp.fragments.TechNewsFragment;
import com.livelycoder.newsapp.fragments.WorldNewsFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class NewsPagerAdapter extends FragmentPagerAdapter {
    private String[] categories = {"technology", "science", "business", "world"};
    private String[] categoryTitles = {"Tech", "Science", "Business", "World"};

    public NewsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TechNewsFragment();
            case 1:
                return new ScienceNewsFragment();
            case 2:
                return new BusinessNewsFragment();
            case 3:
                return new WorldNewsFragment();
            default:
                return new TechNewsFragment();
        }
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return categoryTitles[position];
    }
}
