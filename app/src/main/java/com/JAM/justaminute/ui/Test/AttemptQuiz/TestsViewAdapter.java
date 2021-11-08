package com.JAM.justaminute.ui.Test.AttemptQuiz;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;

import java.util.ArrayList;
import java.util.Map;



public class TestsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Map<String,Object>> tests;
    RecyclerViewClickInterface clickInterface;
    Context context;

    public TestsViewAdapter(ArrayList<Map<String, Object>> tests, RecyclerViewClickInterface clickInterface,Context context) {
        this.tests = tests;
        this.clickInterface = clickInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.testsview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).testname.setText(String.valueOf(tests.get(position).get("test_name")));
        ((ViewHolder) holder).time_of_creation.setText(String.valueOf(tests.get(position).get("time_of_creation")));
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView testname,time_of_creation;
        TextView space;
        ViewHolder(View itemView) {
            super(itemView);
            testname = (TextView) itemView.findViewById(R.id.test_name);
            time_of_creation = (TextView) itemView.findViewById(R.id.time_of_creation);
            space = (TextView) itemView.findViewById(R.id.space2);
            Display display = ((WindowManager)
                    context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int height = display.getHeight();
            int width = display.getWidth();
            align(height,width);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickInterface.onItemClick(getAdapterPosition());
                }
            });
        }

        private void align(int height,int width){

            space.setWidth(width*3/4);
        }
    }
}
