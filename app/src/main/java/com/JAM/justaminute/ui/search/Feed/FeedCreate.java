package com.JAM.justaminute.ui.search.Feed;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.JAM.justaminute.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class FeedCreate extends AppCompatActivity {
    TextView name,rollno,timestamp;
    EditText topic,description;
    ImageView img;
    CircleImageView dp;
    FloatingActionButton done;

    FirebaseFirestore db =FirebaseFirestore.getInstance();
    String my_id,college,roll;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseStorage storage;
    StorageReference storageRef;

    ProgressDialog progressDialog;
    Uri uri,img_uri=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_create_feed);
        progressDialog = new ProgressDialog(this);

        name = (TextView) findViewById(R.id.feed_post_name_create);
        rollno = (TextView) findViewById(R.id.feed_post_rollno_create);
        timestamp= (TextView)findViewById(R.id.feed_post_date_create);

        topic = (EditText) findViewById(R.id.feed_post_topic_create);
        description = (EditText) findViewById(R.id.feed_post_description_create);
        img = (ImageView) findViewById(R.id.feed_post_img_create);
        dp = (CircleImageView) findViewById(R.id.feed_post_userdp_create);
        done = (FloatingActionButton) findViewById(R.id.post_created);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        main();
    }

    void main(){
        db.collection("User").document(my_id).get().addOnCompleteListener(task -> {
        if(task.isSuccessful()){
            DocumentSnapshot a= task.getResult();
            name.setText(a.getString("username"));
            college = a.getString("college");
            roll =a.getString("roll");
            try {
                Picasso.get().load(a.getString("dp_uri")).into(dp);
            }
            catch (Exception e){
                dp.setImageResource(R.drawable.person_white);
            }
        }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(FeedCreate.this);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    progressDialog.setMessage("Just A Minute");
                    progressDialog.show();
                    FeedModel feedModel = new FeedModel(name.getText().toString(),my_id,roll,
                            "",topic.getText().toString(),description.getText().toString(), (int) (System.currentTimeMillis()/ 1000L),"");
                    db.collection("Feed").document(college).collection("posts").add(feedModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                final String id = task.getResult().getId();
                                storageRef.child("Feed").child(college).child("postImg").child(task.getResult().getId()).child(task.getResult().getId()+".jpg")
                                        .putFile(img_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot task) {
                                        storageRef.child("Feed").child(college).child("postImg").child(id).child(id+".jpg")
                                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(@NonNull Uri uri) {
                                                Map<String,Object> res = new HashMap<>();
                                                res.put("feedImage",String.valueOf(uri));
                                                db.collection("Feed").document(college).collection("posts").
                                                        document(id).set(res, SetOptions.merge());
                                                progressDialog.dismiss();
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });
    }

    boolean validate(){
        if(topic.getText().toString().isEmpty()){
            topic.setError("");
            return false;
        }
        if(description.getText().toString().isEmpty()){
            description.setError("");
            return false;
        }
        else if(description.getText().toString().length() > 300){
            description.setError("Content Exceeded");
            return false;
        }
        if(img_uri == null){
            Toast.makeText(this, "Please choose an Image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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

                img_uri = result.getUri();
                Picasso.get().load(img_uri).into(img);
            }

        }

    }

    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
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
