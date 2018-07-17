package com.act.videochat.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.videochat.OnScrollShowHideListner;
import com.act.videochat.R;
import com.act.videochat.adapter.FragmentViewPagerAdapter;
import com.act.videochat.fragment.DuplicateChatFragment;
import com.act.videochat.fragment.DuplicateVideoFragment;
import com.act.videochat.view.FragmentDialog;

import java.util.ArrayList;
import java.util.List;

public class TabMainActivity extends AppCompatActivity implements View.OnClickListener, OnScrollShowHideListner {

    LinearLayout ly_1, ly_2;
    ViewPager view_pager;
    List<Fragment> fragments = null;
    ImageView cursor;
    int bmpW;// 动画图片宽度
    int offset = 0;// 动画图片偏移量
    int currIndex = 0;// 当前页卡编号
    TextView makemoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);
        findViewById();
    }


    void findViewById() {
        ly_1 = (LinearLayout) findViewById(R.id.ly_1);
        ly_2 = (LinearLayout) findViewById(R.id.ly_2);
        makemoney = (TextView) findViewById(R.id.makemoney);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        InitImageView();
        DuplicateVideoFragment tab1Fragment = DuplicateVideoFragment.newInstance();
        DuplicateChatFragment tab2Fragment = DuplicateChatFragment.newInstance();
        fragments = new ArrayList<>();
        fragments.add(tab1Fragment);
        fragments.add(tab2Fragment);
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), view_pager, fragments);
        view_pager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        view_pager.setCurrentItem(0);
        view_pager.setOffscreenPageLimit(2);
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

            @Override
            public void onPageSelected(int arg0) {
                Animation animation = new TranslateAnimation(one * currIndex, one * arg0, 0, 0);//显然这个比较简洁，只有一行代码。
                currIndex = arg0;
                animation.setFillAfter(true);// True:图片停在动画结束位置
                animation.setDuration(300);
                cursor.startAnimation(animation);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        ly_1.setOnClickListener(this);
        ly_2.setOnClickListener(this);
        makemoney.setOnClickListener(this);
    }

    void InitImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.icon_pull_up).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_1:
                view_pager.setCurrentItem(0);
                break;
            case R.id.ly_2:
                view_pager.setCurrentItem(1);
                break;
            case R.id.makemoney:
                if (view_pager.getCurrentItem() == 0) {
                    startActivity(new Intent(this, VideoFollowActivity.class));
                } else {
                    startActivity(new Intent(this, ChatFollowActivity.class));

                }
                break;
        }
    }


    @Override
    public void onScrollShow() {
        showFABAnimation(makemoney);
    }

    @Override
    public void onScrollHide() {
        hideFABAnimation(makemoney);
    }

    public void showFABAnimation(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(400).start();
    }

    public void hideFABAnimation(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 0f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 0f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 0f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(400).start();
    }

    @Override
    public void onBackPressed() {


        FragmentDialog.newInstance(false, "客官再看一会儿呗", "还是留下来再看看吧", "再欣赏下", "有事要忙", "", "", false, new FragmentDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();

            }

            @Override
            public void onNegtiveClick(final Dialog dialog) {
                dialog.dismiss();
                TabMainActivity.super.onBackPressed();
            }
        }).show(getSupportFragmentManager(), "");


    }
}
