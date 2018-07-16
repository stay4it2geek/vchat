package com.act.videochat.bean;


import java.io.Serializable;
import java.util.ArrayList;

public class ChatGirlVideoList implements Serializable{


    public boolean result;
    public int maxPage;
    public int maxCount;
    public int currPage;
    public ArrayList<GirlVideoInfo> data;


    public class GirlVideoInfo implements Serializable{

        public String id;
        public String posterId;
        public String nickname;
        public Avatar avatar;


        public class Avatar implements Serializable{
            public String id;
            public String width;
            public String height;
            public String sn;
            public String url;
        }

        public String isPay;
        public String title;
        public String postTime;
        public String follow;
        public Cover cover;

        public class Cover implements Serializable{
            public String id;
            public String width;
            public String height;
            public String url;
        }

        public Frame frame;

        public class Frame implements Serializable{
            public String id;
            public String width;
            public String height;
            public String url;
        }

        public String states;
        public String seeNum;
        public String shareNum;
        public String income;
        public String vcoin;
        public String online;
        public String payNum;
        public String hasPaid;

    }
}
