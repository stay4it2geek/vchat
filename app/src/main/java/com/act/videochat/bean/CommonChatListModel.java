package com.act.videochat.bean;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * 聊天列表通用模型
 */
public class CommonChatListModel implements Serializable {
    public boolean result;
    public int currPage;
    public int maxPage;
    public ArrayList<HomeChatInfoData> data;

    public class HomeChatInfoData implements Serializable {

        public String id;
        public String nickname;
        public String topic;
        public String vcoinPerMinute;
        public String level;
        public String online;
        public Avatar avatar;


        public class Avatar implements Serializable {
            public String id;
            public int width;
            public int height;
            public int sn;
            public String url;
        }

    }
}