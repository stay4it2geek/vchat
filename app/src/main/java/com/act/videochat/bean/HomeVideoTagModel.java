package com.act.videochat.bean;

import java.util.ArrayList;

/**
 *
 */

public class HomeVideoTagModel {

    public boolean result;
    public ArrayList<TagInfoData> data;

    public class TagInfoData {
        public int id;
        public String name;
    }
}
