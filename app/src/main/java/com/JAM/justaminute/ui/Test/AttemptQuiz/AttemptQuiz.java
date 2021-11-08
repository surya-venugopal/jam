package com.JAM.justaminute.ui.Test.AttemptQuiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.JAM.justaminute.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;

import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.JAM.justaminute.ui.Test.Model.Question;
import com.JAM.justaminute.ui.Test.Model.Test;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class AttemptQuiz extends AppCompatActivity {
    ArrayList<Question> questions = new ArrayList<>();
    String []answers;
    Toolbar toolbar;
    ArrayList<String> list;
    ArrayList<String> arrayList;
    int flag_controller = 1;
    long timer = 50;
    Button next,prev;
    private String TESTNAME;
    private RadioGroup group;
    private int countPaused = 0;

    View frag_test,frag_trf,frag_mto;

    int index,i;

    TextView quesNum;

    TextView questionText,questionText1,questionText2;
    TextView radioText,radioText2,radioText3,radioText4;
    TextView rbText,rbText2,rbText3;
    TextView cbText,cbText2,cbText3,cbText4;
    RadioButton r1,r2,r3,r4,r5,rb1,rb2,rb3;
    EditText resText,resText1,resText2;
    CheckBox cb1,cb2,cb3,cb4;


    JSONArray jsonArray;
    String test_id,group_id,my_id;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_attempt);

        try {
            Bundle bundle = getIntent().getExtras();
            test_id = bundle.getString("test_id");
            group_id = bundle.getString("group_id");

        }
        catch (Exception e){
            finish();
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        my_id = mFirebaseUser.getEmail();
        my_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);

        next=findViewById(R.id.next);
        prev=findViewById(R.id.prev);
        prev.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);


        db.collection("Test").document(group_id).collection("tests").document(test_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot a= task.getResult();
                    TESTNAME = a.getString("test_name");
                    ArrayList<HashMap<String,Object>> b = new ArrayList<>();
                    b=(ArrayList<HashMap<String,Object>>) a.get("Questions");
                    answers=new String[b.size()];
                    for (int i=0;i<b.size();i++){
                        questions.add(new Question(i,String.valueOf(b.get(i).get("question")),String.valueOf(b.get(i).get("opt_A")),
                                String.valueOf(b.get(i).get("opt_B")),String.valueOf(b.get(i).get("opt_C")),String.valueOf(b.get(i).get("opt_D")),
                                "", parseAns(String.valueOf(b.get(i).get("answer")))
                                ,Integer.parseInt(String.valueOf(b.get(i).get("type")).substring(0,String.valueOf(b.get(i).get("type")).length()-2))));
                        answers[i] = "";
                    }
                    timer =  Long.parseLong(String.valueOf(a.get("Time")).substring(0,String.valueOf(a.get("Time")).length()-2));

                    main();
                }
            }
        });

    }

    int[] parseAns(String arr){

        int[] ans = new int[]{0,0,0,0};
        String flag = "";
        int temp=0;
        for(int i=1;i<arr.length()-1;i++){
            Log.d("qwert", String.valueOf(i));
            if(arr.charAt(i) == ','){
                flag = flag.replace(" ","");
                ans[temp] = Integer.parseInt(flag);
                temp++;
                flag="";
            }
            else{
                flag +=arr.charAt(i);
            }
        }
        return ans;
    }

    void main(){
        next.setVisibility(View.VISIBLE);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle(TESTNAME);

        setSupportActionBar(toolbar);
       // toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        index=0;

        jsonArray = new JSONArray();
        quesNum=findViewById(R.id.quesNum);

        frag_test=findViewById(R.id.mcq1);
        frag_trf=findViewById(R.id.trf1);
        frag_mto=findViewById(R.id.mto1);

        questionText =findViewById(R.id.questionTextView);
        r1=findViewById(R.id.radioButton);
        r2=findViewById(R.id.radioButton2);
        r3=findViewById(R.id.radioButton3);
        r4=findViewById(R.id.radioButton4);
        r5 =findViewById(R.id.radioButton5);
        radioText=findViewById(R.id.radioText);
        radioText2=findViewById(R.id.radioText2);
        radioText3=findViewById(R.id.radioText3);
        radioText4=findViewById(R.id.radioText4);
        resText=findViewById(R.id.rt);

        questionText1 =findViewById(R.id.questionTextView1);
        rb1=findViewById(R.id.rb);
        rb2=findViewById(R.id.rb2);
        rb3=findViewById(R.id.rb3);
        rbText=findViewById(R.id.rbText);
        rbText2=findViewById(R.id.rbText2);
        rbText3=findViewById(R.id.rbText3);
        resText1=findViewById(R.id.rt1);

        questionText2=findViewById(R.id.questionTextView2);
        cb1=findViewById(R.id.cb);
        cb2=findViewById(R.id.cb2);
        cb3=findViewById(R.id.cb3);
        cb4=findViewById(R.id.cb4);
        cbText=findViewById(R.id.cbText);
        cbText2=findViewById(R.id.cbText2);
        cbText3=findViewById(R.id.cbText3);
        cbText4=findViewById(R.id.cbText4);
        resText2=findViewById(R.id.rt2);

        if(index<questions.size()-1) {
            i=index+1;
            quesNum.setText("Question "+String.valueOf(i)+"/"+questions.size());
            if (questions.get(index).getType()==1) {
                frag_test.setVisibility(View.VISIBLE);
                frag_trf.setVisibility(View.GONE);
                frag_mto.setVisibility(View.GONE);
                resText.setVisibility(View.INVISIBLE);
                mcqFunc(index);
            } else if (questions.get(index).getType() == 2) {
                frag_test.setVisibility(View.VISIBLE);
                frag_trf.setVisibility(View.GONE);
                frag_mto.setVisibility(View.GONE);
                resText.setVisibility(View.VISIBLE);
                mcqFunc(index);
            } else if (questions.get(index).getType() == 3) {
                frag_test.setVisibility(View.GONE);
                frag_trf.setVisibility(View.VISIBLE);
                frag_mto.setVisibility(View.GONE);
                resText.setVisibility(View.INVISIBLE);
                trfFunc(index);

            } else if (questions.get(index).getType() == 4) {
                frag_test.setVisibility(View.GONE);
                frag_trf.setVisibility(View.VISIBLE);
                frag_mto.setVisibility(View.GONE);
                resText.setVisibility(View.VISIBLE);
                trfFunc(index);

            }
            else if(questions.get(index).getType()==5)
            {
                frag_test.setVisibility(View.GONE);
                frag_trf.setVisibility(View.GONE);
                frag_mto.setVisibility(View.VISIBLE);
                resText.setVisibility(View.INVISIBLE);
                cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(false);

                mtoFunc(index);
            }
            else if(questions.get(index).getType()==6)
            {
                frag_test.setVisibility(View.GONE);
                frag_trf.setVisibility(View.GONE);
                frag_mto.setVisibility(View.VISIBLE);
                resText.setVisibility(View.VISIBLE);
                cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(false);

                mtoFunc(index);
            }
        }
        else
        {
            showPopUp();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i!=questions.size()) {
                    quesNum.setText("Question " + String.valueOf(i + 1) + "/" + questions.size());
                    i++;
                }
                if(index==questions.size()-1){
                    showPopUp();
                }else {

                    if(questions.get(index+1).getType()==1)
                    {
                        frag_test.setVisibility(View.VISIBLE);
                        frag_trf.setVisibility(View.GONE);
                        frag_mto.setVisibility(View.GONE);
                        resText.setVisibility(View.INVISIBLE);
                        mcqFunc(index+1);
                        index++;
                    }
                    else
                    if(questions.get(index+1).getType()==2)
                    {
                        frag_test.setVisibility(View.VISIBLE);
                        frag_trf.setVisibility(View.GONE);
                        frag_mto.setVisibility(View.GONE);
                        resText.setVisibility(View.VISIBLE);
                        mcqFunc(index+1);
                        index++;
                    }
                    else
                    if(questions.get(index+1).getType()==3)
                    {
                        frag_test.setVisibility(View.GONE);
                        frag_trf.setVisibility(View.VISIBLE);
                        frag_mto.setVisibility(View.GONE);
                        resText1.setVisibility(View.INVISIBLE);
                        trfFunc(index+1);
                        index++;
                    }
                    else
                    if(questions.get(index+1).getType()==4)
                    {
                        frag_test.setVisibility(View.GONE);
                        frag_trf.setVisibility(View.VISIBLE);
                        frag_mto.setVisibility(View.GONE);
                        resText1.setVisibility(View.VISIBLE);
                        trfFunc(index+1);
                        index++;
                    }
                    else
                    if(questions.get(index+1).getType()==5)
                    {
                        frag_test.setVisibility(View.GONE);
                        frag_trf.setVisibility(View.GONE);
                        frag_mto.setVisibility(View.VISIBLE);
                        resText2.setVisibility(View.INVISIBLE);
                        cb1.setChecked(false);
                        cb2.setChecked(false);
                        cb3.setChecked(false);
                        cb4.setChecked(false);
                        mtoFunc(index+1);
                        index++;
                    }
                    else
                    if(questions.get(index+1).getType()==6)
                    {
                        frag_test.setVisibility(View.GONE);
                        frag_trf.setVisibility(View.GONE);
                        frag_mto.setVisibility(View.VISIBLE);
                        resText2.setVisibility(View.VISIBLE);
                        cb1.setChecked(false);
                        cb2.setChecked(false);
                        cb3.setChecked(false);
                        cb4.setChecked(false);
                        mtoFunc(index+1);
                        index++;
                    }

                }
            }
        });


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(i!=0)
                {
                    quesNum.setText("Question "+String.valueOf(i-1)+"/"+questions.size());
                    i--;
                }
                if(index!=0){
                    //setNextPrevButton(scrollView.getCurrentItem()-1);
                    prev.setText("previous");
                    if(questions.get(index-1).getType()==1)
                    {
                        frag_test.setVisibility(View.VISIBLE);
                        frag_trf.setVisibility(View.GONE);
                        frag_mto.setVisibility(View.GONE);
                        resText.setVisibility(View.INVISIBLE);
                        mcqFunc(index-1);
                        index--;
                    }
                    else
                    if(questions.get(index-1).getType()==2)
                    {
                        frag_test.setVisibility(View.VISIBLE);
                        frag_trf.setVisibility(View.GONE);
                        frag_mto.setVisibility(View.GONE);
                        resText.setVisibility(View.VISIBLE);
                        mcqFunc(index-1);
                        index--;
                    }
                    else
                    if(questions.get(index-1).getType()==3)
                    {
                        frag_test.setVisibility(View.GONE);
                        frag_trf.setVisibility(View.VISIBLE);
                        frag_mto.setVisibility(View.GONE);
                        resText1.setVisibility(View.INVISIBLE);
                        trfFunc(index-1);
                        index--;
                    }
                    else
                    if(questions.get(index-1).getType()==4)
                    {
                        frag_test.setVisibility(View.GONE);
                        frag_trf.setVisibility(View.VISIBLE);
                        frag_mto.setVisibility(View.GONE);
                        resText1.setVisibility(View.VISIBLE);
                        trfFunc(index-1);
                        index--;
                    }
                    else
                    if(questions.get(index-1).getType()==5)
                    {
                        frag_test.setVisibility(View.GONE);
                        frag_trf.setVisibility(View.GONE);
                        frag_mto.setVisibility(View.VISIBLE);
                        resText2.setVisibility(View.INVISIBLE);
                        cb1.setChecked(false);
                        cb2.setChecked(false);
                        cb3.setChecked(false);
                        cb4.setChecked(false);
                        mtoFunc(index-1);
                        index--;
                    }
                    else
                    if(questions.get(index-1).getType()==6)
                    {
                        frag_test.setVisibility(View.GONE);
                        frag_trf.setVisibility(View.GONE);
                        frag_mto.setVisibility(View.VISIBLE);
                        resText2.setVisibility(View.VISIBLE);
                        cb1.setChecked(false);
                        cb2.setChecked(false);
                        cb3.setChecked(false);
                        cb4.setChecked(false);
                        mtoFunc(index-1);
                        index--;
                    }


                }
            }
        });

    }

    public void mcqFunc(final int position)
    {
        setNextPrevButton(position);
        questionText.setText(questions.get(position).getQuestion());
        radioText.setText(questions.get(position).getOpt_A());
        radioText2.setText(questions.get(position).getOpt_B());
        radioText3.setText(questions.get(position).getOpt_C());
        radioText4.setText(questions.get(position).getOpt_D());

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(r1.isChecked())
                {
                    r2.setChecked(false);
                    r3.setChecked(false);
                    r4.setChecked(false);
                    answers[position]="A";
                }
                else
                {
                    r1.setChecked(false);
                    answers[position]=null;
                }
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(r2.isChecked())
                {
                    r1.setChecked(false);
                    r3.setChecked(false);
                    r4.setChecked(false);
                    answers[position]="B";
                }
                else
                {
                    r2.setChecked(false);
                    answers[position]=null;
                }
            }
        });
        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(r3.isChecked())
                {
                    r1.setChecked(false);
                    r2.setChecked(false);
                    r4.setChecked(false);
                    answers[position]="C";
                }
                else
                {
                    r3.setChecked(false);
                    answers[position]=null;
                }
            }
        });
        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(r4.isChecked())
                {
                    r1.setChecked(false);
                    r2.setChecked(false);
                    r3.setChecked(false);
                    answers[position]="D";
                }
                else
                {
                    r4.setChecked(false);
                    answers[position]=null;
                }
            }
        });

        r5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                answers[position]=null;
                r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(false);

            }
        });

        if(answers[position]==null)
        {
            r1.setChecked(false);
            r2.setChecked(false);
            r3.setChecked(false);
            r4.setChecked(false);
            r5.setChecked(false);
        }
        else
        if(answers[position].equals("A"))
        {
            r1.setChecked(true);
        }
        else
        if(answers[position].equals("B"))
        {
            r2.setChecked(true);
        }
        else
        if(answers[position].equals("C"))
        {
            r3.setChecked(true);
        }
        else
        if(answers[position].equals("D"))
        {
            r4.setChecked(true);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opt_A",radioText.getText().toString());
            jsonObject.put("opt_B",radioText2.getText().toString());
            jsonObject.put("opt_C",radioText3.getText().toString());
            jsonObject.put("opt_D",radioText4.getText().toString());
            jsonObject.put("reason", questions.get(position).getReason());
            if(resText.getVisibility()==View.VISIBLE) {
                jsonObject.put("type", questions.get(position).getType());
                jsonObject.put("answer", answers[position]);
            }
            else
            {
                jsonObject.put("type", questions.get(position).getType());
                jsonObject.put("answer", answers[position]);
            }
            jsonObject.put("question", questionText.getText().toString());
            jsonArray.put(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void trfFunc(final int position)
    {
        setNextPrevButton(position);
        questionText1.setText(questions.get(position).getQuestion());
        rbText.setText(questions.get(position).getOpt_A());
        rbText2.setText(questions.get(position).getOpt_B());
        rbText3.setText("clear selected");

        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rb1.isChecked())
                {
                    rb2.setChecked(false);
                    answers[position]="A";
                }
                else
                {
                    rb1.setChecked(false);
                    answers[position]=null;
                }
            }
        });
        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rb2.isChecked())
                {
                    rb1.setChecked(false);
                    answers[position]="B";
                }
                else
                {
                    rb2.setChecked(false);
                    answers[position]=null;
                }
            }
        });

        rb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
            }
        });

        if(answers[position]==null)
        {
            rb1.setChecked(false);
            rb2.setChecked(false);
            rb3.setChecked(false);
        }
        else
        if(answers[position].equals("A"))
        {
            rb1.setChecked(true);
        }
        else
        if(answers[position].equals("B"))
        {
            rb2.setChecked(true);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opt_A", rbText.getText().toString());
            jsonObject.put("opt_B", rbText2.getText().toString());
            jsonObject.put("reason", questions.get(position).getReason());
            if(resText1.getVisibility()==View.VISIBLE) {
                jsonObject.put("type", questions.get(position).getType());
                jsonObject.put("answer", answers[position]);
            }
            else
            {
                jsonObject.put("type", questions.get(position).getType());
                jsonObject.put("answer", answers[position]);
            }
            jsonObject.put("question", questionText1.getText().toString());
            jsonArray.put(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void mtoFunc(final int position)
    {
        setNextPrevButton(position);
        questionText2.setText(questions.get(position).getQuestion());
        cbText.setText(questions.get(position).getOpt_A());
        cbText2.setText(questions.get(position).getOpt_B());
        cbText3.setText(questions.get(position).getOpt_C());
        cbText4.setText(questions.get(position).getOpt_D());

        cb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb1.isChecked())
                {
                    cb1.setChecked(true);
                    answers[position]+="A";
                }
                else
                {
                    cb1.setChecked(false);
                }
            }
        });

        cb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb2.isChecked())
                {
                    cb2.setChecked(true);
                    answers[position]+="B";
                }
                else
                {
                    cb2.setChecked(false);
                }

            }
        });

        cb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb3.isChecked())
                {
                    cb3.setChecked(true);
                    answers[position]+="C";
                }
                else
                {
                    cb3.setChecked(false);
                }

            }
        });

        cb4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb4.isChecked())
                {
                    cb4.setChecked(true);
                    answers[position]+="D";
                }
                else
                {
                    cb4.setChecked(false);
                }

            }
        });


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opt_A", cbText.getText().toString());
            jsonObject.put("opt_B", cbText2.getText().toString());
            jsonObject.put("opt_C", cbText3.getText().toString());
            jsonObject.put("opt_D", cbText4.getText().toString());
            jsonObject.put("reason", questions.get(position).getReason());
            if(resText2.getVisibility()==View.VISIBLE) {
                jsonObject.put("type", questions.get(position).getType());
                jsonObject.put("answer", answers[position]);
            }
            else
            {
                jsonObject.put("type", questions.get(position).getType());
                jsonObject.put("answer", answers[position]);
            }
            jsonObject.put("question", questionText2.getText().toString());
            jsonArray.put(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
    void showPopUp(){
        if (jsonArray.length() != 0) {
            AlertDialog.Builder builder=new AlertDialog.Builder(AttemptQuiz.this);
            builder.setMessage("Do you want to submit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final JSONObject tempObject = new JSONObject();

                    try {
                        tempObject.put("Questions", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String jsonStr = tempObject.toString();
                    Map<String, Object> result = new Gson().fromJson(jsonStr, Map.class);

                    submit();
                    dialogStart();


                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.show();
        }

    }

    /*submit result to database**/
    void submit(){
        flag_controller = 0;
        int score=0;
        list = new ArrayList<>();
        arrayList = new ArrayList<>();
        for(int i=0;i<answers.length;i++){
            if(answers[i]!=null&&answers[i].equals(questions.get(i).getAnswer())){
                score++;
            }
            String temp = (answers[i]!=null) ? answers[i]+") ":"null) ";

            list.add("Your choice ("+
                    temp +
                    "Right choice is("+ questions.get(i).getAnswer()+")");
            arrayList.add(questions.get(i).getQuestion());
        }
        Map<String,Integer> g = new HashMap<>();
        g.put("score",score);
        try {
            db.collection("Test").document(group_id).collection("tests").document(test_id).collection("results")
                    .document(my_id).set(g, SetOptions.merge());

        }catch (Exception e){
            Log.e("qwert" ,e.getMessage());
        }
    }

    void dialogStart() {

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(AttemptQuiz.this);
        builderSingle.setIcon(R.mipmap.ic_launcher_round);
        builderSingle.setTitle(TESTNAME+" Answers");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (AttemptQuiz.this, android.R.layout.select_dialog_singlechoice);
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>
                (AttemptQuiz.this,android.R.layout.select_dialog_singlechoice);

        for(String y : arrayList) {
            arrayAdapter1.add(y);
        }
        for(String x: list){
            arrayAdapter.add(x);
        }

        builderSingle.setCancelable(false);
        builderSingle.setNegativeButton("Done!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(AttemptQuiz.this);
                builderInner.setMessage(strName);
                builderInner.setCancelable(false);
                builderInner.setTitle("Your Selected Question Answer is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
//                        finish();
                        builderSingle.show();
//                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();

    }


    void setNextPrevButton(int pos){
        if(pos==0){
//            prev.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            prev.setText("");
            prev.setVisibility(View.INVISIBLE);
        }else {
//            prev.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            prev.setText("Previous");
            prev.setVisibility(View.VISIBLE);
        }
        if(pos==questions.size()-1){
            next.setText("Submit");
//            next.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }else {
            next.setText("Next");
//            next.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onBackPressed() {
        showPopUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.attempt_menu, menu);
        final MenuItem  counter = menu.findItem(R.id.counter);

        new CountDownTimer( timer*60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                long hr=TimeUnit.MILLISECONDS.toHours(millis),mn=(TimeUnit.MILLISECONDS.toMinutes(millis)-
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                        sc=TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));


                String  hms =format(hr)+":"+format(mn)+":"+format(sc) ;
                counter.setTitle(hms);
                timer = millis;
            }
            String format(long n){
                if(n<10)
                    return "0"+n;
                else return ""+n;
            }

            public void onFinish() {
                submit();
                dialogStart();
            }
        }.start();

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.submit){
            showPopUp();

            return true;
        }
        return super.onOptionsItemSelected(item);
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