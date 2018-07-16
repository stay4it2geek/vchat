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

public class RegsiterActivity extends AppCompatActivity {
    EditText et_userPhonenumber;
    EditText et_userpassword;

    private Animation shakeAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        et_userPhonenumber = (EditText) findViewById(R.id.et_userPhonenumber);
        et_userpassword = (EditText) findViewById(R.id.et_userpassword);


    }

    public void phoneregister(View view) {
        VerifyUitl uitl = new VerifyUitl(this, new OnCheckInputListner() {
            @Override
            public void onCheckInputLisner(EditText editText) {
                editText.startAnimation(shakeAnimation);
            }
        });
        if (uitl.verifyInputTrue(et_userPhonenumber, et_userpassword)) {

            String[] infos = (new FileUtils().readInfo(Constants.USER_INFO) + "").split("##");
            String phone = "";
            if (infos.length > 1) {
                phone = infos[0];
            }

            if (et_userPhonenumber.getText().toString().equals(phone)) {
                ToastUtil.showToast(this, "该用户已存在");
            } else {
                LoginDataSave dataSave = new LoginDataSave(this);
                dataSave.setLoginData("isLogin", et_userPhonenumber.getText().toString(), et_userpassword.getText().toString());
                FileUtils fileUtils = new FileUtils();
                if (fileUtils.isExternalStorageReadable() && fileUtils.isExternalStorageWritable()) {
                    if (!fileUtils.isFileExist(Constants.USER_INFO)) {
                        fileUtils.write2SDFromInput(Constants.USER_INFO, et_userPhonenumber.getText().toString() + "##" + et_userpassword.getText().toString());
                        startActivity(new Intent(RegsiterActivity.this, TabMainActivity.class));
                        this.finish();
                        ToastUtil.showToast(this, "注册成功");
                    }
                }
            }


        }
    }

    public void back(View view) {
        this.finish();
    }


    public void phonelogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
