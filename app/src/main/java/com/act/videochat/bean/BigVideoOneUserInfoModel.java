package com.act.videochat.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 短视频作者个人信息列表
 */
public class BigVideoOneUserInfoModel implements Serializable {
    public boolean result;
    public int isFocus;
    public GirlInfoData data;

    public class GirlInfoData implements Serializable {
        public String id;
        public String nickname;
        public String isFocus;
        public Avatar avatar;
        public String topic;
        public ArrayList<Tag> tags;
    }
    public class Avatar implements Serializable {
        public String id;
        public int width;
        public int height;
        public int sn;
        public String url;
    }

    public class Tag implements Serializable {
        public String id;
        public String type;
        public String name;
        public String color;
        public String cdate;
        public String isuse;
        public String sn ;
        public String is_love;
        public String category;
    }
}
