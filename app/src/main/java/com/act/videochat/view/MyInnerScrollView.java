package com.act.videochat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by asus-pc on 2018/6/11.
 */

public class MyInnerScrollView extends ScrollView {


    public MyInnerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




    private int count = 0;
    private OnScrollToBottomListener onScrollToBottom;


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View childAt = getChildAt(0);


        if (childAt.getMeasuredHeight() <= getHeight() + getScrollY()) {
            count++;
            if (count == 1) {
                Log.i("myscrollview", "开始加载更多");
                if (onScrollToBottom != null) {
                    onScrollToBottom.onScrollBottomListener(true);
                    count = 0;
                }
            }
        } else {
            count = 0;
            if (onScrollToBottom != null) {
                onScrollToBottom.onScrollBottomListener(false);
            }
        }
    }


    public void setOnScrollToBottomLintener(OnScrollToBottomListener listener) {
        onScrollToBottom = listener;
    }


    public interface OnScrollToBottomListener {
        void onScrollBottomListener(boolean isBottom);
    }


}
