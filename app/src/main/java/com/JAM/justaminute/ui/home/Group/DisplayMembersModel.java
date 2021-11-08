package com.JAM.justaminute.ui.home.Group;

import java.io.Serializable;

public class DisplayMembersModel implements Serializable {
    String personName;
    String personImage;
    String person_id;
    String roll;

    public DisplayMembersModel(String personName, String personImage, String person_id,String roll) {
        this.personName = personName;
        this.personImage = personImage;
        this.person_id = person_id;
        this.roll = roll;
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

    public String getRoll() {
        return roll;
    }
}
