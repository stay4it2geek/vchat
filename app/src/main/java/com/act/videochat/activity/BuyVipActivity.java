package com.act.videochat.activity;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.act.videochat.Constants;
import com.act.videochat.R;
import com.act.videochat.util.FileUtils;
import com.act.videochat.util.LoginDataSave;
import com.act.videochat.util.VipDataSave;
import com.act.videochat.view.FragmentDialog;

import org.json.JSONException;

public class BuyVipActivity extends AppCompatActivity {

    WebView mWebView;
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyvip);

        mWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = mWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.loadUrl("http://xiaogu1.av86.vip/index.php");

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
                        FragmentDialog.newInstance(false, "!!支付时不能关闭该应用", "即将打开支付宝支付,支付时不能关闭该应用,支付完成后返回该页面,则自动升级为VIP会员", "立即支付", "取消", "", "", false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(Dialog dialog) {
                                mWebView.loadUrl("javascript:callPay()");
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                dialog.dismiss();
                            }
                        }).show(getSupportFragmentManager(),"");

                    }
                });

            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                findViewById(R.id.cover).setVisibility(View.GONE);
                super.onPageFinished(view, url);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("TAG", "访问的url地址：" + url);
                if (url.contains("platformapi/startapp")) {
                    button.setVisibility(View.GONE);
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
                    button.setVisibility(View.VISIBLE);
                    view.loadUrl(url);
                }

                return true;

            }

        });

    }


    public class JsInteration {

        @JavascriptInterface
        public void payInfoMessage(String message) throws JSONException {

            if (message.equals("SUCCESS")) {
                VipDataSave dataSave = new VipDataSave(BuyVipActivity.this);
                dataSave.setVipData(new LoginDataSave(BuyVipActivity.this).getLoginPhone()+"isVip");
                FileUtils fileUtils=new FileUtils();
                if(fileUtils.isExternalStorageReadable() && fileUtils.isExternalStorageWritable()){
                    if(!fileUtils.isFileExist(Constants.VIP)){
                        fileUtils.write2SDFromInput(Constants.VIP,new LoginDataSave(BuyVipActivity.this).getLoginPhone()+"isVip");
                    }

                }
                button.setVisibility(View.GONE);
                BuyVipActivity.this.finish();
            }
        }

    }


    public void back(View view) {
        this.finish();
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






