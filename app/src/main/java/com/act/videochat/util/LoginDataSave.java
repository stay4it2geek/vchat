package com.act.videochat.util;


import android.content.Context;
import android.content.SharedPreferences;

public class LoginDataSave {

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginEditor;


    public LoginDataSave(Context mContext) {
        loginPreferences = mContext.getSharedPreferences("loginData", Context.MODE_PRIVATE);
        loginEditor = loginPreferences.edit();
    }


    public void clearLoginData() {
        loginEditor.clear();
        loginEditor.commit();

    }


    public void setLoginData(String isLogin, String phone, String password) {
        loginEditor.putString("isLogin", isLogin);
        loginEditor.putString("phone", phone);
        loginEditor.putString("password", password);
        loginEditor.commit();
    }

    public String getLoginPhone() {
        return loginPreferences.getString("phone", "");
    }

    public String getLoginPassWord() {
        return loginPreferences.getString("password", "");
    }

    public String getLoginData() {
        return loginPreferences.getString("isLogin", "");
    }
}