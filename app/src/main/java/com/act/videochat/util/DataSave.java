package com.act.videochat.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

public class DataSave {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private SharedPreferences vipPreferences;
    private SharedPreferences.Editor vipEditor;
    public DataSave(Context mContext, String preferenceName) {
        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    public DataSave(Context mContext) {
        vipPreferences = mContext.getSharedPreferences("vipData", Context.MODE_PRIVATE);
        vipEditor = vipPreferences.edit();
    }


    public void setDataList(List<String> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        StringBuilder sb = new StringBuilder();
        for (String str : datalist) {
            sb.append(str).append(",");
        }
        editor.clear();
        editor.putString("videoList", sb.toString());
        editor.putLong("time",  System.currentTimeMillis());
        editor.commit();

    }


    public void clearDataList() {
        editor.clear();
        editor.commit();

    }

    public List<String> getDataList() {
        List<String> datalist = new ArrayList<>();
        String str = preferences.getString("videoList", null);
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

        return preferences.getLong("time", 0);

    }


    public void setVipData(String isVip) {
        vipEditor.putString("isVip", isVip);
        vipEditor.commit();

    }


    public String getVipData() {
        return vipPreferences.getString("isVip", null);

    }
}