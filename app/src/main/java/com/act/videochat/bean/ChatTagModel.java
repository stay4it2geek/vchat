package com.act.videochat.bean;

import java.util.ArrayList;



public class ChatTagModel {

    public boolean result;
    public Integer[] orderByStatus;
    public ArrayList<ChatTagData> data;

    public int scrollTime;
    public ArrayList<AdSlide> aAdSlide;


    public class ChatTagData {
        public int id;
        public String name;
    }

    public class AdSlide {
        public String cover;
        public String url;
        public String type;
        public String title;
    }

}
