package com.act.videochat.util;

import android.content.Context;
import android.widget.EditText;

import com.act.videochat.OnCheckInputListner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VerifyUitl {

    Context context;
    OnCheckInputListner listner;

    public VerifyUitl(Context context, OnCheckInputListner listner) {
        this.context = context;
        this.listner = listner;
    }

    public boolean verifyInputTrue(EditText et_userPhonenumber,
                                   EditText et_password) {


        if (et_userPhonenumber != null) {
            if (et_userPhonenumber.getText().toString().equals("手机号") ||
                    et_userPhonenumber.getText().toString().equals("") ||
                    et_userPhonenumber.getText().length() > 11) {
                ToastUtil.showToast(context, "请输入11位数的手机号码");
                listner.onCheckInputLisner(et_userPhonenumber);
                return false;
            } else {
                String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))||(17[4|7])|(18[0,5-9]))\\d{8}$";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(et_userPhonenumber.getText().toString());
                if (!m.find()) {
                    ToastUtil.showToast(context, "请输入国内通用的手机号码");
                    listner.onCheckInputLisner(et_userPhonenumber);
                    return false;
                }
            }
        }


        if (et_password != null) {
            if (et_password.getText().toString().equals("密码") || et_password.getText().toString().equals("")) {
                ToastUtil.showToast(context, "请输入密码");
                listner.onCheckInputLisner(et_password);
                return false;
            }
        }


        return true;
    }


}
