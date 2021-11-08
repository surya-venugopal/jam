package com.JAM.justaminute.ui.home.HomePage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.JAM.justaminute.ui.home.Group.GroupFragment;
import com.JAM.justaminute.ui.home.Person.PersonFragment;

public class HomeTabAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public HomeTabAdapter(Context myContext, int totalTabs, FragmentManager fm) {
        super(fm);
        this.myContext = myContext;
        this.totalTabs = totalTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PersonFragment homeFragment = new PersonFragment();
                return homeFragment;
            case 1:
                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
