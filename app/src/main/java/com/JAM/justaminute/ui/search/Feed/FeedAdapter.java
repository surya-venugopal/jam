package com.JAM.justaminute.ui.search.Feed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>{

    Context context;
    List<FeedModel> feedModelList;
    FeedModel feedModel;
    RecyclerViewClickInterface recyclerViewClickInterface;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FeedAdapter(Context context, List<FeedModel> feedModelList, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.feedModelList = feedModelList;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.search_feedview,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        feedModel = feedModelList.get(position);
        holder.personname.setText(feedModel.getPersonName());
        holder.feeddes.setText(feedModel.getFeedDes());
        holder.feedtopic.setText(feedModel.getFeedTopic());
        holder.personroll.setText(feedModel.getPerson_roll());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(feedModel.getDate()* 1000L);
        cal.setTimeZone(TimeZone.getDefault());
        String date = String.valueOf(cal.get(Calendar.DATE)) +"-"+String.valueOf(cal.get(Calendar.MONTH))+"-"+String.valueOf(cal.get(Calendar.YEAR));
        holder.date.setText(date);
        Display display = ((WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();
        db.collection("User").document(feedModel.getPerson_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot a= task.getResult();
                    try {
                        Picasso.get().load(a.getString("dp_uri")).into(holder.persondp);
                    }
                    catch (Exception e){
                        holder.persondp.setImageResource(R.drawable.person_white);
                    }
                }
            }
        });
        try {
            if (!feedModel.getFeedImage().isEmpty()) {
                holder.feedimg.getLayoutParams().height = height/3;
                Picasso.get().load(feedModel.getFeedImage()).into(holder.feedimg);
            }
        }
        catch (Exception e){
            holder.feedimg.getLayoutParams().height = 0;

        }

    }

    @Override
    public int getItemCount() {
        return feedModelList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView personname,personroll,feedtopic,feeddes,date;
        CircleImageView persondp;
        PhotoView feedimg;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            personname = itemView.findViewById(R.id.feed_post_name);
            personroll = itemView.findViewById(R.id.feed_post_rollno);
            feedtopic = itemView.findViewById(R.id.feed_post_topic);
            feeddes = itemView.findViewById(R.id.feed_post_description);
            date = itemView.findViewById(R.id.feed_post_date);
            persondp = itemView.findViewById(R.id.feed_post_userdp);
            feedimg = itemView.findViewById(R.id.feed_post_img);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });

        }
    }

    String validate_mailid(String mail_id){

        char mail[] = mail_id.toCharArray();
        for (int i=0;i<mail.length;i++){
            if(mail[i] == '.'){
                mail[i] = ',';
            }
        }
        mail_id = String.valueOf(mail);
        return mail_id;
    }
}
