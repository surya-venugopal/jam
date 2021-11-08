package com.JAM.justaminute.ui.search.Forum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.JAM.justaminute.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForumCreate extends AppCompatActivity {

    EditText topic,description,tag;
    FloatingActionButton done;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mfirebaseAuth;
    FirebaseUser mFirebaseUser;

    String my_id,college;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_create_activity);
        topic = (EditText) findViewById(R.id.forum_create_topic);
        description = (EditText) findViewById(R.id.forum_create_description);
        tag = (EditText) findViewById(R.id.forum_create_tag);

        progressDialog = new ProgressDialog(this);
        done = (FloatingActionButton) findViewById(R.id.forum_create_done);

        done.setOnClickListener(view -> main());

        mfirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mfirebaseAuth.getCurrentUser();

        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(description.getText().toString().length() >300){
                    description.setError("Content Exceeded");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        db.collection("User").document(my_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if(task.isSuccessful()){
                DocumentSnapshot a = task.getResult();
                college = a.getString("college");
            }
            }
        });


    }

    void main(){
        if(validate()){
            progressDialog.setMessage("Just A Minute");
            progressDialog.show();
            ForumModel forumModel = new ForumModel(topic.getText().toString(),description.getText().toString(),
                    "",(int) (System.currentTimeMillis()/ 1000L),"",tag.getText().toString(),"");
            db.collection("Forum").document(college).collection("posts").add(forumModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        finish();
                    }
                }
            });
        }

    }

    boolean validate(){
        if(topic.getText().toString().isEmpty()){
            topic.setText("Empty");
            return false;
        }
        if(description.getText().toString().isEmpty() && description.getText().toString().length() >300){
            description.setError("Mismatch");
            return false;
        }
        return true;
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