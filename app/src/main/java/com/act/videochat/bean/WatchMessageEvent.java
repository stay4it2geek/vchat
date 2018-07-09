package com.act.videochat.bean;


import java.util.ArrayList;

public class WatchMessageEvent {
    private ArrayList<String> message;

    public WatchMessageEvent(ArrayList<String> message) {
        this.message = message;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<String> message) {
        this.message = message;
    }

}
