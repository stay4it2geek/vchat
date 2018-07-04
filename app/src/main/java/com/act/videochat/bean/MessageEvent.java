package com.act.videochat.bean;


import java.util.ArrayList;

public class MessageEvent {
    private ArrayList<String> message;

    public MessageEvent(ArrayList<String> message) {
        this.message = message;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<String> message) {
        this.message = message;
    }

}
