package com.act.videochat.bean;

import java.io.Serializable;

/**
 * 短视频独立播放的视频资源
 */
public class SmallPlayVideoInfoModel implements Serializable{
    public boolean result;
    public VideoInfoData data;
    public int isFocus;

    public class VideoInfoData implements Serializable{
        public String id;
        public String vid;
        public String shareNum;
        public String url;//http://cdn.pairui8.com/prod/video/jrPWkCJAtp.mp4",
        public Avatar avatar;
        public class Avatar implements Serializable{
            public String id;
            public int width;
            public int height;
            public int sn;
            public String url;//http://cdn.pairui8.com/prod/image/NyHWzRdF3r.jpg"
        }
    }
}
