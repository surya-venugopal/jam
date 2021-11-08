package com.JAM.justaminute.ui.Test.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.Storage;
import com.JAM.justaminute.ui.Test.AttemptQuiz.ViewGroupsAttempt;
import com.JAM.justaminute.ui.Test.Create_Quiz.ViewGroupsCreate;
import com.JAM.justaminute.ui.home.MyProfile.Profile;

public class TestFragment extends Fragment  {

    View root;
    Button view_groups_create,view_groups_attempt,analyse;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_test, container, false);
        view_groups_create = (Button) root.findViewById(R.id.view_groups_for_test);
        view_groups_attempt = (Button)root.findViewById(R.id.view_groups_attempt);
        analyse = (Button)root.findViewById(R.id.analyse);
        setHasOptionsMenu(true);

        Display display = ((WindowManager)
                getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();

        view_groups_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(root.getContext(), ViewGroupsCreate.class);
                startActivity(i);
            }
        });

        view_groups_attempt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(root.getContext(), ViewGroupsAttempt.class);
                startActivity(i);
            }
        });
        align(height,width);
        return root;
    }

    void align(int height,int width){
        view_groups_create.setWidth((width/2)-10);
        view_groups_create.setHeight(width/2);

        view_groups_attempt.setHeight(width/2);

        analyse.setHeight(width/2);
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
}
