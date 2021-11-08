package com.JAM.justaminute.ui.Test.Model;
import java.io.Serializable;

public class Question implements Serializable {

    private int id;
    private String question;
    private String opt_A;
    private String opt_B;
    private String opt_C;
    private String opt_D;
    private String reason;
    private int answer[];
    private int type;

    public Question() {

    }
    //constructer for setting values


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int[] getAnswer() {
        return answer;
    }

    public void setAnswer(int answer[]) {
        this.answer = answer;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Question(int id, String question, String opt_A, String opt_B, String opt_C, String opt_D, String reason , int answer[], int type) {
        this.id = id;
        this.question = question;
        this.opt_A = opt_A;
        this.opt_B = opt_B;
        this.opt_C = opt_C;
        this.opt_D = opt_D;
        this.reason=reason;
        this.answer = answer;
        this.type=type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOpt_A() {
        return opt_A;
    }

    public void setOpt_A(String opt_A) {
        this.opt_A = opt_A;
    }

    public String getOpt_B() {
        return opt_B;
    }

    public void setOpt_B(String opt_B) {
        this.opt_B = opt_B;
    }

    public String getOpt_C() {
        return opt_C;
    }

    public void setOpt_C(String opt_C) {
        this.opt_C = opt_C;
    }

    public String getOpt_D() {
        return opt_D;
    }

    public void setOpt_D(String opt_D) {
        this.opt_D = opt_D;
    }


}