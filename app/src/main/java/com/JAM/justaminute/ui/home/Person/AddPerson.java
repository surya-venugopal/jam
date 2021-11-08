package com.JAM.justaminute.ui.home.Person;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.JAM.justaminute.ui.MainActivity;
import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.User;
import com.JAM.justaminute.ui.LoginActivity;
import com.JAM.justaminute.ui.home.DisplayPersonAdapter;
import com.JAM.justaminute.ui.home.Group.DisplayMembersModel;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPerson extends AppCompatActivity implements RecyclerViewClickInterface {
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String my_id,person_id,chatid;
    boolean flag;
    EditText person;

    ArrayList<DisplayMembersModel> persons;
    DisplayPersonAdapter adapter;
    RecyclerView group_persons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_addperson);
        setTitle("Add Friend");
        person = (EditText) findViewById(R.id.user_name);
        Button add = (Button)findViewById(R.id.add);
        group_persons = (RecyclerView) findViewById(R.id.add_person_dis);
        persons = new ArrayList<>();
        adapter = new DisplayPersonAdapter(persons,this,getApplicationContext());
        group_persons.setLayoutManager(new LinearLayoutManager(this));
        group_persons.setAdapter(adapter);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);

        updateUIfirebase(mFirebaseUser);
    }

    private void main(){
        chatid=createChat(person_id);
        if(chatid != null ){
            addFriend();

        }
        else{
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }


    private void updateUIfirebase(FirebaseUser account) {
        if (account == null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
            my_id = validate_mailid(my_id);

            person.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    persons.clear();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(person.getText().toString().length() >0)
                        search();
                    else
                        persons.clear();
                }
            });
            }
    }

    void search(){
        db.collection("User").orderBy("username").startAt(person.getText().toString().toLowerCase()).endAt(person.getText().toString().toLowerCase() + "\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    persons.clear();
                    List<DocumentSnapshot> a = task.getResult().getDocuments();
                    for (DocumentSnapshot documentSnapshot : a){
                        if(!persons.contains(documentSnapshot.getString("username")) && !documentSnapshot.getId().equals(my_id))
                        {
                            DisplayMembersModel users = new DisplayMembersModel(documentSnapshot.getString("username"),
                                    documentSnapshot.getString("dp_uri"),documentSnapshot.getId(),documentSnapshot.getString("roll"));
                            persons.add(users);

                            adapter.notifyDataSetChanged();
                        }
                    }

                }
            }
        });
    }

    private String createChat(String person_name){

        int a = my_id.compareTo(person_name);

        if(a>0){
            return person_name+"_"+my_id;
        }
        else if(a<0){
            return my_id+"_"+person_name;
        }
        return "";
    }

    private void AddMeAsFriend(){
        db.collection("User").document(person_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> friends = new HashMap<>();
                friends.put("id",my_id);
                friends.put("last_message","");
                friends.put("last_time",(int) (System.currentTimeMillis()/ 1000L));
                db.collection("User").document(person_id).collection("friends").document(my_id).set(friends).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Connection Lost",Toast.LENGTH_SHORT).show();
                    }
                });

                try {
                    String no = (documentSnapshot.getString("no_of_friends"));
                    Map<String,String> frnds = new HashMap<>();
                    frnds.put("no_of_friends",String.valueOf(Integer.valueOf(no)+1));
                    db.collection("User").document(person_id).set(frnds, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }catch (Exception e){
                    Map<String,String> frnds = new HashMap<>();
                    frnds.put("no_of_friends","1");
                    db.collection("User").document(person_id).set(frnds, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }
            }
        });
    }

        private void addFriend(){
            db.collection("User").document(person_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            db.collection("User").document(my_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        final DocumentSnapshot documentSnapshot1 = task.getResult();

                                        db.collection("User").document(my_id).collection("friends").whereEqualTo("id",person_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    QuerySnapshot documentSnapshot2 = task.getResult();
                                                    if(documentSnapshot2.isEmpty()){
                                                        Map<String,Object> friends = new HashMap<String, Object>();
                                                        friends.put("id",person_id);
                                                        friends.put("last_message","");
                                                        friends.put("last_time",(int) (System.currentTimeMillis()/ 1000L));

                                                        db.collection("User").document(my_id).collection("friends").document(person_id).set(friends).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                        try {

                                                                String no = (documentSnapshot1.getString("no_of_friends"));
                                                                Map<String, String> frnds = new HashMap<>();
                                                                frnds.put("no_of_friends", String.valueOf(Integer.valueOf(no) + 1));
                                                                db.collection("User").document(my_id).set(frnds, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                    }
                                                                });
                                                        }
                                                        catch (Exception e){
                                                            Map<String, Object> frnds = new HashMap<>();
                                                            frnds.put("no_of_friends", "1");
                                                            db.collection("User").document(my_id).set(frnds, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                }
                                                            });
                                                        }

                                                        AddMeAsFriend();
                                                    }
                                                    else
                                                    {
                                                        start_chat();
                                                    }
                                                }
                                            }
                                        });
                                        start_chat();
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), "Person doesn't exist!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }

        void start_chat(){
            db.collection("User").document(person_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        User user = new User();
                        user = documentSnapshot.toObject(User.class);
                        Intent i = new Intent(getApplicationContext(), Chat.class);
                        i.putExtra("person",person_id);
                        i.putExtra("person_dp",documentSnapshot.getString("dp_uri"));
                        i.putExtra("person_name",documentSnapshot.getString("username"));
                        startActivity(i);
                        finish();

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


    @Override
    public void onItemClick(int position) {
        try{
            person_id = persons.get(position).getPerson_id();;
            person_id = validate_mailid(person_id);
            main();
        }
        catch (Exception e){
            persons.clear();
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onLongItemClick(int position) {

    }
}



