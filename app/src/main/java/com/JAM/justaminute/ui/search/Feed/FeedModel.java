package com.JAM.justaminute.ui.search.Feed;

public class FeedModel {
    String personName;
    String person_id;
    String person_roll;
    String feedImage;
    String feedTopic;
    String feedDes;
    int date;
    String id;

    public FeedModel(){

    }

    public FeedModel(String personName, String person_id,String person_roll, String feedImage, String feedTopic, String feedDes, int date,String id) {
        this.personName = personName;

        this.person_id = person_id;
        this.person_roll = person_roll;
        this.feedImage = feedImage;
        this.feedTopic = feedTopic;
        this.feedDes = feedDes;
        this.date = date;
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }



    public String getPerson_id() {
        return person_id;
    }

    public String getPerson_roll() {
        return person_roll;
    }

    public String getFeedImage() {
        return feedImage;
    }

    public String getFeedTopic() {
        return feedTopic;
    }

    public String getFeedDes() {
        return feedDes;
    }

    public int getDate() {
        return date;
    }

    public String getId() {
        return id;
    }
}
