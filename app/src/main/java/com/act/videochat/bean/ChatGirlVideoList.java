package com.act.videochat.bean;


import java.util.ArrayList;

public class ChatGirlVideoList {


    public boolean result;
    public String maxPage;// 1,
    public String maxCount;// 3,
    public String currPage;// "1",
    public ArrayList<GirlVideoInfo> data;


    public class GirlVideoInfo {

        public String id;// 15204,
        public String posterId;// 875739,
        public String nickname;// "菲力Lilly",
        public Avatar avatar;


        public class Avatar {
            public String id;// 1890557,
            public String width;// 750,
            public String height;// 750,
            public String sn;// 0,
            public String url;// "http://cdn.vliao1.xyz/prod/image/pdbp5SChw6.jpg"
        }

        public String isPay;// 0,
        public String title;// "你好啊来聊天",
        public String postTime;// "18天之前",
        public String follow;// 11,
        public Cover cover;

        public class Cover {
            public String id;// 27490,
            public String width;// 540,
            public String height;// 960,
            public String url;// "http://cdn.vliao1.xyz/prod/image/875739_1528964641000_q5fda4nc4iklpu9uduhe"
        }

        public Frame frame;

        public class Frame {
            public String id;// 27491,
            public String width;// 540,
            public String height;// 960,
            public String url;// "http://cdn.vliao1.xyz/prod/image/875739_1528964642000_4jqws3rpcoyzo4bgrm39"
        }

        public String states;// 1,
        public String seeNum;// 608,
        public String shareNum;// 1,
        public String income;// 0,
        public String vcoin;// 0,
        public String online;// "4",
        public String payNum;// 0,
        public String hasPaid;// 0

    }
}
