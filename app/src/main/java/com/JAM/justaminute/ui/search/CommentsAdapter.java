package com.JAM.justaminute.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    List<CommentsModel> commentsModelList = new ArrayList<>();
    Context context;
    RecyclerViewClickInterface recyclerViewClickInterface;

    public CommentsAdapter(List<CommentsModel> commentsModelList, Context context, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.commentsModelList = commentsModelList;
        this.context = context;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentsModel commentsModel = commentsModelList.get(position);

        holder.name.setText(commentsModel.getName());
        holder.answer.setText(commentsModel.getComment());

        try {
            Picasso.get().load(commentsModel.getDps()).into(holder.img);
        }
        catch (Exception e){
            holder.img.setImageResource(R.drawable.person_white);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(commentsModel.getTime()* 1000L);
        cal.setTimeZone(TimeZone.getDefault());
        String date = String.valueOf(cal.get(Calendar.DATE)) +"-"+String.valueOf(cal.get(Calendar.MONTH))+"-"+String.valueOf(cal.get(Calendar.YEAR));
        holder.time.setText(date);
    }

    @Override
    public int getItemCount() {
        return commentsModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,answer,time;
        CircleImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.comments_name);
            answer = itemView.findViewById(R.id.comments_answer);
            time = itemView.findViewById(R.id.comments_time);
            img = itemView.findViewById(R.id.comments_dp);

            itemView.setOnClickListener(view -> {
                recyclerViewClickInterface.onItemClick(getAdapterPosition());
            });
        }
    }
}
