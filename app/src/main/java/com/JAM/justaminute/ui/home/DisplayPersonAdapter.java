package com.JAM.justaminute.ui.home;
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
import com.JAM.justaminute.ui.home.Group.DisplayMembersModel;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayPersonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DisplayMembersModel> names;
    RecyclerViewClickInterface clickInterface;
    Context context;

    public DisplayPersonAdapter(ArrayList<DisplayMembersModel> names, RecyclerViewClickInterface recyclerViewClickInterface,Context context) {
        this.names = names;
        clickInterface = recyclerViewClickInterface;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_listview_displayontype, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).textViewName.setText(names.get(position).getPersonName());
        ((ViewHolder) holder).textViewroll.setText(names.get(position).getRoll());
        try{
            Picasso.get().load(names.get(position).getPersonImage()).into(((ViewHolder)holder).imageView);
        }
        catch (Exception e){
            ((ViewHolder) holder).imageView.setImageResource(R.drawable.person_white);
        }

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName,textViewroll;
        CircleImageView imageView;
        TextView space;
        ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.person_name_add);
            textViewroll = (TextView) itemView.findViewById(R.id.person_roll_add);
            imageView = (CircleImageView) itemView.findViewById(R.id.profileImage_dis);
            space = (TextView) itemView.findViewById(R.id.space);
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