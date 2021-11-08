package com.JAM.justaminute.ui.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.Storage;
import com.JAM.justaminute.ui.home.MyProfile.Profile;
import com.google.android.material.tabs.TabLayout;

public class SearchFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    View root;
    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_search, container, false);

        viewPager = (ViewPager)root.findViewById(R.id.student_feed_main);
        tabLayout = (TabLayout) root.findViewById(R.id.student_feed_tab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setHasOptionsMenu(true);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                Intent intent=new Intent(root.getContext(), Profile.class);
                startActivity(intent);
                break;
            case R.id.storage:
                Intent intent1 = new Intent(root.getContext(), Storage.class);
                startActivity(intent1);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        SearchTabAdapter adapter = new SearchTabAdapter(root.getContext(),2,getChildFragmentManager());
        viewPager.setAdapter(adapter);
    }
}
