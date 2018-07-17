package com.act.videochat.bean;

import java.util.ArrayList;

public class ChatGirlInfoComment {
    public boolean result;
    public int maxPage;
    public ArrayList<CommentTagList> data;

    public class CommentTagList {
        public String nickname;
        public Avatar avatar;
        public class Avatar {
            public String id;
            public String width;
            public String height;
            public String sn;
            public String url;
        }
        public ArrayList<Taglist> taglist;
        public class Taglist {
            public String name;
            public String color;
        }
    }

}