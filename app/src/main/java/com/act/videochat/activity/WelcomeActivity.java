package com.act.videochat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.act.videochat.R;
import com.act.videochat.util.WatchAndVipDataSave;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        final ImageView login_start_bg = (ImageView) findViewById(R.id.login_start_bg);
        final TranslateAnimation taRight = new TranslateAnimation(250, 0, 0, 0);
        final TranslateAnimation taLeft = new TranslateAnimation(0, 250, 0, 0);
        taRight.setDuration(4000);
        taLeft.setDuration(4000);
       WatchAndVipDataSave dataSave=new WatchAndVipDataSave(this);
        dataSave.setVipData("isVip");
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
    public void toMain(View view){
        startActivity(new Intent(this,TabMainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
