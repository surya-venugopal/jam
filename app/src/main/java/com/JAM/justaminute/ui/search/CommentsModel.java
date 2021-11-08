package com.JAM.justaminute.ui.search;

public class CommentsModel {
    String name;
    String id;
    String comment;
    int time;
    String dps;

    public CommentsModel(){}

    public CommentsModel(String name, String id, String comment, int time,String dps) {
        this.name = name;
        this.id = id;
        this.comment = comment;
        this.time = time;
        this.dps = dps;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public int getTime() {
        return time;
    }

    public String getDps() {
        return dps;
    }
}
