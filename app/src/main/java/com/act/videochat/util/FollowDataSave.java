package com.act.videochat.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.act.videochat.bean.BigVideoOneUserInfoModel;
import com.act.videochat.bean.CommonChatListModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class FollowDataSave {


    private SharedPreferences chatPreferences;
    private SharedPreferences.Editor chatEditor;

    public FollowDataSave(Context mContext, String preferenceName) {
        chatPreferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        chatEditor = chatPreferences.edit();
    }

    public  void setDataList(String tag, List datalist) {
        if (null == datalist || datalist.size() <= 0) {
            chatEditor.clear();
            chatEditor.commit();
            return;
        }
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        chatEditor.clear();
        chatEditor.putString(tag, strJson);
        chatEditor.commit();

    }


    public List<CommonChatListModel.HomeChatInfoData> getChatGirlDataList(String tag) {
        List<CommonChatListModel.HomeChatInfoData> datalist = new ArrayList();
        String strJson = chatPreferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<CommonChatListModel.HomeChatInfoData>>() {
        }.getType());
        return datalist;

    }

    public List<BigVideoOneUserInfoModel> getVideoGirlDataList(String tag) {
        List<BigVideoOneUserInfoModel> datalist = new ArrayList();
        String strJson = chatPreferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<BigVideoOneUserInfoModel>>() {
        }.getType());
        return datalist;

    }

    public void clear() {
        chatEditor.clear();
        chatEditor.commit();
    }

}