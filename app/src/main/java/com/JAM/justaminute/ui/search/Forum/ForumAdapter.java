package com.JAM.justaminute.ui.search.Forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.JAM.justaminute.ui.search.CommentsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    Context context;
    List<ForumModel> forumModelList;
    ForumModel forumModel;
    RecyclerViewClickInterface recyclerViewClickInterface;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String my_id,college,myc="",mydp="",my_name="";
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    public ForumAdapter(Context context, List<ForumModel> forumModels, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.forumModelList = forumModels;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_view,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.forum_mycomment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.my_comment.getText().toString().equals("")){

                    CommentsModel a = new CommentsModel(my_name,my_id,holder.my_comment.getText().toString(),
                            (int) (System.currentTimeMillis()/ 1000L),mydp);
                    myc = holder.my_comment.getText().toString();
                    holder.my_comment.setText("");
                    db.collection("Forum").document(college).collection("posts").
                            document(forumModelList.get(holder.getAdapterPosition()).getId()).
                            collection("comments").add(a).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            try {
                                if (!mydp.equals(""))
                                Picasso.get().load(mydp).into(holder.forum_view_comment1_img);
                                else
                                    holder.forum_view_comment1_img.setImageResource(R.drawable.person_white);
                            }
                            catch (Exception e){
                                holder.forum_view_comment1_img.setImageResource(R.drawable.person_white);
                            }
                            holder.comment1.setText(myc);
                            Map<String,Object> b= new HashMap<String, Object>();
                            b.put("comment1",myc);
                            b.put("cimg1",mydp);
                            db.collection("Forum").document(college).collection("posts").
                                    document(forumModelList.get(holder.getAdapterPosition()).getId()).set(b, SetOptions.merge());

                        }
                    });
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        forumModel = forumModelList.get(position);
        holder.topic.setText(forumModel.getTopic());
        holder.description.setText(forumModel.getDescription());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(forumModel.getTime()* 1000L);
        cal.setTimeZone(TimeZone.getDefault());
        String date = String.valueOf(cal.get(Calendar.DATE)) +"-"+String.valueOf(cal.get(Calendar.MONTH))+"-"+String.valueOf(cal.get(Calendar.YEAR));
        holder.time.setText(date);
        holder.comment1.setText(forumModel.getComment1());
        try {
            if(!holder.comment1.getText().toString().equals(""))
            Picasso.get().load(forumModel.getCimg1()).into(holder.forum_view_comment1_img);
        }
        catch (Exception e){
            if(!holder.comment1.getText().toString().equals(""))
            holder.forum_view_comment1_img.setImageResource(R.drawable.person_white);
        }

    }

    @Override
    public int getItemCount() {
        return forumModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView topic,description,time,comment1;
        CircleImageView forum_view_comment1_img;
        EditText my_comment;
        CircleImageView my_dp,forum_mycomment_send;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.forum_view_topic);
            description = itemView.findViewById(R.id.forum_view_description);
            time = itemView.findViewById(R.id.forum_view_time);
            comment1 = itemView.findViewById(R.id.forum_view_comment1_text);
            my_comment = (EditText) itemView.findViewById(R.id.forum_enteryourcomment);
            my_dp = (CircleImageView) itemView.findViewById(R.id.forum_comment_mydp);
            forum_view_comment1_img = itemView.findViewById(R.id.forum_view_comment1_img);
            forum_mycomment_send = itemView.findViewById(R.id.forum_mycomment_send);

            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
            my_id = validate_mailid(my_id);

            db.collection("User").document(my_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot a= task.getResult();
                        college = a.getString("college");
                        my_name = a.getString("username");
                        try {
                            mydp = a.getString("dp_uri");
                            Picasso.get().load(a.getString("dp_uri")).into(my_dp);
                        }
                        catch (Exception e){
                            my_dp.setImageResource(R.drawable.person_white);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
