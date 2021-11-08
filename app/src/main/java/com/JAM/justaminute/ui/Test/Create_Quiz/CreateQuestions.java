package com.JAM.justaminute.ui.Test.Create_Quiz;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.Test.Model.Question;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;



public class CreateQuestions extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText question, quest1, quest2;
    EditText aText, aText1, aText2;
    EditText bText, bText1, bText2;
    EditText cText, cText2;
    EditText dText, dText2, resText, resText1, resText2;
    RadioButton aRadio, aRadio1;
    RadioButton bRadio, bRadio1;
    RadioButton cRadio;
    RadioButton dRadio;

    ImageView image;

    CheckBox aCheck, bCheck, cCheck, dCheck;

    int currentQuestion = 1;
    int previousQuestion = 1;
    TextView questionNumber;

    ArrayList<Question> ques;
    JSONArray jsonArray;
    int[] selectedOption = new int[]{0,0,0,0};

    AlertDialog alertDialog;
    private View dialogvView;
    String fileName = "file";
    private FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    CardView fab, f2, fl;
    ArrayAdapter<String> type_of_ques_adapter;
    Spinner type_of_ques;
    View mcq, trf, mto;
    String topic,desp,group_id;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            Bundle bundle = getIntent().getExtras();
            topic = bundle.getString("test_name");
            desp = bundle.getString("test_des");
            group_id = bundle.getString("group_id");

        }
        catch (Exception e){
            finish();
        }

        jsonArray = new JSONArray();
        setContentView(R.layout.test_createquiz_createques);
        mcq = findViewById(R.id.mcq);
        trf = findViewById(R.id.trf);
        mto = findViewById(R.id.mto);

        mcq.setVisibility(View.VISIBLE);
        trf.setVisibility(View.INVISIBLE);
        mto.setVisibility(View.INVISIBLE);
        resText = findViewById(R.id.resText);
        resText1 = findViewById(R.id.resText1);
        resText2 = findViewById(R.id.resText2);
        resText.setVisibility(View.INVISIBLE);
        image=findViewById(R.id.image);
        type_of_ques = findViewById(R.id.type_of_ques);
        type_of_ques_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, this.getResources().getStringArray(R.array.type_of_ques));
        type_of_ques_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_of_ques.setAdapter(type_of_ques_adapter);
        type_of_ques.setOnItemSelectedListener(this);
        question = findViewById(R.id.questionView);
        quest1 = findViewById(R.id.questionView1);
        quest2=findViewById(R.id.questionView2);
        aText = findViewById(R.id.aText);
        bText = findViewById(R.id.bText);
        cText = findViewById(R.id.cText);
        dText = findViewById(R.id.dText);
        aText1 = findViewById(R.id.aText1);
        bText1 = findViewById(R.id.bText1);
        aText2 = findViewById(R.id.aText2);
        bText2 = findViewById(R.id.bText2);
        cText2 = findViewById(R.id.cText2);
        dText2 = findViewById(R.id.dText2);
        questionNumber = findViewById(R.id.questionNumber);
        aRadio = findViewById(R.id.aRadio);
        bRadio = findViewById(R.id.bRadio);
        cRadio = findViewById(R.id.cRadio);
        dRadio = findViewById(R.id.dRadio);
        aRadio1 = findViewById(R.id.aRadio1);
        bRadio1 = findViewById(R.id.bRadio1);
        aCheck = findViewById(R.id.aCheck);
        bCheck = findViewById(R.id.bCheck);
        cCheck = findViewById(R.id.cCheck);
        dCheck = findViewById(R.id.dCheck);
        auth = FirebaseAuth.getInstance();

        currentQuestion = 1;
        setListeners();

        ques = new ArrayList<>();

        alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        dialogvView = inflater.inflate(R.layout.dialog_testconfirm, null);


        fab = findViewById(R.id.nextfab);
        fl = findViewById(R.id.fab2);//save button
        f2 = findViewById(R.id.pre_card);
        progressDialog = new ProgressDialog(CreateQuestions.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Just A Minute");


        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jsonArray.length() != 0) {
                    final JSONObject tempObject = new JSONObject();
                    LayoutInflater li = LayoutInflater.from(CreateQuestions.this);
                    View promptsView = li.inflate(R.layout.dialog_testconfirm, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            CreateQuestions.this);

                    // set dialog_custom.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);
                    final EditText userTime = promptsView.findViewById(R.id.editTextDialogUserInput1);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            final String str = userTime.getText().toString();
                                            String temp2 = str;
                                            try {
                                                tempObject.put("Questions", jsonArray);
                                                final String TIME = userTime.getText().toString().trim();
                                                tempObject.put("Time", Integer.parseInt(temp2));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            final String jsonStr = tempObject.toString();

                                            if (str != null) {
                                                progressDialog.show();
                                                Map<String, Object> result = new Gson().fromJson(jsonStr, Map.class);
                                                result.put("test_name",topic);
                                                result.put("description",desp);

                                                db.collection("Test").document(group_id).collection("tests")
                                                .add(result).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        progressDialog.dismiss();
                                                        finish();
                                                    }
                                                });

                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else {
                    Toast.makeText(CreateQuestions.this, "Incomplete Question format", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateQuestions.this);
        builder.setMessage("Exit without saving?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void setAllData(int position) {
        clearAllData();
        Question question1 = new Question();
        question1 = ques.get(position - 1);
        questionNumber.setText(String.valueOf(question1.getId()));
        selectedOption = question1.getAnswer();
        type_of_ques.setSelection(question1.getType()-1);
        if(question1.getType()==1) {

            mcq.setVisibility(View.VISIBLE);
            trf.setVisibility(View.GONE);
            mto.setVisibility(View.GONE);
            resText.setVisibility(View.INVISIBLE);
            question.setText(question1.getQuestion());
            aText.setText(question1.getOpt_A());
            bText.setText(question1.getOpt_B());
            cText.setText(question1.getOpt_C());
            dText.setText(question1.getOpt_D());
            resText.setText(question1.getReason());
            int[] answer = question1.getAnswer();
            if (answer[0] == 1) {
                aRadio.setChecked(true);
            } else if (answer[1] == 1) {
                bRadio.setChecked(true);
            } else if (answer[2] == 1) {
                cRadio.setChecked(true);
            } else if (answer[3] == 1) {
                dRadio.setChecked(true);
            }

        }
        else
        if(question1.getType()==2)
        {
            mcq.setVisibility(View.VISIBLE);
            trf.setVisibility(View.GONE);
            mto.setVisibility(View.GONE);
            resText.setVisibility(View.VISIBLE);
            question.setText(question1.getQuestion());
            aText.setText(question1.getOpt_A());
            bText.setText(question1.getOpt_B());
            cText.setText(question1.getOpt_C());
            dText.setText(question1.getOpt_D());
            resText.setText(question1.getReason());
            int[] answer = question1.getAnswer();
            if (answer[0] == 1) {
                aRadio.setChecked(true);
            } else if (answer[1] == 1) {
                bRadio.setChecked(true);
            } else if (answer[2] == 1) {
                cRadio.setChecked(true);
            } else if (answer[3] == 1) {
                dRadio.setChecked(true);
            }
        }
        else
        if(question1.getType()==3)
        {
            mcq.setVisibility(View.GONE);
            trf.setVisibility(View.VISIBLE);
            mto.setVisibility(View.GONE);
            resText1.setVisibility(View.INVISIBLE);
            quest1.setText(question1.getQuestion());
            aText1.setText(question1.getOpt_A());
            bText1.setText(question1.getOpt_B());
            resText1.setText(question1.getReason());
            int[] answer = question1.getAnswer();
            if (answer[0] == 1) {
                aRadio1.setChecked(true);
            } else if (answer[1] == 1) {
                bRadio1.setChecked(true);
            }
        }
        else
        if(question1.getType()==4)
        {
            mcq.setVisibility(View.GONE);
            trf.setVisibility(View.VISIBLE);
            mto.setVisibility(View.GONE);
            resText1.setVisibility(View.VISIBLE);
            quest1.setText(question1.getQuestion());
            aText1.setText(question1.getOpt_A());
            bText1.setText(question1.getOpt_B());
            resText1.setText(question1.getReason());
            int[] answer = question1.getAnswer();
            if (answer[0] == 1) {
                aRadio1.setChecked(true);
            } else if (answer[1] == 1) {
                bRadio1.setChecked(true);
            }
        }
        else
        if(question1.getType()==5)
        {
            mcq.setVisibility(View.GONE);
            trf.setVisibility(View.GONE);
            mto.setVisibility(View.VISIBLE);
            resText2.setVisibility(View.INVISIBLE);
            quest2.setText(question1.getQuestion());
            aText2.setText(question1.getOpt_A());
            bText2.setText(question1.getOpt_B());
            cText2.setText(question1.getOpt_C());
            dText2.setText(question1.getOpt_D());
            resText2.setText(question1.getReason());
            int[] answer = question1.getAnswer();
            if (answer[0] == 1) {
                aCheck.setChecked(true);
            }
            if (answer[1] == 1) {
                bCheck.setChecked(true);
            }
            if (answer[2] == 1) {
                cCheck.setChecked(true);
            }
            if (answer[3] == 1) {
                dCheck.setChecked(true);
            }
        }
        else
        if(question1.getType()==6)
        {
            mcq.setVisibility(View.GONE);
            trf.setVisibility(View.GONE);
            mto.setVisibility(View.VISIBLE);
            resText2.setVisibility(View.VISIBLE);
            quest2.setText(question1.getQuestion());
            aText2.setText(question1.getOpt_A());
            bText2.setText(question1.getOpt_B());
            cText2.setText(question1.getOpt_C());
            dText2.setText(question1.getOpt_D());
            resText2.setText(question1.getReason());
            int[] answer = question1.getAnswer();
            if (answer[0] == 1) {
                aCheck.setChecked(true);
            }
            if (answer[1] == 1) {
                bCheck.setChecked(true);
            }
            if (answer[2] == 1) {
                cCheck.setChecked(true);
            }
            if (answer[3] == 1) {
                dCheck.setChecked(true);
            }
        }
    }

    private void clearAllData() {

        if(mcq.getVisibility()==View.VISIBLE) {
            aRadio.setChecked(false);
            bRadio.setChecked(false);
            cRadio.setChecked(false);
            dRadio.setChecked(false);
            aText.setText(null);
            bText.setText(null);
            cText.setText(null);
            dText.setText(null);
            if (resText.getVisibility() == View.VISIBLE) {
                resText.setText(null);
            }
            question.setText(null);
            selectedOption =  new int[]{0, 0, 0, 0};
        }
        else
        if(trf.getVisibility()==View.VISIBLE)
        {
            aRadio1.setChecked(false);
            bRadio1.setChecked(false);
            aText1.setText(null);
            bText1.setText(null);
            if (resText1.getVisibility() == View.VISIBLE) {
                resText1.setText(null);
            }
            quest1.setText(null);
            selectedOption =  new int[]{0, 0, 0, 0};
        }
        else
        if(mto.getVisibility()==View.VISIBLE)
        {
            aText2.setText(null);
            bText2.setText(null);
            cText2.setText(null);
            dText2.setText(null);
            quest2.setText(null);
            aCheck.setChecked(false);
            bCheck.setChecked(false);
            cCheck.setChecked(false);
            dCheck.setChecked(false);
            if (resText2.getVisibility() == View.VISIBLE) {
                resText2.setText(null);
            }
            selectedOption = new int[]{0, 0, 0, 0};
        }
    }

    private void setListeners() {
        aRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bRadio.setChecked(false);
                cRadio.setChecked(false);
                dRadio.setChecked(false);
            }
        });
        bRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aRadio.setChecked(false);
                cRadio.setChecked(false);
                dRadio.setChecked(false);
            }
        });
        cRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bRadio.setChecked(false);
                aRadio.setChecked(false);
                dRadio.setChecked(false);
            }
        });
        dRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bRadio.setChecked(false);
                cRadio.setChecked(false);
                aRadio.setChecked(false);
            }
        });
        aRadio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bRadio1.setChecked(false);
            }
        });
        bRadio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aRadio1.setChecked(false);
            }
        });
        aCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bCheck.isChecked())
                {
                    bCheck.setChecked(true);
                }
                else
                {
                    bCheck.setChecked(false);
                }
                if(cCheck.isChecked())
                {
                    cCheck.setChecked(true);
                }
                else
                {
                    cCheck.setChecked(false);
                }
                if(dCheck.isChecked())
                {
                    dCheck.setChecked(true);
                }
                else
                {
                    dCheck.setChecked(false);
                }


            }
        });
        bCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedOption[1] = 1;
                if(aCheck.isChecked())
                {
                    aCheck.setChecked(true);
                }
                else
                {
                    aCheck.setChecked(false);
                }
                if(cCheck.isChecked())
                {
                    cCheck.setChecked(true);
                }
                else
                {
                    cCheck.setChecked(false);
                }
                if(dCheck.isChecked())
                {
                    dCheck.setChecked(true);
                }
                else
                {
                    dCheck.setChecked(false);
                }


            }
        });
        cCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedOption[2] = 1;
                if(aCheck.isChecked())
                {
                    aCheck.setChecked(true);
                }
                else
                {
                    aCheck.setChecked(false);
                }
                if(bCheck.isChecked())
                {
                    bCheck.setChecked(true);
                }
                else
                {
                    bCheck.setChecked(false);
                }
                if(dCheck.isChecked())
                {
                    dCheck.setChecked(true);
                }
                else
                {
                    dCheck.setChecked(false);
                }

            }
        });
        dCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedOption[3] = 1;
                if(aCheck.isChecked())
                {
                    aCheck.setChecked(true);
                }
                else
                {
                    aCheck.setChecked(false);
                }
                if(cCheck.isChecked())
                {
                    cCheck.setChecked(true);
                }
                else
                {
                    cCheck.setChecked(false);
                }
                if(bCheck.isChecked())
                {
                    bCheck.setChecked(true);
                }
                else
                {
                    bCheck.setChecked(false);
                }

            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (type_of_ques.getSelectedItem().toString().equals("MCQ")) {
            mcq.setVisibility(View.VISIBLE);
            trf.setVisibility(View.GONE);
            mto.setVisibility(View.GONE);
            resText.setVisibility(View.INVISIBLE);

            mcqFunc();


        } else if (type_of_ques.getSelectedItem().toString().equals("MCQ with Reason")) {
            mcq.setVisibility(View.VISIBLE);
            trf.setVisibility(View.GONE);
            mto.setVisibility(View.GONE);
            resText.setVisibility(View.VISIBLE);

            mcqFunc();

        } else if (type_of_ques.getSelectedItem().toString().equals("True or False")) {
            mcq.setVisibility(View.GONE);
            trf.setVisibility(View.VISIBLE);
            mto.setVisibility(View.GONE);
            resText1.setVisibility(View.INVISIBLE);

            trfFunc();
        } else if (type_of_ques.getSelectedItem().toString().equals("True or False with Reason")) {
            mcq.setVisibility(View.GONE);
            trf.setVisibility(View.VISIBLE);
            mto.setVisibility(View.GONE);
            resText1.setVisibility(View.VISIBLE);

            trfFunc();
        } else if (type_of_ques.getSelectedItem().toString().equals("Multi Correct")) {
            mcq.setVisibility(View.GONE);
            trf.setVisibility(View.GONE);
            mto.setVisibility(View.VISIBLE);
            resText2.setVisibility(View.INVISIBLE);

            mtoFunc();
        } else if (type_of_ques.getSelectedItem().toString().equals("Multi Correct with Reason")) {
            mcq.setVisibility(View.GONE);
            trf.setVisibility(View.GONE);
            mto.setVisibility(View.VISIBLE);
            resText2.setVisibility(View.VISIBLE);

            mtoFunc();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void mcqFunc() {


        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (previousQuestion > 1) {
                    previousQuestion--;
                    setAllData(previousQuestion);
                }
                if (previousQuestion == 1)
                    f2.setVisibility(View.INVISIBLE);
                //Question question1 = new Question();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean cont = getEnteredQuestionsValue();
                if (cont && previousQuestion != currentQuestion) {
                    previousQuestion++;
                    if (previousQuestion != currentQuestion)
                    {
                        setAllData(previousQuestion);

                    }
                    else {
                        clearAllData();
                        questionNumber.setText(String.valueOf(currentQuestion));
                    }
                    if (previousQuestion > 1)
                        f2.setVisibility(View.VISIBLE);
                }

                else if (cont) {

                    previousQuestion++;
                    currentQuestion++;

                    questionNumber.setText(String.valueOf(currentQuestion));
                    clearAllData();
                    f2.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void trfFunc() {
        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (previousQuestion > 1) {
                    previousQuestion--;
                    setAllData(previousQuestion);
                }
                if (previousQuestion == 1)
                    f2.setVisibility(View.INVISIBLE);
                //Question question1 = new Question();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cont = getEnteredQuestionsValue2();
                if (cont && previousQuestion != currentQuestion) {
                    previousQuestion++;
                    if (previousQuestion != currentQuestion)
                        setAllData(previousQuestion);
                    else {
                        clearAllData();
                        questionNumber.setText(String.valueOf(currentQuestion));
                    }
                    if (previousQuestion > 1)
                        f2.setVisibility(View.VISIBLE);
                }

                else if (cont) {
                    previousQuestion++;
                    currentQuestion++;
                    questionNumber.setText(String.valueOf(currentQuestion));
                    clearAllData();
                    f2.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void mtoFunc() {
        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (previousQuestion > 1) {
                    previousQuestion--;
                    setAllData(previousQuestion);
                }
                if (previousQuestion == 1)
                    f2.setVisibility(View.INVISIBLE);
                //Question question1 = new Question();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cont = getEnteredQuestionsValue3();
                if (cont && previousQuestion != currentQuestion) {
                    previousQuestion++;
                    if (previousQuestion != currentQuestion)
                        setAllData(previousQuestion);
                    else {
                        clearAllData();
                        questionNumber.setText(String.valueOf(currentQuestion));
                    }
                    if (previousQuestion > 1)
                        f2.setVisibility(View.VISIBLE);
                }

                else if (cont) {
                    previousQuestion++;
                    currentQuestion++;
                    questionNumber.setText(String.valueOf(currentQuestion));
                    clearAllData();
                    f2.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean getEnteredQuestionsValue() {

        boolean cont = false;
        if (question.getText().toString().trim().isEmpty()) {
            question.setError("Please fill in a question");
        } else if (TextUtils.isEmpty(aText.getText().toString().trim())) {
            aText.setError("Please fill in option A");
        } else if (TextUtils.isEmpty(bText.getText().toString().trim())) {
            bText.setError("Please fill in option B");
        } else if (TextUtils.isEmpty(cText.getText().toString().trim())) {
            cText.setError("Please fill in option C");
        } else if (TextUtils.isEmpty(dText.getText().toString().trim())) {
            dText.setError("Please fill in option D");
        } else if (!aRadio.isChecked() && !bRadio.isChecked() && !cRadio.isChecked() && !dRadio.isChecked()) {
            Toast.makeText(this, "Please select the correct answer", Toast.LENGTH_SHORT).show();
        } else {

            selectedOption = new int[]{0,0,0,0};
            if(aRadio.isChecked()){
                selectedOption[0] = 1;
                selectedOption[1] = 0;
                selectedOption[2] = 0;
                selectedOption[3] = 0;
            }
            else if(bRadio.isChecked()){
                selectedOption[1] = 1;
                selectedOption[0] = 0;
                selectedOption[2] = 0;
                selectedOption[3] = 0;
            }
            else if(cRadio.isChecked()){
                selectedOption[2] = 1;
                selectedOption[1] = 0;
                selectedOption[0] = 0;
                selectedOption[3] = 0;
            }
            else if(dRadio.isChecked()){
                selectedOption[3] = 1;
                selectedOption[1] = 0;
                selectedOption[2] = 0;
                selectedOption[0] = 0;
            }


            Question quest = new Question();
            quest.setId(previousQuestion);
            quest.setQuestion(question.getText().toString());
            quest.setOpt_A(aText.getText().toString());
            quest.setOpt_B(bText.getText().toString());
            quest.setOpt_C(cText.getText().toString());
            quest.setOpt_D(dText.getText().toString());
            if(resText.getVisibility()==View.VISIBLE) {
                quest.setReason(resText.getText().toString());
                quest.setAnswer(selectedOption);
                quest.setType(2);
            }
            else
            {
                quest.setReason(resText.getText().toString());
                quest.setAnswer(selectedOption);
                quest.setType(1);
            }

            try {
                ques.set(previousQuestion-1,quest);
            }
            catch (Exception e){
                ques.add(quest);
                try {
                    Toast.makeText(this, String.valueOf(ques.get(previousQuestion-1).getQuestion()), Toast.LENGTH_SHORT).show();
                }
                catch (Exception r){

                }
            }

            cont = true;

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("opt_A", aText.getText().toString().trim());
                jsonObject.put("opt_B", bText.getText().toString().trim());
                jsonObject.put("opt_C", cText.getText().toString().trim());
                jsonObject.put("opt_D", dText.getText().toString().trim());
                jsonObject.put("reason", resText.getText().toString().trim());
                if(resText.getVisibility()==View.VISIBLE) {
                    jsonObject.put("type", 2);
                    jsonObject.put("answer", Arrays.toString(selectedOption));
                }
                else
                {
                    jsonObject.put("type", 1);
                    jsonObject.put("answer", Arrays.toString(selectedOption));
                }
                jsonObject.put("question", question.getText().toString().trim());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                jsonArray.put(previousQuestion-1,jsonObject);
            } catch (JSONException e) {
                jsonArray.put(jsonObject);
            }
        }
        return cont;
    }

    private boolean getEnteredQuestionsValue2() {

        boolean cont = false;
        if (quest1.getText().toString().trim().isEmpty()) {
            quest1.setError("Please fill in a question");
        } else if (TextUtils.isEmpty(aText1.getText().toString().trim())) {
            aText1.setError("Please fill in option A");
        } else if (TextUtils.isEmpty(bText1.getText().toString().trim())) {
            bText1.setError("Please fill in option B");
        } else if (!aRadio1.isChecked() && !bRadio1.isChecked()) {
            Toast.makeText(this, "Please select the correct answer", Toast.LENGTH_SHORT).show();
        } else {
            selectedOption = new int[]{0,0,0,0};
            if(aRadio1.isChecked()){
                selectedOption[0] = 1;
                selectedOption[1] = 0;
                selectedOption[2] = 0;
                selectedOption[3] = 0;
            }
            else if(bRadio1.isChecked()){
                selectedOption[1] = 1;
                selectedOption[0] = 0;
                selectedOption[2] = 0;
                selectedOption[3] = 0;
            }

            Question quest = new Question();
            quest.setId(previousQuestion);
            quest.setQuestion(quest1.getText().toString());
            quest.setOpt_A(aText1.getText().toString());
            quest.setOpt_B(bText1.getText().toString());
            if(resText1.getVisibility()==View.VISIBLE) {
                quest.setReason(resText1.getText().toString());
                quest.setAnswer(selectedOption);
                quest.setType(4);
            }
            else
            {
                quest.setReason(resText1.getText().toString());
                quest.setAnswer(selectedOption);
                quest.setType(3);
            }
            try {
                ques.set(previousQuestion-1,quest);
            }
            catch (Exception e){
                ques.add(quest);
            }
            cont = true;

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("opt_A", aText1.getText().toString().trim());
                jsonObject.put("opt_B", bText1.getText().toString().trim());
                jsonObject.put("reason", resText1.getText().toString().trim());
                if(resText1.getVisibility()==View.VISIBLE) {
                    jsonObject.put("type",4);
                    jsonObject.put("answer", Arrays.toString(selectedOption));
                }
                else
                {
                    jsonObject.put("type",3);
                    jsonObject.put("answer", Arrays.toString(selectedOption));
                }
                jsonObject.put("question", quest1.getText().toString().trim());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                jsonArray.put(previousQuestion-1,jsonObject);
            } catch (JSONException e) {
                jsonArray.put(jsonObject);
            }
        }
        return cont;
    }

    private boolean getEnteredQuestionsValue3() {

        boolean cont = false;
        if (quest2.getText().toString().trim().isEmpty()) {
            quest2.setError("Please fill in a question");
        } else if (TextUtils.isEmpty(aText2.getText().toString().trim())) {
            aText2.setError("Please fill in option A");
        } else if (TextUtils.isEmpty(bText2.getText().toString().trim())) {
            bText2.setError("Please fill in option B");
        }
        else if (TextUtils.isEmpty(cText2.getText().toString().trim())) {
            cText2.setError("Please fill in option C");
        }
        else if (TextUtils.isEmpty(dText2.getText().toString().trim())) {
            dText2.setError("Please fill in option D");
        } else if (!aCheck.isChecked() && !bCheck.isChecked() && !cCheck.isChecked() && !dCheck.isChecked() ) {
            Toast.makeText(this, "Please select the correct answer", Toast.LENGTH_SHORT).show();
        } else {
            selectedOption = new int[]{0,0,0,0};
            if(aCheck.isChecked()){
                selectedOption[0]=1;
            }
            if(bCheck.isChecked()){
                selectedOption[1]=1;
            }
            if(cCheck.isChecked()){
                selectedOption[2]=1;
            }
            if(dCheck.isChecked()){
                selectedOption[3]=1;
            }

            Question quest = new Question();
            quest.setId(previousQuestion);
            quest.setQuestion(quest2.getText().toString());
            quest.setOpt_A(aText2.getText().toString());
            quest.setOpt_B(bText2.getText().toString());
            quest.setOpt_C(cText2.getText().toString());
            quest.setOpt_D(dText2.getText().toString());
            quest.setReason(resText2.getText().toString());
            if(resText2.getVisibility()==View.VISIBLE) {
                quest.setAnswer(selectedOption);
                quest.setType(6);
            }
            else
            {
                quest.setAnswer(selectedOption);
                quest.setType(5);
            }

            try {
                ques.set(previousQuestion-1,quest);
            }
            catch (Exception e){
                ques.add(quest);
            }
            cont = true;

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("opt_A", aText2.getText().toString().trim());
                jsonObject.put("opt_B", bText2.getText().toString().trim());
                jsonObject.put("opt_C", cText2.getText().toString().trim());
                jsonObject.put("opt_D", dText2.getText().toString().trim());
                jsonObject.put("reason", resText2.getText().toString().trim());
                if(resText2.getVisibility()==View.VISIBLE) {
                    jsonObject.put("type",6);
                    jsonObject.put("answer", Arrays.toString(selectedOption));
                }
                else
                {
                    jsonObject.put("type",5);
                    jsonObject.put("answer", Arrays.toString(selectedOption));
                }
                jsonObject.put("question", quest2.getText().toString().trim());

            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                jsonArray.put(previousQuestion-1,jsonObject);
            } catch (JSONException e) {
                jsonArray.put(jsonObject);
            }

        }
        return cont;
    }


}