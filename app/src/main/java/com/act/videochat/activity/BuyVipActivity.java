package com.act.videochat.activity;


import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.act.videochat.R;
import com.act.videochat.util.WatchAndVipDataSave;

import org.json.JSONException;
import org.json.JSONObject;

public class BuyVipActivity extends AppCompatActivity{

    WebView mWebView;
    Button button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyvip);

        mWebView =(WebView) findViewById(R.id.webview);

        WebSettings webSettings = mWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.loadUrl("http://xiaogu1.av86.vip");

        button = (Button) findViewById(R.id.button);

        /**
         * 设置js调用Android的接口
         */
        mWebView.addJavascriptInterface(new JsInteration(), "control");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过Handler发送消息
                mWebView.post(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {

                        // 注意调用的JS方法名要对应上
                        // 调用javascript的callJS()方法
                            mWebView.loadUrl("javascript:callpay()");

                    }
                });

            }
        });


        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("TAG", "访问的url地址：" + url);
                if (url.contains("platformapi/startapp")) {

                        Intent intent;
                        try {
                            intent = Intent.parseUri(url,
                                    Intent.URI_INTENT_SCHEME);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setComponent(null);
                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                } else {
                    view.loadUrl(url);
                }

                return true;

            }

        });

    }



    public class JsInteration {

        @JavascriptInterface
        public void payInfoMessage(String payInfoJson) throws JSONException {
            //js将返回payInfoJson 信息 , 具体是什么信息 , 需要js传入 , 我这里传入的是json
            //获取payInfoJson之后 , 就可以在这里使用了 , 最好用handler
            Log.e("payInfoJson",payInfoJson+"");
            JSONObject object=new JSONObject(payInfoJson);
            String message =object.getString("message");
            Log.e("payInfoJson",message+"");
            if(message.equals("SUCCESS")){
                WatchAndVipDataSave dataSave=new WatchAndVipDataSave(BuyVipActivity.this);
                dataSave.setVipData("isVip");
                button.setVisibility(View.GONE);
               findViewById(R.id.okaimage).setVisibility(View.VISIBLE);
            }
        }

    }









    /**
     * 当退出加载webView的Activity时 , 记得将webView销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }


}






