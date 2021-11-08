package com.JAM.justaminute.ui.home.Person;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.JAM.justaminute.ui.MainActivity;
import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.User;
import com.JAM.justaminute.ui.LoginActivity;
import com.JAM.justaminute.ui.home.PhotoViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfile extends AppCompatActivity {
    EditText age, phone, college,roll;

    TextView dp_name;
    CircleImageView imageView;

    String dp_uri="",person_id;


    //firebase
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage;
    StorageReference storageRef;

    //class
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_chatbox_friendprofile);
        person_id = "";
        try {
            Bundle bundle = getIntent().getExtras();
            person_id = bundle.getString("person");
            person_id = validate_mailid(person_id);

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Retry",Toast.LENGTH_SHORT);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        age = (EditText) findViewById(R.id.age_friend);
        phone = (EditText) findViewById(R.id.phone_friend);
        college = (EditText) findViewById(R.id.college_friend);
        dp_name = (TextView) findViewById(R.id.dpName_friend);
        imageView = (CircleImageView) findViewById(R.id.dp_friend);
        roll = (EditText) findViewById(R.id.roll_friend);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        updateUIfirebase(mFirebaseUser);
    }


    void init(){
        db.collection("User").document(person_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                User users = new User();
                users = documentSnapshot.toObject(User.class);
                phone.setText( users.getPhone());
                college.setText(users.getCollege());
                age.setText(users.getAge());
                dp_name.setText(users.getUsername());
                roll.setText(users.getRoll());
            }
            }
        });

    }

    private void updateUIfirebase(FirebaseUser account) {
        if (account == null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            init();
            try {
                storageRef.child("User_DP").child(person_id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dp_uri = String.valueOf(uri);
                        Picasso.get().load(uri).into(imageView);

                    }
                });
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.person_white);
            }


        }
    }

    public void imageClick(View v)
    {
        Intent intent=new Intent(getApplicationContext(), PhotoViewActivity.class);
        intent.putExtra("photo",dp_uri);
        startActivity(intent);
    }


    String validate_mailid(String mailid){

        char mail[] = mailid.toCharArray();
        for (int i=0;i<mail.length;i++){
            if(mail[i] == '.'){
                mail[i] = ',';
            }
        }
        mailid = String.valueOf(mail);
        return mailid;
    }
}
