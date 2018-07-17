package com.act.videochat.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.act.videochat.Constants;
import com.act.videochat.OnCheckInputListner;
import com.act.videochat.R;
import com.act.videochat.util.FileUtils;
import com.act.videochat.util.LoginDataSave;
import com.act.videochat.util.ToastUtil;
import com.act.videochat.util.VerifyUitl;

public class LoginActivity extends AppCompatActivity {
    EditText et_userPhonenumber;
    EditText et_userpassword;
    Animation shakeAnimation;
    FileUtils fileUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        et_userPhonenumber = (EditText) findViewById(R.id.et_userPhonenumber);
        et_userpassword = (EditText) findViewById(R.id.et_userpassword);
        fileUtils = new FileUtils();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoginDataSave dataSave = new LoginDataSave(this);
        if ("isLogin".equals(dataSave.getLoginData()) || "isLogin".equals(fileUtils.readInfo(Constants.USER_EXIST))) {
            this.finish();
            ToastUtil.showToast(this, "登录成功");
            startActivity(new Intent(this, TabMainActivity.class));
        }
    }

    public void phonelogin(View view) {
        VerifyUitl uitl = new VerifyUitl(this, new OnCheckInputListner() {
            @Override
            public void onCheckInputLisner(EditText editText) {
                editText.startAnimation(shakeAnimation);
            }
        });
        if (uitl.verifyInputTrue(et_userPhonenumber, et_userpassword)) {
            LoginDataSave dataSave = new LoginDataSave(this);
            if (dataSave.getLoginData() != null && "isLogin".equals(dataSave.getLoginData())) {
                this.finish();
                ToastUtil.showToast(this, "登录成功");
            } else {
                String[] infos = (fileUtils.readInfo(Constants.USER_INFO) + "").split("##");
                String loginInfo = "";
                String[] logininfos;
                String phone = "";
                String password = "";
                if (infos.length > 0) {
                    for (int i = 0; i < infos.length; i++) {
                        loginInfo = infos[i];
                        if (loginInfo.contains("&#&") && (loginInfo.contains(et_userPhonenumber.getText().toString()))) {
                            logininfos = loginInfo.split("&#&");
                            if (logininfos.length > 1) {
                                phone = logininfos[0];
                                password = logininfos[1];
                            }
                        }
                    }
                }
                if ((et_userPhonenumber.getText().toString().equals(dataSave.getLoginPhone())
                        && et_userpassword.getText().toString().equals(dataSave.getLoginPassWord()))) {
                    dataSave.setLoginData("isLogin", et_userPhonenumber.getText().toString(), et_userpassword.getText().toString());
                    startActivity(new Intent(LoginActivity.this, TabMainActivity.class));
                    this.finish();
                    ToastUtil.showToast(this, "登录成功");
                } else if (et_userPhonenumber.getText().toString().equals(phone)
                        && et_userpassword.getText().toString().equals(password)) {
                    dataSave.setLoginData("isLogin", et_userPhonenumber.getText().toString(), et_userpassword.getText().toString());
                    startActivity(new Intent(LoginActivity.this, TabMainActivity.class));
                    this.finish();
                    ToastUtil.showToast(this, "登录成功");
                } else if (!et_userPhonenumber.getText().toString().equals(phone)) {
                    ToastUtil.showToast(this, "请检查用户是否正确！");
                } else if (!et_userpassword.getText().toString().equals(password)) {
                    ToastUtil.showToast(this, "请检查密码是否正确！");
                }
            }
        }
    }

    public void back(View view) {
        this.finish();
    }

    public void phoneRegister(View view) {
        startActivity(new Intent(this, RegsiterActivity.class));
    }

}
