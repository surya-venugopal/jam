package com.JAM.justaminute.ui.home.Person;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.JAM.justaminute.ui.MainActivity;
import com.JAM.justaminute.ui.Permissions;
import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.LoginActivity;
import com.JAM.justaminute.ui.home.MessageAdapter;
import com.JAM.justaminute.ui.home.MessageModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Chat extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    EditText user_input;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    FloatingActionButton scrollFab,mic,mic2,send,camera,attach;
    List<MessageModel> responseMessageList;
    TextView userName;
    Toolbar toolbar;
    CircleImageView dp_chat;



    String my_id,chatid,person_id ,person_name,person_dp, audio_filename;
    MediaRecorder recorder;
    Uri uri,img_uri,audio_uri,video_uri,document_uri;
    String doc_name;
    ProgressDialog progressDialog;

    FrameLayout mRevealView;
    ImageButton gallery,audio,document;
    boolean hidden = true;
    private int GALLERY=1,DOCUMENT=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_chatbox_chat);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//            @Override
//            public void handleOnBackPressed() {
//                Intent a = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(a);
//                finish();
//            }
//        };
//        getOnBackPressedDispatcher().addCallback(this, callback);
        toolbar=findViewById(R.id.toolbar2);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting Profile Image");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        recyclerView = findViewById(R.id.conversation);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, (ArrayList<MessageModel>) responseMessageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(messageAdapter);
        scrollFab=findViewById(R.id.scrollFab);
        userName=findViewById(R.id.userName);
        mic = findViewById(R.id.mic);
        mic2 = findViewById(R.id.mic2);
        send = (FloatingActionButton) findViewById(R.id.sendButton);
        user_input = (EditText) findViewById(R.id.message);
        mRevealView = findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.GONE);


        dp_chat = (CircleImageView) findViewById(R.id.dp_chat);
        camera=findViewById(R.id.camera);
        attach = findViewById(R.id.attach);
        gallery=findViewById(R.id.gallery);
        document=findViewById(R.id.document);


        chatid="";
        person_id = "";
        person_name = "";
        person_dp = "";
        try {
            Bundle bundle = getIntent().getExtras();
            person_id = bundle.getString("person");
            person_id = validate_mailid(person_id);
            person_dp = bundle.getString("person_dp");
            person_name = bundle.getString("person_name");

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Retry",Toast.LENGTH_SHORT);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }

        updateUIfirebase(mFirebaseUser);

    }


    private String createChatid(String person_name){

        int a = my_id.compareTo(person_name);

        if(a>0){
            return person_name+"_"+my_id;
        }
        else if(a<0){
            return my_id+"_"+person_name;
        }
        return "";
    }

    public void send()
    {
        if (user_input.getText().toString().equals("") || user_input.getText().toString().trim().isEmpty()) {

            send.setVisibility(View.INVISIBLE);
            camera.setVisibility(View.VISIBLE);

        } else {

            camera.setVisibility(View.INVISIBLE);
            send.setVisibility(View.VISIBLE);

        }
    }



    public boolean isVisible()
    {
        LinearLayoutManager linearLayoutManager=(LinearLayoutManager) recyclerView.getLayoutManager();
        int positionOfLastVisibleItem=linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int itemCount=recyclerView.getAdapter().getItemCount();
        return (positionOfLastVisibleItem>=itemCount);
    }

    void startRecording() {
        Permissions permissions = new Permissions(getApplicationContext(), this);
        if (permissions.checkmic()) {
            mic2.setVisibility(View.VISIBLE);
            mic.setVisibility(View.INVISIBLE);
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(audio_filename);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                recorder.prepare();
                recorder.start();
            } catch (Exception e) {
                Toast.makeText(this, "Audio ERR", Toast.LENGTH_SHORT).show();
            }
        }
    }
    void stopRecording(){
        mic2.setVisibility(View.INVISIBLE);
        mic.setVisibility(View.VISIBLE);
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
            progressDialog.show();
            audio_uri = Uri.fromFile(new File(audio_filename));;
            MessageModel message = new MessageModel("",3,"", mFirebaseUser.getEmail(),mFirebaseUser.getDisplayName(),my_id);
            update_lastmsg("");
            db.collection("Message").document(chatid).collection("messages").add(message);
        }
        catch (Exception e ){

        }
    }

    private void update_lastmsg(final String msg){
        db.collection("User").document(my_id).collection("friends").whereEqualTo("id",person_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot snapshots : task.getResult()){
                    Map<String,Object> lastmsg = new HashMap<String, Object>();
                    lastmsg.put("last_time", (int) (System.currentTimeMillis()/ 1000L));
                    lastmsg.put("last_message",msg);
                    db.collection("User").document(my_id).collection("friends").document(snapshots.getId()).set(lastmsg,SetOptions.merge());
                }
            }
        });
        db.collection("User").document(person_id).collection("friends").whereEqualTo("id",my_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot snapshots : task.getResult()){
                    Map<String,Object> lastmsg = new HashMap<String, Object>();
                    lastmsg.put("last_time", (int) (System.currentTimeMillis()/ 1000L));
                    lastmsg.put("last_message",msg);
                    db.collection("User").document(person_id).collection("friends").document(snapshots.getId()).set(lastmsg,SetOptions.merge());
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


            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CropImage.startPickImageActivity(Chat.this);
                }
            });

            attach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showDiag();
                }
            });
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRevealView.setVisibility(View.INVISIBLE);
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, GALLERY);
                }
            });
            document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRevealView.setVisibility(View.INVISIBLE);
                    Intent documentIntent=new Intent(Intent.ACTION_GET_CONTENT);
                    documentIntent.setType("*/*");
                    String[] extraMimeTypes={"application/pdf","application/vnd.ms-word","application/vnd.ms-powerpoint","application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.presentationml.presentation","application/x-wav","application/vnd.android.package-archive","excel/*"};
                    documentIntent.putExtra(Intent.EXTRA_MIME_TYPES,extraMimeTypes);
                    documentIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                    startActivityForResult(documentIntent,DOCUMENT);
                }
            });


            my_id = mFirebaseUser.getEmail().substring(0, mFirebaseUser.getEmail().indexOf('@'));
            my_id = validate_mailid(my_id);
            chatid = createChatid(person_id);
            userName.setText(person_name);
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            audio_filename = getExternalCacheDir().getAbsolutePath() + "/audioo.3gp";
            try {
                Picasso.get().load(person_dp).into(dp_chat);
            }
            catch (Exception e){
                dp_chat.setImageResource(R.drawable.person_white);
            }

                mic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startRecording();
                    }
                });
                mic2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopRecording();
                    }
                });

                userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), FriendProfile.class);
                        intent.putExtra("person", person_id);
                        startActivity(intent);

                    }
                });
            progressDialog.setMessage("Loading");
            progressDialog.show();
                db.collection("Message").document(chatid).collection("messages").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }
                        for (final DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    try {
                                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                                    }
                                    catch (Exception ex) {

                                    }
                                    if(audio_uri!=null){
                                        storageRef.child("Message").child(chatid).child("Audio").child(dc.getDocument().getId()+".3gp").putFile(audio_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                storageRef.child("Message").child(chatid).child("Audio").child(dc.getDocument().getId()+".3gp").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Map<String,Object> res = new HashMap<>();
                                                        res.put("res",String.valueOf(uri));
                                                        db.collection("Message").document(chatid).collection("messages").document(dc.getDocument().getId()).set(res, SetOptions.merge());
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                            }
                                        });
                                        audio_uri = null;
                                        try {
                                            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                                        }
                                        catch (Exception ex) {

                                        }
                                    }

                                    if(img_uri != null){

                                        storageRef.child("Message").child(chatid).child("Image").child(dc.getDocument().getId()).child("image.jpg").putFile(img_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                storageRef.child("Message").child(chatid).child("Image").child(dc.getDocument().getId()).child("image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Map<String,Object> res = new HashMap<>();
                                                        res.put("res",String.valueOf(uri));
                                                        db.collection("Message").document(chatid).collection("messages").document(dc.getDocument().getId()).set(res, SetOptions.merge());
                                                    }
                                                });
                                                progressDialog.dismiss();

                                            }
                                        });
                                        img_uri = null;
                                        try {
                                            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                                        }
                                        catch (Exception ex) {

                                        }
                                    }
                                    if(video_uri != null){
                                        storageRef.child("Message").child(chatid).child("Video").child(dc.getDocument().getId()).child("video.mp4").putFile(video_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                storageRef.child("Message").child(chatid).child("Video").child(dc.getDocument().getId()).child("video.mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Map<String,Object> res = new HashMap<>();
                                                        res.put("res",String.valueOf(uri));
                                                        db.collection("Message").document(chatid).collection("messages").document(dc.getDocument().getId()).set(res, SetOptions.merge());
                                                    }
                                                });
                                                progressDialog.dismiss();

                                            }
                                        });
                                        video_uri = null;
                                        try {
                                            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                                        }
                                        catch (Exception ex) {

                                        }
                                    }
                                    if(document_uri != null){
                                        storageRef.child("Message").child(chatid).child("Document").child(dc.getDocument().getId()).child(doc_name).putFile(document_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                storageRef.child("Message").child(chatid).child("Document")
                                                        .child(dc.getDocument().getId())
                                                        .child(doc_name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Map<String,Object> res = new HashMap<>();
                                                        res.put("res",String.valueOf(uri));
                                                        db.collection("Message").document(chatid).collection("messages").document(dc.getDocument().getId()).set(res, SetOptions.merge());
                                                        doc_name = null;
                                                    }
                                                });
                                                progressDialog.dismiss();

                                            }
                                        });
                                        document_uri = null;

                                        try {
                                            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                                        }
                                        catch (Exception ex) {

                                        }
                                    }

                                    MessageModel messageModel = new MessageModel();
                                    messageModel = dc.getDocument().toObject(MessageModel.class);
                                    if(messageModel.getType() ==1)
                                    {
                                        if(messageModel.getEmail().compareTo(mFirebaseUser.getEmail()) == 0){
                                            responseMessageList.add(messageModel);
                                            messageAdapter.notifyDataSetChanged();
                                        }
                                        else{
                                            messageModel.setType(2);
                                            responseMessageList.add(messageModel);
                                            messageAdapter.notifyDataSetChanged();
                                        }

                                    }
                                    else if(messageModel.getRes().compareTo("") != 0)
                                    {
                                        if(messageModel.getEmail().compareTo(mFirebaseUser.getEmail()) == 0){
                                            responseMessageList.add(messageModel);
                                            messageAdapter.notifyDataSetChanged();
                                        }
                                        else{
                                            messageModel.setType(messageModel.getType()+1);
                                            responseMessageList.add(messageModel);
                                            messageAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    break;
                                case MODIFIED:
                                    if(String.valueOf(dc.getDocument().toObject(MessageModel.class).getType()).compareTo("1") == 0){

                                    }
                                    else{
                                        if (dc.getDocument().toObject(MessageModel.class).getRes().compareTo("") !=0){
                                            MessageModel messageModel1 = new MessageModel();
                                            messageModel1 = dc.getDocument().toObject(MessageModel.class);
                                            update_lastmsg("");
                                            if(messageModel1.getEmail().compareTo(mFirebaseUser.getEmail()) == 0)
                                            {
                                                final MessageModel message = new MessageModel(messageModel1.getMessage(), messageModel1.getType(), messageModel1.getRes(), messageModel1.getEmail(), messageModel1.getName(), messageModel1.getId());
                                                responseMessageList.add(message);
                                            }
                                            else {
                                                final MessageModel message = new MessageModel(messageModel1.getMessage(), messageModel1.getType()+1, messageModel1.getRes(), messageModel1.getEmail(), messageModel1.getName(), messageModel1.getId());
                                                responseMessageList.add(message);
                                            }

                                            messageAdapter.notifyDataSetChanged();
                                        }
                                        try {
                                            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                                        }
                                        catch (Exception ex) {

                                        }
                                    }
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                    }
                });
            progressDialog.dismiss();

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //////////////////////////////////////////////////////////
                        MessageModel messageModel = new MessageModel(user_input.getText().toString().replaceAll(" +", " ").replaceAll("\n\n+","\n\n").trim(),1,"",mFirebaseUser.getEmail(),mFirebaseUser.getDisplayName(),my_id);
                        update_lastmsg(user_input.getText().toString().replaceAll(" +", " ").replaceAll("\n\n+","\n\n").trim());
                        db.collection("Message").document(chatid).collection("messages").add(messageModel);
                        user_input.setText("");


                        ///////////////////////////////////////////////////////////////
                        try {
                            if (!isVisible()) {
                                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                            }
                        }
                        catch (Exception e){

                        }

                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                            @Override
                            public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == recyclerView.SCROLL_STATE_IDLE || newState == recyclerView.SCROLL_STATE_DRAGGING) {
                                    // scrollFab.setVisibility(View.VISIBLE);
                                    scrollFab.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == R.id.scrollFab) {
                                                scrollFab.setVisibility(View.INVISIBLE);
                                                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                                            }

                                        }
                                    });
                                }
                            }

                        });
                    }
                });

                user_input.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                        }
                        catch (Exception e){

                        }
                    }
                });


                user_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        hidden = false;
                        showDiag();
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        send();

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

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
                progressDialog.setMessage("Sending");
                progressDialog.show();

                MessageModel message = new MessageModel("",5,"", mFirebaseUser.getEmail(),mFirebaseUser.getDisplayName(),my_id);
                img_uri = result.getUri();
                db.collection("Message").document(chatid).collection("messages").add(message);

            }

        }
        else if (requestCode == GALLERY) {
            if (data != null) {
                progressDialog.setMessage("Sending");
                progressDialog.show();

                video_uri = data.getData();
                MessageModel message = new MessageModel("", 7, "", mFirebaseUser.getEmail(),mFirebaseUser.getDisplayName(),my_id);
                db.collection("Message").document(chatid).collection("messages").add(message);
            }
        } else if (requestCode == DOCUMENT) {
            if (data != null) {
                progressDialog.setMessage("Sending");
                progressDialog.show();
                document_uri = data.getData();
                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    String d = cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE));
                    doc_name = name;
                    final MessageModel message = new MessageModel(name, 9, "", mFirebaseUser.getEmail(),mFirebaseUser.getDisplayName(),my_id);
                    db.collection("Message").document(chatid).collection("messages").add(message);
                }
                else {
                    final MessageModel message = new MessageModel("", 9, "", mFirebaseUser.getEmail(),mFirebaseUser.getDisplayName(),my_id);
                    db.collection("Message").document(chatid).collection("messages").add(message);
                }


            }
        }


    }

    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    public void showDiag() {
        int w = mRevealView.getWidth();
        int h = mRevealView.getHeight();
        Log.i("w",Integer.toString(w));
        Log.i("w",Integer.toString(h));

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (attach.getX() + (attach.getWidth()/2));
        int cy = (int) (attach.getY())+ attach.getHeight() + 56;

        if(hidden)
        {
            mRevealView.setVisibility(View.VISIBLE);
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(mRevealView,cx,cy,0,endRadius);
            revealAnimator.setDuration(700);
            hidden=false;
            revealAnimator.start();
            try {

                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
            catch (Exception ex) {

            }
        }
        else
        {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mRevealView.setVisibility(View.GONE);
                    hidden=true;

                }
            });
            anim.setDuration(700);
            anim.start();

        }
    }

}