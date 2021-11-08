package com.JAM.justaminute.ui.home.Person;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.JAM.justaminute.ui.MainActivity;
import com.JAM.justaminute.ui.Permissions;
import com.JAM.justaminute.ui.Storage;
import com.JAM.justaminute.ui.home.Group.ChatGroup;
import com.JAM.justaminute.ui.home.HomePage.List_Users;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.JAM.justaminute.ui.home.MyProfile.Profile;
import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.LoginActivity;
import com.JAM.justaminute.ui.home.MyProfile.profile_ini;
import com.JAM.justaminute.ui.home.Group.AddGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class PersonFragment extends Fragment implements RecyclerViewClickInterface,View.OnClickListener {
    //Class
    List_Users deletePerson=null;


    //widgets
    FloatingActionButton add;


    //Firebase
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /////////////////////
    private List<List_Users> personList=new ArrayList<>();
    private List<List_Users> temp=new ArrayList<>();
    private RecyclerView recyclerView;
    PersonPageAdapter adapter;

    Intent intent ;
    View root;
    String chat_id,my_id,person_id;


    ProgressDialog progressDialog;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);



        progressDialog = new ProgressDialog(root.getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        add=root.findViewById(R.id.fabMain);
        add.setImageResource(R.drawable.person_add);
        add.setOnClickListener(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        chat_id ="";
        person_id ="";
        try {
            Bundle bundle = getActivity().getIntent().getExtras();
            chat_id = bundle.getString("chatid");
            person_id = bundle.getString("person");
        }
        catch (Exception e){
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        Permissions permissions = new Permissions(getContext(),getActivity());
        permissions.checkperm();
        updateUIfirebase(mFirebaseUser);
        return root;
    }


    private void updateUIfirebase(FirebaseUser account){
        if (account == null){
            Intent i = new Intent(root.getContext(), LoginActivity.class);
            startActivity(i);
            getActivity().finish();
        }
        else {

            recyclerView = root.findViewById(R.id.homeRecycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));


            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(dividerItemDecoration);
            adapter = new PersonPageAdapter(root.getContext(),personList, this);
            main();

        }

    }

    public  String getTimefromUTC(int time){
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
        if(cal.get(Calendar.AM_PM) == 0){
            ctime+=" am";
        }
        else{
            ctime+=" pm";
        }

        return ctime;
    }

    void main(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);

        newUsercheck();
        addFriend();
        db.collection("User").document(my_id).collection("friends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }
                for (final DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            break;
                        case MODIFIED:
                            if (personList.size() > 0) {

                            int ind = 0;
                            for (int i = 0; i < personList.size(); i++) {
                                if (personList.get(i).getPerson_id().compareTo(dc.getDocument().getString("id")) == 0) {
                                    ind = i;
                                    break;
                                }
                            }
                            List_Users temp = null;
                                temp = personList.get(ind);
                                temp.setLast_text(dc.getDocument().getString("last_message"));
                                temp.setLast_time(getTimefromUTC(((Long) dc.getDocument().get("last_time")).intValue()));
                            for (int j = ind; j > 0; j--) {
                                personList.set(j, personList.get(j - 1));
                            }
                            personList.set(0, temp);
                            recyclerView.setAdapter(adapter);
                             }
                            break;
                        case REMOVED:
                            break;
                    }
                }
            }
        });
    }

    private void newUsercheck(){

        db.collection("User").document(my_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {

                }
                else {
                    Intent i =new Intent(root.getContext(),profile_ini.class);
                    startActivity(i);
                    getActivity().finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
                Log.d("HomeFragment",String.valueOf(e));
            }
        });

    }

    private void addFriend(){
        db.collection("User").document(my_id).collection("friends").orderBy("last_time", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){

                    }
                else {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                        prepareTheList(validate_mailid(doc.getString("id")),doc.getString("last_message"),getTimefromUTC(((Long) doc.get("last_time")).intValue()));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
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
    ArrayList<String> person_index = new ArrayList<String>();

    void validate_index(){
        personList.clear();
        for(int i=0;i<temp.size();i++){
            for(int j=0;j<person_index.size();j++){
                if(person_index.get(i) == temp.get(j).getPerson_id()){
                    personList.add(temp.get(j));
                    break;
                }
            }
        }
        recyclerView.setAdapter(adapter);

    }


    int flag=0;
    private void prepareTheList(final String p_id, final String last_text, final String last_time) {
        person_index.add(p_id);

            db.collection("User").document(p_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    List_Users person = new List_Users(documentSnapshot.getString("username"), documentSnapshot.getString("dp_uri"), p_id,
                            last_text, last_time,0);
                    temp.add(person);
                    flag++;
                    if (flag == person_index.size()) {
                        validate_index();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
                }
            });

    }


    @Override
    public void onItemClick(int position) {
        Intent intent = null;
            try {
                intent=new Intent(getActivity().getApplicationContext(), Chat.class);
                intent.putExtra("person",personList.get(position).getPerson_id());
                intent.putExtra("person_name",personList.get(position).getPersonName());
                intent.putExtra("person_dp",personList.get(position).getPersonImage());
                startActivity(intent);
            }
            catch (Exception e){
                main();
            }




    }

    @Override
    public void onLongItemClick(final int position)  {

        deletePerson=personList.get(position);
        personList.remove(position);
        adapter.notifyItemRemoved(position);
        Snackbar.make(recyclerView,deletePerson.getPersonName(),Snackbar.LENGTH_LONG)
                .setAction("undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        personList.add(position,deletePerson);
                        adapter.notifyItemInserted(position);
                    }
                }).show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case 121:onLongItemClick(item.getGroupId());
                displayMessage("deleted");
                return true;
            case 122:displayMessage("Archieve this message");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    private void displayMessage(String message)
    {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("JAM");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabMain) {
            intent = new Intent(root.getContext(), AddPerson.class);
            startActivity(intent);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.profile:
                Intent intent=new Intent(root.getContext(), Profile.class);
                startActivity(intent);
                break;
            case R.id.storage:
                Intent intent1 = new Intent(root.getContext(), Storage.class);
                startActivity(intent1);
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
