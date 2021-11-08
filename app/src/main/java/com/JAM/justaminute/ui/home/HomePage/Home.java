package com.JAM.justaminute.ui.home.HomePage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.JAM.justaminute.R;
import com.google.android.material.tabs.TabLayout;

public class Home extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    View root;
    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.home_activity_main, container, false);
        viewPager = (ViewPager)root.findViewById(R.id.home_page_main);
        tabLayout = (TabLayout) root.findViewById(R.id.home_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return root;
    }

    private void setupViewPager(ViewPager viewPager) {
        HomeTabAdapter adapter = new HomeTabAdapter(root.getContext(),2,getChildFragmentManager());
        viewPager.setAdapter(adapter);
    }
}