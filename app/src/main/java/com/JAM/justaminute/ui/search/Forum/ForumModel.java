package com.JAM.justaminute.ui.search.Forum;

public class ForumModel {
    String topic;
    String description;
    String comment1;
    int time;
    String cimg1;
    String tag;
    String id;

    public ForumModel(){}

    public ForumModel(String topic, String description, String comment1, int time, String cimg1,String tag,String id) {
        this.topic = topic;
        this.description = description;
        this.comment1 = comment1;
        this.time = time;
        this.cimg1 = cimg1;
        this.tag = tag;
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public String getDescription() {
        return description;
    }

    public String getComment1() {
        return comment1;
    }

    public int getTime() {
        return time;
    }

    public String getCimg1() {
        return cimg1;
    }

    public String getTag() {
        return tag;
    }

    public String getId() {
        return id;
    }
}
