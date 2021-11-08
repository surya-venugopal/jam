package com.JAM.justaminute.ui.Test.Create_Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.JAM.justaminute.ui.MainActivity;
import com.JAM.justaminute.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Objects;

public class CreateQuizMain extends AppCompatActivity {

    private FloatingActionButton fab1;
    EditText test_des,test_name;

    FirebaseFirestore db=FirebaseFirestore.getInstance();
    String group_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_createquiz_main);
        try {
            Bundle bundle = getIntent().getExtras();
            group_id = bundle.getString("group_id");

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Retry",Toast.LENGTH_SHORT);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        test_name=findViewById(R.id.test_name);
        test_des=findViewById(R.id.test_des);
        test_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                send();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        fab1 = findViewById(R.id.fab);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateQuizMain.this, CreateQuestions.class);
                i.putExtra("test_name",test_name.getText().toString());
                i.putExtra("test_des",test_des.getText().toString());
                i.putExtra("group_id",group_id);
                startActivity(i);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void send()
    {
        String result=test_name.getText().toString().trim();

    }
}
