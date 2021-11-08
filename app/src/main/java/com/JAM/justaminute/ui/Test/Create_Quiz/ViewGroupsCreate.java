package com.JAM.justaminute.ui.Test.Create_Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.List_Users;
import com.JAM.justaminute.ui.home.Person.PersonPageAdapter;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewGroupsCreate extends AppCompatActivity implements RecyclerViewClickInterface {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private List<List_Users> personList=new ArrayList<>();
    private RecyclerView recyclerView;
    PersonPageAdapter adapter;
    String chat_id,my_id,person_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_createquiz_groupview);
        recyclerView = findViewById(R.id.test_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new PersonPageAdapter(getApplicationContext(),personList, this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        main();
    }


    void main(){
        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);
        db.collection("User").document(my_id).collection("group").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot a= task.getResult();
                    for(DocumentSnapshot snapshot : a.getDocuments()){

                        db.collection("Group").document(snapshot.getString("id")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot a =task.getResult();
                                    if(a.getString("admin").compareTo(my_id) == 0)
                                        prepareTheList(a.getId(), "", "");

                                }
                            }
                        });

                    }

                }
            }
        });
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

    private void prepareTheList(final String p_id, final String last_text, final String last_time) {

            db.collection("Group").document(p_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    List_Users person = new List_Users(documentSnapshot.getString("name"), documentSnapshot.getString("dp_uri"), p_id,
                            last_text, last_time,1);

                    personList.add(person);
                    recyclerView.setAdapter(adapter);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
                }
            });


    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(this, CreateQuizMain.class);
        i.putExtra("group_id",personList.get(position).getPerson_id());
        startActivity(i);

    }

    @Override
    public void onLongItemClick(int position) {

    }
}