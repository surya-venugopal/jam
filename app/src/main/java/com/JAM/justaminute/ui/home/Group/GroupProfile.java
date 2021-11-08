package com.JAM.justaminute.ui.home.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterfaceDis;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterfaceDoc;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterfaceImg;
import com.JAM.justaminute.ui.home.PhotoViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupProfile extends AppCompatActivity implements RecyclerViewClickInterfaceDis, RecyclerViewClickInterfaceDoc, RecyclerViewClickInterfaceImg {
    EditText name;
    FloatingActionButton done;
    RecyclerView display_persons;
    GroupAdapter display_adapter;

    RecyclerView display_images;
    GroupProfileImageAdapter groupProfileImageAdapter;

    ArrayList<String> mem,ids,Imgs = new ArrayList<>();
    ArrayList<DisplayMembersModel> bt,b,person_dis;


    String group_id="`",person_id;

    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String my_id;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_group_groupprofile);

        progressDialog = new ProgressDialog(this);
        name = (EditText) findViewById(R.id.group_name);
        done = (FloatingActionButton) findViewById(R.id.group_confirm);
        display_persons = (RecyclerView) findViewById(R.id.group_person_display_confirm);
        display_images = (RecyclerView) findViewById(R.id.group_profile_img);

        try {
            Bundle bundle = getIntent().getExtras();
            try {
                group_id = bundle.getString("group_id");
                if(group_id == null){
                    group_id = "`";
                }
            }
            catch (Exception e){
                group_id = "`";
            }

            try {
                person_dis = (ArrayList<DisplayMembersModel>) getIntent().getSerializableExtra("arrUsers");
            }
            catch (Exception f){
                person_dis = new ArrayList<>();
            }
            try {
                ids = new ArrayList<>();
                ids = bundle.getStringArrayList("members");
            }
            catch (Exception e){
                ids = null;
            }
            try {
                person_id = bundle.getString("person");
            }
            catch (Exception c){
                person_id = "-1";
            }
        }
        catch (Exception e){
            finish();
        }
        try {
            if(group_id.compareTo("`") != 0)
                db.collection("Group").document(group_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot a= task.getResult();
                            try {
                                if(!a.getString("admin").equals(person_id) && !person_id.equals("")){
                                    done.setVisibility(View.INVISIBLE);
                                    name.setFocusable(false);
                                }
                                else{
                                    done.setVisibility(View.VISIBLE);
                                }
                            }
                            catch (Exception e){
                                done.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                });
            else{
                done.setVisibility(View.VISIBLE);
            }

        }
        catch (Exception e ){

        }

        display_adapter = new GroupAdapter(person_dis, this, this);
        display_persons.setLayoutManager(new LinearLayoutManager(this));
        display_persons.setAdapter(display_adapter);

        groupProfileImageAdapter = new GroupProfileImageAdapter(getApplicationContext(),  this,Imgs);
        display_images.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL,false));

        loadImgs();

        b = new ArrayList<>();
        if(person_dis.isEmpty()){
            db.collection("Group").document(group_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot a = task.getResult();
                        name.setText(a.getString("name"));

                        mem = new ArrayList<>();
                        mem = (ArrayList<String>) a.get("members");
                        bt= new ArrayList<>();
                        for (int i=0;i<mem.size();i++){
                            final int finalI = i;
                            db.collection("User").document(mem.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    DocumentSnapshot a = task.getResult();
                                    DisplayMembersModel usr = new DisplayMembersModel(a.getString("username"),a.getString("dp_uri"), a.getId(),a.getString("roll"));
                                    bt.add(usr);
                                    if(bt.size() == mem.size())
                                    valGroup();
                                }
                            });

                        }


                    }
                }

            });
        }
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait..");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                if (group_id.compareTo("`")==0){
                    if (name.getText().toString().isEmpty()) {
                        name.setError("");
                    } else {
                        Map<String, Object> group = new HashMap<String, Object>();
                        group.put("name", name.getText().toString());
                        group.put("admin", my_id);
                        group.put("members", ids);
                        group.put("last_message","");
                        group.put("last_time",(int) (System.currentTimeMillis()/ 1000L));
                        db.collection("Group").add(group).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    group_id=task.getResult().getId();
                                    db.collection("Group").document(group_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot a = task.getResult();
                                            ArrayList<String> ids = new ArrayList<>();
                                            ids = (ArrayList<String>) a.get("members");
                                            for (int i = 0; i < ids.size(); i++) {
                                                Map<String, Object> grp = new HashMap<String, Object>();
                                                grp.put("id", group_id);
                                                db.collection("User").document(ids.get(i)).collection("group").document(group_id).set(grp);


                                            }
                                            db.collection("Group").document(group_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        DocumentSnapshot a = task.getResult();
                                                        Intent i = new Intent(getApplicationContext(), ChatGroup.class);
                                                        i.putExtra("person", a.getId());
                                                        i.putExtra("person_dp", a.getString("dp_uri"));
                                                        i.putExtra("person_name", a.getString("name"));
                                                        i.putExtra("chat_id", a.getId());
                                                        startActivity(i);
                                                        finish();

                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
            }
                else {
                    if (name.getText().toString().isEmpty()) {
                        name.setError("");
                    } else{
                        Map<String, Object> group = new HashMap<String, Object>();
                        group.put("name", name.getText().toString());
                        //group.put("members", ids);
                        db.collection("Group").document(group_id).set(group,SetOptions.merge());
                        progressDialog.dismiss();
                        finish();
                }
                }
            }
        });


    }

    void loadImgs(){

        db.collection("Message").document(group_id).collection("messages")
                .whereEqualTo("type",5)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            List<DocumentSnapshot> a =task.getResult().getDocuments();
                            for(DocumentSnapshot b :a){

                                Imgs.add(b.getString("res"));
                                display_images.setAdapter(groupProfileImageAdapter);
                                groupProfileImageAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    void valGroup(){
        person_dis.clear();
        for(int i=0;i<mem.size();i++) {
            for (int j = 0; j < bt.size(); j++) {
                if (mem.get(i).equals(bt.get(j).getPerson_id())) {
                    person_dis.add(bt.get(j));
                }
            }

        }
            display_adapter.notifyDataSetChanged();
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
    public void onItemClick1(int position) {
    }

    @Override
    public void onLongItemClick1(int position) {

    }

    @Override
    public void onItemClickDoc(int position) {

    }

    @Override
    public void onLongItemClickDoc(int position) {

    }

    @Override
    public void onItemClickImg(int position) {
        Intent i = new Intent(getApplicationContext(), PhotoViewActivity.class);
        i.putExtra("photo",Imgs.get(position));
        startActivity(i);
    }

    @Override
    public void onLongItemClickImg(int position) {

    }
}