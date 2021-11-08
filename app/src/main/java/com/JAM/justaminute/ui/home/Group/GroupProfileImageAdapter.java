package com.JAM.justaminute.ui.home.Group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterfaceImg;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupProfileImageAdapter extends RecyclerView.Adapter<GroupProfileImageAdapter.ViewHolder> {


    Context context;
    RecyclerViewClickInterfaceImg recyclerViewClickInterface;
    ArrayList<String> imgArrayList = new ArrayList<>();

    public GroupProfileImageAdapter(Context context, RecyclerViewClickInterfaceImg recyclerViewClickInterface, ArrayList<String> imgArrayList) {
        this.context = context;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
        this.imgArrayList = imgArrayList;
    }

    @NonNull
    @Override
    public GroupProfileImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.group_profile_img_view,parent,false);
        final GroupProfileImageAdapter.ViewHolder holder=new GroupProfileImageAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupProfileImageAdapter.ViewHolder holder, int position) {
        String a= imgArrayList.get(position);
        try {
            Picasso.get().load(a).into(holder.img);
        }
        catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return imgArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.group_profile_view_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClickImg(getAdapterPosition());
                }
            });

        }
    }
}
