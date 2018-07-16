package com.act.videochat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.videochat.Constants;
import com.act.videochat.R;


public class LoadNetView extends LinearLayout {

     LinearLayout nologinlayout;
     Button reloadbutton;
     Button loginbutton;
     LinearLayout loadlayout;
     LinearLayout noDataText;
     LinearLayout reloadlayout;
     Button loadDataButton;

    public LoadNetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.load_net_view, this);
        reloadlayout = (LinearLayout) findViewById(R.id.reloadlayout);
        reloadbutton = (Button) findViewById(R.id.relaodbutton);
        loginbutton = (Button) findViewById(R.id.loginbutton);
        loadDataButton = (Button) findViewById(R.id.loadDataButton);
        noDataText = (LinearLayout) findViewById(R.id.noDataText);
        loadlayout = (LinearLayout) findViewById(R.id.loadlayout);
        nologinlayout = (LinearLayout) findViewById(R.id.nologinlayout);
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;     //截断事件的传递
            }
        });
    }

    public void setReloadButtonListener(OnClickListener listener) {
        reloadbutton.setOnClickListener(listener);
    }

    public void setLoadButtonListener(OnClickListener listener) {
        loadDataButton.setOnClickListener(listener);
    }


    public void setloginButtonListener(OnClickListener listener) {
        loginbutton.setOnClickListener(listener);
    }

    public void setlayoutVisily(int loadType) {
        if (loadType == Constants.LOAD) {
            noDataText.setVisibility(GONE);
            reloadlayout.setVisibility(View.GONE);
            loadlayout.setVisibility(View.VISIBLE);
            nologinlayout.setVisibility(View.GONE);
        } else if (loadType == Constants.RELOAD) {
            reloadlayout.setVisibility(View.VISIBLE);
            loadlayout.setVisibility(View.GONE);
            noDataText.setVisibility(GONE);
            nologinlayout.setVisibility(View.GONE);
        } else if (loadType == Constants.LOGIN) {
            loadlayout.setVisibility(View.GONE);
            noDataText.setVisibility(GONE);
            reloadlayout.setVisibility(View.GONE);
            nologinlayout.setVisibility(View.VISIBLE);
        }  else if (loadType == Constants.NO_DATA) {
            loadlayout.setVisibility(View.GONE);
            nologinlayout.setVisibility(View.GONE);
            reloadlayout.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
        }
    }

}
