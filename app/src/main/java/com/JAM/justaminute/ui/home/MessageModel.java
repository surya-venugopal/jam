package com.JAM.justaminute.ui.home;

public class MessageModel {
    public String message;
    public int type;
    public String res;
    private int timestamp;
    private String email;
    private String name;
    private String id;


    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;
    public static final int AUDIO_TYPE_IN=3;
    public static final int AUDIO_TYPE_OUT=4;
    public static final int IMAGE_TYPE_IN = 5;
    public static final int IMAGE_TYPE_OUT = 6;
    public static final int VIDEO_TYPE_IN=7;
    public static final int VIDEO_TYPE_OUT=8;
    public static final int DOCUMENT_TYPE_IN=9;
    public static final int DOCUMENT_TYPE_OUT=10;

    public MessageModel( String message, int type, String res, String email, String name, String id) {
        this.message = message;
        this.type = type;
        this.res = res;
        this.timestamp = (int) (System.currentTimeMillis()/ 1000L);
        this.email = email;
        this.name = name;
        this.id = id;
    }
    public MessageModel(){

    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public String getRes() {
        return res;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public static int getMessageTypeIn() {
        return MESSAGE_TYPE_IN;
    }

    public static int getMessageTypeOut() {
        return MESSAGE_TYPE_OUT;
    }

    public static int getAudioTypeIn() {
        return AUDIO_TYPE_IN;
    }

    public static int getAudioTypeOut() {
        return AUDIO_TYPE_OUT;
    }

    public static int getImageTypeIn() {
        return IMAGE_TYPE_IN;
    }

    public static int getImageTypeOut() {
        return IMAGE_TYPE_OUT;
    }

    public static int getVideoTypeIn() {
        return VIDEO_TYPE_IN;
    }

    public static int getVideoTypeOut() {
        return VIDEO_TYPE_OUT;
    }
}
