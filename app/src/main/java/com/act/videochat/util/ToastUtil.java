package com.act.videochat.util;

import android.content.Context;
import android.widget.Toast;


public class ToastUtil {

    private static Toast cacheToast = null;
    public static final void showToast(Context context, String msg) {
        if (cacheToast != null) {
            cacheToast.cancel();
        }
        cacheToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        cacheToast.show();
    }
}
