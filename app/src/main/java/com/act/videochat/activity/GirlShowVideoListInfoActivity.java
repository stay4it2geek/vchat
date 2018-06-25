package com.act.videochat.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.View;

import com.act.videochat.ApiUrls;
import com.act.videochat.Constants;
import com.act.videochat.R;
import com.act.videochat.adapter.VideoWorksListAdapter;
import com.act.videochat.bean.BigVideoOneUserInfoModel;
import com.act.videochat.bean.CommonVideoListModel;
import com.act.videochat.bean.SmallPlayVideoInfoModel;
import com.act.videochat.manager.OkHttpClientManager;
import com.act.videochat.util.CommonUtil;
import com.act.videochat.view.YRecycleview;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.act.videochat.manager.OkHttpClientManager.createChart;
import static com.act.videochat.manager.OkHttpClientManager.createNumData;
import static com.act.videochat.manager.OkHttpClientManager.getStringRandom;


public class GirlShowVideoListInfoActivity extends AppCompatActivity {
    YRecycleview recyclerView;
    BigVideoOneUserInfoModel headerInfo;
    StaggeredGridLayoutManager gridLayoutManager;
    VideoWorksListAdapter adapter;
    ArrayList<CommonVideoListModel.HomeVideoInfoData> details = new ArrayList<>();
    int currentPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list_info);
        headerInfo = (BigVideoOneUserInfoModel) getIntent().getSerializableExtra(Constants.LIVE_INFO_USER_INFO);
        recyclerView = (YRecycleview) findViewById(R.id.videoRecyleview);
        gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setReFreshEnabled(true);
        recyclerView.setLoadMoreEnabled(true);
        getData(headerInfo.data.id, "1", Constants.REFRESH);
        recyclerView.setRefreshAndLoadMoreListener(new YRecycleview.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                getData(headerInfo.data.id, "1", Constants.REFRESH);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setReFreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                getData(headerInfo.data.id, (currentPage + 1) + "", Constants.LOADMORE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setloadMoreComplete();
                    }
                }, 1000);
            }
        });

    }

    private void getData(String vid, String startPage, int what) {
        if (vid == null) {
            handler.sendEmptyMessage(Constants.NetWorkError);
            return;
        }
        OkHttpClientManager.parseRequestGirlSmallVideoList(this, ApiUrls.COMMON_VIDEO_SMALL_LIST_HREF, handler, what, vid, startPage);
    }


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NetWorkError) {
                final CommonVideoListModel result = CommonUtil.parseJsonWithGson((String) msg.obj, CommonVideoListModel.class);
                currentPage = result.currPage;
                ArrayList<CommonVideoListModel.HomeVideoInfoData> girlDetail = result.data;

                if (msg.what == Constants.REFRESH) {
                    details.clear();
                    currentPage = 1;
                }
                if (result.maxCount > 0) {
                    details.addAll(girlDetail);
                }

                if (msg.what == Constants.LOADMORE && result.data.size() == 0) {
                    recyclerView.setNoMoreData(true);
                }
                if (adapter == null) {
                    adapter = new VideoWorksListAdapter(GirlShowVideoListInfoActivity.this, headerInfo, details, result.maxCount);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new VideoWorksListAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            startPlay(result.maxPage, position);
                        }
                    });

                } else {
                    adapter.notifyDataSetChanged();
                }

            }
        }
    };

    private void startPlay(final int maxPage, final int position) {

        final Intent intent = new Intent(GirlShowVideoListInfoActivity.this, TCVodPlayerActivity.class);
        intent.putExtra(Constants.LIVE_INFO_LIST, details);
        intent.putExtra(Constants.LIVE_INFO_POSITION, position);
        intent.putExtra(Constants.LIVE_INFO_VIDEO_COVER, details.get(position).cover);
        intent.putExtra(Constants.LIVE_INFO_CURRENTPAGE, currentPage);
        intent.putExtra(Constants.LIVE_INFO_MAXCOUNT, maxPage);
        RequestBody formBody = new FormBody.Builder()
                .add("userId", "0")
                .add("userKey", "")
                .add("macid", createChart(6) + "-" + getStringRandom(4) + "-" + getStringRandom(4) + "-" + createNumData(4) + "-" + createNumData(6) + getStringRandom(6))
                .add("videoId", details.get(position).id).build();

        Call call = OkHttpClientManager.newInstance(GirlShowVideoListInfoActivity.this).newCall(new Request.Builder().url(ApiUrls.SMALL_PLAY_VIDEO_INO_HREF).post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String entityStr = response.body().string();
                final SmallPlayVideoInfoModel model = CommonUtil.parseJsonWithGson(entityStr, SmallPlayVideoInfoModel.class);
                intent.putExtra(Constants.LIVE_INFO_VIDEO_ID, model.data.vid);
                intent.putExtra(Constants.LIVE_INFO_AVATAR_URL, model.data.avatar.url);
                intent.putExtra(Constants.LIVE_INFO_VIDEO_URL, model.data.url);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }
        });

    }


}
