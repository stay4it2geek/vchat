package com.act.videochat.util;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class WatchDataSave {

    private SharedPreferences watchCountPreferences;
    private SharedPreferences.Editor watchCountEditor;

    public WatchDataSave(Context mContext, String preferenceName) {
        watchCountPreferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        watchCountEditor = watchCountPreferences.edit();
    }


    public void setWatchDataList(List<String> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        StringBuilder sb = new StringBuilder();
        for (String str : datalist) {
            sb.append(str).append(",");
        }
        watchCountEditor.clear();
        watchCountEditor.putString("videoList", sb.toString());
        watchCountEditor.putLong("time",  System.currentTimeMillis());
        watchCountEditor.commit();

    }


    public void clearWatchCountDataList() {
        watchCountEditor.clear();
        watchCountEditor.commit();

    }

    public List<String> getDataList() {
        List<String> datalist = new ArrayList<>();
        String str = watchCountPreferences.getString("videoList", null);
        if (null == str) {
            return datalist;
        }
        String[] strSplite = str.split(",");
        for (String str_ : strSplite) {
            datalist.add(str_);
        }
        return datalist;

    }

    public Long getTimeData() {

        return watchCountPreferences.getLong("time", 0);

    }


}