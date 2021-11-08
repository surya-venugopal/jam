package com.JAM.justaminute.ui.home.MyProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.User;
import com.JAM.justaminute.ui.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    EditText age, phone, college,roll;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    TextView dp_name;
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
        setContentView(R.layout.home_myprofile_profile);

        age = (EditText) findViewById(R.id.profile_age);
        phone = (EditText) findViewById(R.id.profile_phone);
        college = (EditText) findViewById(R.id.profile_college);
        dp_name = (TextView) findViewById(R.id.dpName);
        next = (FloatingActionButton) findViewById(R.id.next_profile);
        imageView = (CircleImageView) findViewById(R.id.dp);
        roll = (EditText) findViewById(R.id.profile_roll);

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
        if(roll.getText().toString().isEmpty()){
            roll.setError("");
            return false;
        }

        return true;
    }


    void init(){

        db.collection("User").document(my_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                phone.setText(documentSnapshot.getString("phone"));
                college.setText(documentSnapshot.getString("college"));
                age.setText(documentSnapshot.getString("age"));
                dp_name.setText(documentSnapshot.getString("username"));
                roll.setText(documentSnapshot.getString("roll"));
                try {
                    dp_uri = documentSnapshot.getString("dp_uri");
                    Picasso.get().load(dp_uri).into(imageView);
                }
                catch (Exception e){
                    imageView.setImageResource(R.drawable.person_white);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
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
            my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
            my_id = validate_mailid(my_id);
            init();
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
                        //Firestore
                        Map<String,Object> User = new HashMap<>();
                        User.put("email",mFirebaseUser.getEmail());
                        User.put("college",college.getText().toString());
                        User.put("phone",phone.getText().toString());
                        User.put("age",age.getText().toString());
                        User.put("dp_uri",dp_uri);
                        User.put("roll",roll.getText().toString());


                        db.collection("User").document(mailid).set(User, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
                            }
                        });



                        finish();
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
        init();

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