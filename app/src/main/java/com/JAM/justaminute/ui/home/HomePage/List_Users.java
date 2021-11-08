package com.JAM.justaminute.ui.home.HomePage;

public class List_Users {
    String personName;
    String personImage;
    String person_id;
    String last_text;
    String last_time;
    int type;
    public List_Users(String personName, String personImage ,String person_id,String last_text,String last_time,int type) {
        this.personName = personName;
        this.personImage = personImage;
        this.person_id = person_id;
        this.last_text = last_text;
        this.last_time = last_time;
        this.type = type;
    }

    public String getPersonName() {
        return personName;
    }

    public String getPersonImage() {
        return personImage;
    }

    public String getPerson_id() {
        return person_id;
    }

    public String getLast_text() {
        return last_text;
    }

    public String getLast_time() {
        return last_time;
    }

    public int getType() {
        return type;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public void setLast_text(String last_text) {
        this.last_text = last_text;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }
}
