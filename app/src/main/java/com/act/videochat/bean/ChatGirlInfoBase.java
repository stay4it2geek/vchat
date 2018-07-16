package com.act.videochat.bean;


import java.util.ArrayList;

public class ChatGirlInfoBase {

    public boolean result;
    public InfoData data;

    public class InfoData {

        public String id;
        public String nickname;
        public String identity;
        public String vcoinPerMinute;
        public int level;
        public String fans;
        public String focused;
        public Avatar avatar;
        public ArrayList<Photos> photos;
        public ArrayList<FigureTags> figureTags;
        public ArrayList<Intimacy> intimacy;
    }

    public class Avatar {

        public String id;
        public String width;
        public String height;
        public String sn;
        public String url;
    }





    public class Photos {
        public String id;//1867238,
        public String width;//720,
        public String height;//720,
        public String sn;//0,
        public String url;//public String  http://cdn.vliao1.xyz/prod/image/400137_1524989036000_qas2hcjvvbi98uodls4n
    }



    public class FigureTags {
        public String id;//7,
        public String type;//1,
        public String name;//public String  清纯美女
        public String color;//public String  57bdfe
        public String cdate;//public String  2017-09-07 13:54:33
        public String isuse;//1,
        public String sn;//2,
        public String is_love;//1,
        public String meiliao_color;//public String  ff2f79
    }



    public class Intimacy {

        public Avatar avatar;

        public class Avatar {
            public String id;//1845825,
            public String width;//720,
            public String height;//720,
            public String sn;//1,
            public String url;//public String  http://cdn.vliao1.xyz/prod/image/581131_1521119228000_shj4ojpmwm2iq7or0khgpublic
        }

        public String nickname;//public String  Jupiterpublic String  ,
        public String contribution;//11728,
        public String userId;//581131,
        public String rank;//1,
        public String level;//8
    }



}
