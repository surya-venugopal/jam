package com.JAM.justaminute.ui.search.Feed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.JAM.justaminute.ui.search.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FeedFragment extends Fragment implements RecyclerViewClickInterface {

    RecyclerView feedmain;
    FeedAdapter adapter;
    ArrayList<FeedModel> feedModelList = new ArrayList<>();
    FloatingActionButton add;

    FirebaseFirestore db =FirebaseFirestore.getInstance();
    String my_id,college;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.search_feed_feedmain, container, false);
        feedmain = (RecyclerView) root.findViewById(R.id.feed_main);
        add = (FloatingActionButton) root.findViewById(R.id.add_post_button);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(root.getContext(), FeedCreate.class);
                startActivity(i);
            }
        });

        feedmain.setLayoutManager(new LinearLayoutManager(root.getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL);
        feedmain.addItemDecoration(dividerItemDecoration);
        adapter = new FeedAdapter(root.getContext(),feedModelList,this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);
        main();
        return root;

    }

    void main(){

        db.collection("User").document(my_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if(task.isSuccessful()){
                DocumentSnapshot a = task.getResult();
                college = a.getString("college");
                loadfeed();
            }
            }
        });


    }

    void loadfeed(){

        db.collection("Feed").document(college).collection("posts").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }
                for (final DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            QueryDocumentSnapshot a = dc.getDocument();
                            if(!a.getString("feedImage").equals("")){
                               FeedModel feedModel1 = new FeedModel(a.getString("personName"),
                                       a.getString("person_id"),a.getString("person_roll"),a.getString("feedImage"),
                                       a.getString("feedTopic"),a.getString("feedDes"),((Long) a.get("date")).intValue(),a.getId());

                                feedModelList.add(feedModel1);
                                sortit();
                            }
                            break;
                        case MODIFIED:
                            QueryDocumentSnapshot a1 = dc.getDocument();
                                FeedModel feedModel1 = new FeedModel(a1.getString("personName"),
                                        a1.getString("person_id"),a1.getString("person_roll"),a1.getString("feedImage"),
                                        a1.getString("feedTopic"),a1.getString("feedDes"),((Long) a1.get("date")).intValue(),a1.getId());
                                feedModelList.add(feedModel1);
                                sortit();

                            break;
                        case REMOVED:
                            break;
                    }
                }
            }
        });


    }

    void sortit(){
        FeedModel temp = feedModelList.get(feedModelList.size()-1);
        for(int i=feedModelList.size()-1;i>0;i--){
            feedModelList.set(i,feedModelList.get(i-1));
        }
        feedModelList.set(0,temp);
        feedmain.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(root.getContext(), Comments.class);
        i.putExtra("type","Feed");
        i.putExtra("postid",feedModelList.get(position).getId());
        i.putExtra("topic",feedModelList.get(position).getFeedTopic());
        i.putExtra("description", feedModelList.get(position).getFeedDes());
        startActivity(i);
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