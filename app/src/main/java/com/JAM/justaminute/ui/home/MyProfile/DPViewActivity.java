package com.JAM.justaminute.ui.home.MyProfile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class DPViewActivity extends AppCompatActivity {
    ImageButton btBrowse,btReset;
    ImageView imageView;
    Uri uri;
    AlertDialog alert;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage;
    StorageReference storageRef;


    String my_id,dp_uri;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        btBrowse=findViewById(R.id.id_browse);
        btReset=findViewById(R.id.id_reset);
        imageView=findViewById(R.id.imageView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting Profile Image");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        Bundle bundle=getIntent().getExtras();
        try {
            dp_uri = bundle.getString("dp_uri");
            Picasso.get().load(dp_uri).into(imageView);
            }
        catch (Exception e){
            imageView.setImageResource(R.drawable.person_white);
        }
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        updateUIfirebase(mFirebaseUser);

        btBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(DPViewActivity.this);

            }
        });

        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert =new AlertDialog.Builder(DPViewActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove Profile Photo")
                        .setMessage("Are u Sure?")
                        .setPositiveButton("remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.show();
                                storageRef.child("User_DP").child(my_id+".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        imageView.setImageResource(R.drawable.person_white);
                                        Map<String,Object> dp = new HashMap<>();
                                        dp.put("dp_uri","");
                                        db.collection("User").document(my_id).set(dp, SetOptions.merge());
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("cancel",null)
                        .show();


            }
        });


    }

    private void updateUIfirebase(FirebaseUser account){
        if (account == null){
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }
        else {

            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
            my_id = validate_mailid(my_id);

        }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode== Activity.RESULT_OK)
        {
            Uri imageUri=CropImage.getPickImageResultUri(this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(this,imageUri))
            {
                uri=imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
            else
            {
                startCrop(imageUri);
            }
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            final CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {


                progressDialog.show();
                storageRef.child("User_DP").child(my_id+".jpg").putFile(result.getUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageView.setImageURI(result.getUri());
                        progressDialog.dismiss();
                        storageRef.child("User_DP").child(my_id+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String,Object> dp = new HashMap<>();
                                dp.put("dp_uri",String.valueOf(uri));
                                db.collection("User").document(my_id).set(dp, SetOptions.merge());
                                finish();
                            }
                        });
                    }
                });



            }
        }
    }

    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }


}



