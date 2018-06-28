package com.act.videochat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.act.videochat.ApiUrls;
import com.act.videochat.R;
import com.act.videochat.manager.OkHttpClientManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GirlInfoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycleview);

    }



    private void requestInfoBaseData(String id) {

        RequestBody formBody = new FormBody.Builder()
                .add("userId", "")
                .add("userKey", "")
                .add("vid", id)
                .build();

        Call call = OkHttpClientManager.newInstance(this).newCall(new Request.Builder().url(ApiUrls.GIRL_INFO_DETAIL_BASE_HREF).post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }


    private void requestCommentInfo(String id) {

        RequestBody formBody = new FormBody.Builder()
                .add("userId", "")
                .add("userKey", "")
                .add("vid", id)
                .add("page","1")
                .add("pageCount","30")
                .build();

        Call call = OkHttpClientManager.newInstance(this).newCall(new Request.Builder().url(ApiUrls.GIRL_INFO_COMMENT_HREF).post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }
    private void requestDetailData(String id) {

        RequestBody formBody = new FormBody.Builder()
                .add("userId", "")
                .add("userKey", "")
                .add("vid", id)
                .add("page","1")

                .build();

        Call call = OkHttpClientManager.newInstance(this).newCall(new Request.Builder().url(ApiUrls.GIRL_INFO_DETAIL_DATA_HREF).post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }



    private void requestBigVideoListInfo(String id) {

        RequestBody formBody = new FormBody.Builder()
                .add("userId", "")
                .add("userKey", "")
                .add("vid", id)
                .add("page","1")
                .build();

        Call call = OkHttpClientManager.newInstance(this).newCall(new Request.Builder().url(ApiUrls.GIRL_CHATING_SMALL_LIST_HREF).post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }
}
