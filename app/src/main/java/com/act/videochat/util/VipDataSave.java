package com.act.videochat.util;


import android.content.Context;
import android.content.SharedPreferences;

public class VipDataSave {

    private SharedPreferences vipPreferences;
    private SharedPreferences.Editor vipEditor;

    public VipDataSave(Context mContext) {
        vipPreferences = mContext.getSharedPreferences("vipData", Context.MODE_PRIVATE);
        vipEditor = vipPreferences.edit();
    }


    public void setVipData(String isVip) {
        vipEditor.putString("isVip", isVip);
        vipEditor.commit();
    }

    public String getVipData() {
        return vipPreferences.getString("isVip", null);

    }
}