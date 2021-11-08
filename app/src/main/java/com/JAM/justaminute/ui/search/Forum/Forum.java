package com.JAM.justaminute.ui.search.Forum;

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


public class Forum extends Fragment implements RecyclerViewClickInterface {

    RecyclerView forumview;
    ForumAdapter adapter;
    ArrayList<ForumModel> forumModelList = new ArrayList<>();
    FloatingActionButton add;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String my_id,college;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.forum_fragment, container, false);
        forumview = (RecyclerView) root.findViewById(R.id.forum_main);
        add = (FloatingActionButton) root.findViewById(R.id.forum_add_button);


        forumview.setLayoutManager(new LinearLayoutManager(root.getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(root.getContext(),DividerItemDecoration.VERTICAL);
        forumview.addItemDecoration(dividerItemDecoration);

        adapter = new ForumAdapter(root.getContext(),forumModelList,this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(root.getContext(),ForumCreate.class);
                startActivity(i);
            }
        });

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

                    load();
                }
            }
        });
    }

    void load(){
        db.collection("Forum").document(college).collection("posts").orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }
                        assert queryDocumentSnapshots != null;
                        for (final DocumentChange dc :queryDocumentSnapshots.getDocumentChanges()){
                            switch (dc.getType()){
                                case ADDED:
                                    QueryDocumentSnapshot a = dc.getDocument();
                                    ForumModel forumModel = new ForumModel(a.getString("topic"),a.getString("description"),
                                            a.getString("comment1"),((Long) a.get("time")).intValue(),a.getString("cimg1"),
                                            a.getString("tag"),a.getId());
                                    forumModelList.add(forumModel);
                                    sortit();
                                    break;
                                case MODIFIED:
                                    break;
                                 case REMOVED:
                                    break;

                            }
                        }
                    }
                });
    }

    void sortit(){
        ForumModel temp = forumModelList.get(forumModelList.size()-1);
        for(int i=forumModelList.size()-1;i>0;i--){
            forumModelList.set(i,forumModelList.get(i-1));
        }
        forumModelList.set(0,temp);
        forumview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(root.getContext(), Comments.class);
        i.putExtra("type","Forum");
        i.putExtra("postid",forumModelList.get(position).getId());
        i.putExtra("topic",forumModelList.get(position).getTopic());
        i.putExtra("description",forumModelList.get(position).getDescription());
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