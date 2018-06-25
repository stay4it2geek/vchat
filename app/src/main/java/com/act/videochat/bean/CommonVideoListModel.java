package com.act.videochat.bean;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * 短视频列表通用模型
 */
public class CommonVideoListModel implements Serializable{
    public boolean result;
    public int currPage;
    public int maxCount;
    public int maxPage;
    public ArrayList<HomeVideoInfoData> data;

    public class HomeVideoInfoData  implements Serializable{
        public String cover;//封面
        public String id;//videoId
        }

}
