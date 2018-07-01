package com.act.videochat.bean;


import java.util.ArrayList;

public class ChatGirlInfoDetail {

    public boolean result;
    public GirlDetailInfo data;

    public class GirlDetailInfo {

        public String id;//: 875739,
        public String topic;//你快乐所以我快乐",
        public String weight;// 48,
        public String height;// 168,
        public String constellation;//秤座",
        public String country;//"中国",
        public String province;//重庆",
        public String city;//重庆",
        public String connectRate;// 86,
        public String lastTime;//2天之前",
        public ArrayList<FigureTags> figureTags;


        public class FigureTags {
            public String id;// 7,
            public String type;//1,
            public String name;//清纯美女",
            public String color;//57bdfe",
            public String cdate;//2017-09-07 13:54:33",
            public String isuse;//1,
            public String sn;//2,
            public String is_love;// 1,
            public String meiliao_color;//ff2f79"
        }


        public ArrayList<CommentTags> commentTags;

        public class CommentTags {
            public String id;
            public String name;
            public String color;
        }

        public String like;
        public String dislike;

    }

}
