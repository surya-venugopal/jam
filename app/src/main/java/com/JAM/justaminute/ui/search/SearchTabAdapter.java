package com.JAM.justaminute.ui.search;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.JAM.justaminute.ui.search.Feed.FeedFragment;
import com.JAM.justaminute.ui.search.Forum.Forum;

public class SearchTabAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public SearchTabAdapter(Context myContext, int totalTabs, FragmentManager fm) {
        super(fm);
        this.myContext = myContext;
        this.totalTabs = totalTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FeedFragment feedFragment = new FeedFragment();
                return feedFragment;
            case 1:
                Forum forum = new Forum();
                return forum;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
