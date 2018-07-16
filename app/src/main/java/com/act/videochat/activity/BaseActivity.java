package com.act.videochat.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {
    public static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        setContentView(setLayoutId());
        ButterKnife.bind(this);
        initView(savedInstanceState);
        initData();
    }

    public void beforeSetContentView() {
    }

    public abstract int setLayoutId();

    public abstract void initView(Bundle savedInstanceState);

    public abstract void initData();
}
