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
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

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
            LoadingDialog dialog;
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog==null){
                            dialog = new LoadingDialog(BuyVipActivity.this);
                            dialog.setLoadingText("加载中").setSuccessText("加载成功").closeSuccessAnim();
                            dialog.show();
                        }

                    }
                });
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                findViewById(R.id.cover).setVisibility(View.GONE);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog!=null){
                        dialog.close();
                        }
                    }
                });
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("platformapi/startapp")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(dialog==null){
                                dialog = new LoadingDialog(BuyVipActivity.this);
                                dialog.setLoadingText("加载中").setSuccessText("加载成功").closeSuccessAnim();
                                dialog.show();
                            }

                        }
                    });
                    Intent intent;
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setComponent(null);
                        startActivity(intent);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(dialog!=null){
                                    dialog.close();

                                }
                                button.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    button.setVisibility(View.VISIBLE); view.loadUrl(url);
                }
                return true;
            }
        });
    }

    public class JsInteration {

        @JavascriptInterface
        public void payInfoMessage(String message) throws JSONException {
            Log.e("message",message);
            if (message.equals("SUCCESS")) {
                VipDataSave dataSave = new VipDataSave(BuyVipActivity.this);
                String vipContent =dataSave.getVipData();
                dataSave.setVipData("##"+new LoginDataSave(BuyVipActivity.this).getLoginPhone()+"isVip"+"##"+vipContent);
                FileUtils fileUtils=new FileUtils();
                String content = new FileUtils().readInfo(Constants.VIP);
                if(fileUtils.isExternalStorageReadable() && fileUtils.isExternalStorageWritable()){
                    fileUtils.write2SDFromInput(Constants.VIP,"##"+new LoginDataSave(BuyVipActivity.this).getLoginPhone()+"isVip"+"##"+content);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setVisibility(View.GONE);
                        BuyVipActivity.this.finish();
                    }
                });
            }
        }
        @JavascriptInterface
        public void payMoney(final String message) throws JSONException {
            if (!message.equals("")&&message!=null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setVisibility(View.VISIBLE);
                        button.setText("点击支付"+message+"元立刻享受1年VIP会员");
                    }
                });
            }
        }
    }

    public void back(View view) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

}






