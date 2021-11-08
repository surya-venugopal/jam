package com.JAM.justaminute.ui.home.MyProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.JAM.justaminute.ui.MainActivity;
import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.LoginActivity;
import com.JAM.justaminute.ui.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_ini extends AppCompatActivity {
    EditText  age, phone, college,name,roll;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;


    FloatingActionButton next;
    ToggleButton profession;
    CircleImageView imageView;


    String my_id,dp_uri="";


    //firebase
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    //class
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_myprofile_profileini);

        age = (EditText) findViewById(R.id.profile_age_ini);
        phone = (EditText) findViewById(R.id.profile_phone_ini);
        college = (EditText) findViewById(R.id.profile_college_ini);
        name = (EditText) findViewById(R.id.profile_name_ini);
        next = (FloatingActionButton) findViewById(R.id.next_profile_ini);
        imageView = (CircleImageView) findViewById(R.id.dp_ini);
        roll = (EditText) findViewById(R.id.profile_roll_ini);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        updateUIfirebase(mFirebaseUser);

    }

    private boolean validate(){

        if(phone.getText().toString().isEmpty()){
            phone.setError("");
            return false;
        }
        if(age.getText().toString().isEmpty()){
            age.setError("");
            return false;
        }

        if(college.getText().toString().isEmpty()){
            college.setError("");
            return false;
        }
        if(name.getText().toString().isEmpty()){
            name.setError("");
            return false;
        }
        if(roll.getText().toString().isEmpty()){
            roll.setError("");
            return false;
        }


        return true;
    }


    private void updateUIfirebase(FirebaseUser account) {
        if (account == null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
            my_id = validate_mailid(my_id);

            try {
                storageRef.child("User_DP").child(my_id+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dp_uri = String.valueOf(uri);
                        Picasso.get().load(uri).into(imageView);

                    }
                });
            }
            catch (Exception e){
                imageView.setImageResource(R.drawable.person_white);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i= new Intent(getApplicationContext(), DPViewActivity.class);
                    i.putExtra("dp_uri",dp_uri);
                    startActivity(i);
                }
            });


            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validate()){
                        String a = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
                        String mailid = validate_mailid(a);
                        user = new User(name.getText().toString().toLowerCase(),mFirebaseUser.getEmail(),phone.getText().toString(),college.getText().toString()
                                ,age.getText().toString() ,dp_uri,roll.getText().toString());
                        //Firestore
                        db.collection("User").document(mailid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profile_ini.this, "Connection Lost!", Toast.LENGTH_SHORT).show();
                            }
                        });



                    }
                }
            });
        }
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


    @Override
    protected void onResume() {
        super.onResume();
        try {
            storageRef.child("User_DP").child(my_id+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    dp_uri = String.valueOf(uri);
                    Picasso.get().load(uri).into(imageView);

                }
            });
        }
        catch (Exception e){
            imageView.setImageResource(R.drawable.person_white);
        }

    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        age.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

}