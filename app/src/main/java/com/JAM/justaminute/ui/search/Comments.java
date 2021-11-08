package com.JAM.justaminute.ui.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Comments extends AppCompatActivity implements RecyclerViewClickInterface {

    RecyclerView commentsview;
    CommentsAdapter adapter;
    List<CommentsModel> commentsModelList = new ArrayList<>();

    ConstraintLayout layout;
    TextView topic,description;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String my_id,college;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    String type = "",postid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);

        topic = (TextView) findViewById(R.id.comments_topic);
        description = (TextView) findViewById(R.id.comments_description);
        layout = (ConstraintLayout) findViewById(R.id.constraintLayoutComments);
        try {
            Bundle bundle = getIntent().getExtras();
            type = bundle.getString("type");
            postid = bundle.getString("postid");
            topic.setText(bundle.getString("topic"));
            description.setText(bundle.getString("description"));

            if(type.equals("") || postid.equals("")){
                finish();
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Retry",Toast.LENGTH_SHORT).show();
            finish();
        }

        commentsview = (RecyclerView) findViewById(R.id.comments_view);
        commentsview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        commentsview.addItemDecoration(dividerItemDecoration);

        adapter = new CommentsAdapter(commentsModelList,getApplicationContext(),this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);

        db.collection("User").document(my_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot a = task.getResult();
                    college = a.getString("college");
                    main();
                }
            }
        });


    }

    void main(){
        db.collection(type).document(college).collection("posts").document(postid).collection("comments")
                .orderBy("time", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if(task.isSuccessful()){
                List<DocumentSnapshot> a = task.getResult().getDocuments();
                for (DocumentSnapshot doc:a){
                    CommentsModel commentsModel = new CommentsModel();
                    commentsModel = doc.toObject(CommentsModel.class);
                    commentsModelList.add(commentsModel);
                    commentsview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
            }
        });

    }
    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLongItemClick(int position) {

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