package com.JAM.justaminute.ui.Test.AttemptQuiz;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DisplayTests extends AppCompatActivity implements RecyclerViewClickInterface {
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Map<String,Object>> personList = new ArrayList<>();
    private RecyclerView recyclerView;
    TestsViewAdapter adapter;
    String my_id,group_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_attempt_viewtests);

        try {
            Bundle bundle = getIntent().getExtras();
            group_id = bundle.getString("group_id");

        }
        catch (Exception e){
            finish();
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        my_id = mFirebaseUser.getEmail();
        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent a = new Intent(getApplicationContext(),ViewGroupsAttempt.class);
                startActivity(a);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        recyclerView = findViewById(R.id.tests_for_me);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new TestsViewAdapter(personList, this,this);
        main();
    }

    void main(){
        db.collection("Test").document(group_id).collection("tests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot a= task.getResult();
                    for(final DocumentSnapshot snapshot : a.getDocuments()){
                        Map<String,Object> person = new HashMap<String, Object>();
                        person.put("test_name",snapshot.getString("test_name"));
                        person.put("time_of_creation",getTimefromUTC(((Long) snapshot.get("time_of_creation")).intValue()));
                        person.put("id",snapshot.getId());
                        personList.add(person);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                }
            }
        });

    }

    String getTimefromUTC(int time){
        String ctime="";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time* 1000L);
        cal.setTimeZone(TimeZone.getDefault());
        if( cal.get(Calendar.HOUR) <10){

            if(cal.get(Calendar.HOUR) == 0){
                ctime+="12"+":";
            }
            else{
                ctime+="0"+cal.get(Calendar.HOUR)+":";
            }
        }
        else {
            ctime+=cal.get(Calendar.HOUR)+":";
        }
        if(cal.get(Calendar.MINUTE) <10){
            ctime+="0"+cal.get(Calendar.MINUTE);
        }
        else{
            ctime+=cal.get(Calendar.MINUTE);
        }

        return ctime;
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
       Intent i = new Intent(getApplicationContext(),AttemptQuiz.class);
       i.putExtra("group_id",group_id);
       i.putExtra("test_id",String.valueOf(personList.get(position).get("id")));
       startActivity(i);
    }

    @Override
    public void onLongItemClick(int position) {

    }
}