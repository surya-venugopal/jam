package com.JAM.justaminute.ui.home.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.DisplayPersonAdapter;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterfaceDis;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddGroup extends AppCompatActivity implements RecyclerViewClickInterface, RecyclerViewClickInterfaceDis {
    RecyclerView group_persons,display_persons;
    EditText person_name;
    FloatingActionButton done;

    ArrayList<DisplayMembersModel> persons,person_dis;
    ArrayList<String> ids;
    DisplayPersonAdapter adapter;
    GroupAdapter display_adapter;

    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String my_id,person_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_addgroup);

        group_persons = (RecyclerView) findViewById(R.id.group_persons);
        display_persons = (RecyclerView) findViewById(R.id.group_person_display);
        person_name = (EditText) findViewById(R.id.person_name_for_group);
        done = (FloatingActionButton)findViewById(R.id.done_group);

        persons = new ArrayList<>();
        person_dis = new ArrayList<>();
        ids = new ArrayList<>();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(person_dis.size()>=2){
                        for (int j = 0;j<person_dis.size();j++){
                            if(!ids.contains(person_dis.get(j).getPerson_id()))
                                ids.add(person_dis.get(j).getPerson_id());
                        }

                    if (!ids.contains(my_id)) {
                        ids.add(my_id);
                    }
                    Intent i = new Intent(getApplicationContext(), GroupProfile.class);
                    i.putExtra("arrUsers",person_dis);
                    i.putStringArrayListExtra("members", ids);
                    startActivity(i);
                }
                else {
                    Toast.makeText(AddGroup.this, "Not enough Members!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter = new DisplayPersonAdapter(persons, AddGroup.this,getApplicationContext());
        display_adapter = new GroupAdapter(person_dis,this,this);

        group_persons.setLayoutManager(new LinearLayoutManager(this));
        display_persons.setLayoutManager(new LinearLayoutManager(this));

        group_persons.setAdapter(adapter);
        display_persons.setAdapter(display_adapter);

        Display display = ((WindowManager)
                getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();

        align(height,width);



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);
        person_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                persons.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(person_name.getText().toString().length() >0)
                    main();
                else
                    persons.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    void align(int height,int width){
        group_persons.setMinimumHeight(height/4);
    }

    void main(){

        db.collection("User").orderBy("username").startAt(person_name.getText().toString().toLowerCase())
                .endAt(person_name.getText().toString().toLowerCase() + "\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    int j=0;

    @Override
    public void onItemClick(int position) {
            int flag=1;
                for (int k = 0; k < person_dis.size(); k++) {
                    if (person_dis.get(k).getPerson_id().compareTo(persons.get(position).getPerson_id()) == 0) {
                        flag = 0;
                        break;
                    }
                }


            if(flag == 1) {
                person_dis.add(persons.get(position));
                display_adapter.notifyDataSetChanged();
            }

        person_name.setText("");
    }

    @Override
    public void onLongItemClick(int position) {

    }

    @Override
    public void onItemClick1(int position) {

    }

    @Override
    public void onLongItemClick1(int position) {

    }
}