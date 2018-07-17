package com.act.videochat.bean;


import java.util.ArrayList;

public class ChatGirlInfoDetail {

    public boolean result;
    public GirlDetailInfo data;
    public class GirlDetailInfo {
        public String id;
        public String topic;
        public String weight;
        public String height;
        public String constellation;
        public String country;
        public String province;
        public String city;
        public String connectRate;
        public String lastTime;
        public ArrayList<FigureTags> figureTags;
        public class FigureTags {
            public String id;
            public String type;
            public String name;
            public String color;
            public String cdate;
            public String isuse;
            public String sn;
            public String is_love;
            public String meiliao_color;
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
