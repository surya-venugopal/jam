package com.JAM.justaminute.ui;


public class User {

    private String username;
    private String email;
    private String phone;
    private String college;
    private String age;
    private String dp_uri;
    private String roll;

    public User(){

    }

    public User(String username, String email ,String phone,String college,String age,String dp_uri,String roll) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.college = college;
        this.dp_uri = dp_uri;
        this.roll = roll;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCollege() {
        return college;
    }

    public String getAge() {
        return age;
    }


    public String getDp_uri() {
        return dp_uri;
    }

    public String getRoll() {
        return roll;
    }
}