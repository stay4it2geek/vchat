package com.act.videochat.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.act.videochat.R;
import com.act.videochat.util.LoginDataSave;
import com.act.videochat.util.TCUtils;
import com.act.videochat.view.FragmentDialog;
import com.act.videochat.view.SelfDialog;

import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

public class WelcomeActivity extends ActivityManagePermission {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        final ImageView login_start_bg = (ImageView) findViewById(R.id.login_start_bg);
        final TranslateAnimation taRight = new TranslateAnimation(250, 0, 0, 0);
        final TranslateAnimation taLeft = new TranslateAnimation(0, 250, 0, 0);
        taRight.setDuration(4000);
        taLeft.setDuration(4000);

        login_start_bg.startAnimation(taRight);
        taLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                login_start_bg.startAnimation(taRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        taRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                login_start_bg.startAnimation(taLeft);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void toMain(View view) {
        grantPermission(TabMainActivity.class);
    }


    public void loginphone(View view) {
        grantPermission(LoginActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoginDataSave dataSave = new LoginDataSave(this);
        if (!TCUtils.isNetworkAvailable(this)) {
            findViewById(R.id.phonelogin).setVisibility(View.GONE);
            findViewById(R.id.logintip).setVisibility(View.GONE);
            FragmentDialog.newInstance(false, "网络有问题", "请先打开网络", "确定", "", "", "", true, new FragmentDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick(Dialog dialog) {
                    Intent intent=null;
                    //判断手机系统的版本  即API大于10 就是3.0或以上版本
                    if(android.os.Build.VERSION.SDK_INT>10){
                        intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    }else{
                        intent = new Intent();
                        ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                        intent.setComponent(component);
                        intent.setAction("android.intent.action.VIEW");
                    }
                    startActivity(intent);
                    dialog.dismiss();
                }

                @Override
                public void onNegtiveClick(Dialog dialog) {
                        dialog.dismiss();
                }
            });
        } else {
            if ("isLogin".equals(dataSave.getLoginData())) {
                findViewById(R.id.phonelogin).setVisibility(View.GONE);
                findViewById(R.id.logintip).setVisibility(View.GONE);
            }
            if (dataSave.getLoginData() == ""||dataSave.getLoginData() == null) {
                findViewById(R.id.phonelogin).setVisibility(View.VISIBLE);
                findViewById(R.id.logintip).setVisibility(View.VISIBLE);
            }
            findViewById(R.id.tomain).setVisibility(View.VISIBLE);
        }


    }


    void grantPermission(final Class cls) {

        final SelfDialog selfDialog = new SelfDialog(WelcomeActivity.this, false);
        askCompactPermissions(new String[]{PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
            @Override
            public void permissionGranted() {
                startActivity(new Intent(WelcomeActivity.this, cls));

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }

            @Override
            public void permissionDenied() {
                selfDialog.setTitle("同意权限后才能正常使用哦");
                selfDialog.setMessage("您需要同意读写存储权限");
                selfDialog.setYesOnclickListener("立即同意", new SelfDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        grantPermission(cls);
                        selfDialog.dismiss();
                    }
                });
                selfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        finish();
                    }
                });
                selfDialog.show();
            }

            @Override
            public void permissionForeverDenied() {
                selfDialog.setTitle("同意权限后才能正常使用哦");
                selfDialog.setMessage("您需要同意读写存储权限");
                selfDialog.setYesOnclickListener("立即同意", new SelfDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        grantPermission(cls);
                        selfDialog.dismiss();
                    }
                });
                selfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        selfDialog.dismiss();
                        finish();
                    }
                });
                selfDialog.show();
            }
        });
    }
}
