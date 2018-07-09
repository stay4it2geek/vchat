package com.act.videochat.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.act.videochat.Constants;
import com.act.videochat.R;
import com.act.videochat.adapter.FollowVideoListAdapter;
import com.act.videochat.bean.BigVideoOneUserInfoModel;
import com.act.videochat.manager.PageHelper;
import com.act.videochat.util.FollowDataSave;
import com.act.videochat.view.LoadNetView;
import com.act.videochat.view.YRecycleview;

import java.util.ArrayList;
import java.util.List;

public class VideoFollowActivity extends AppCompatActivity {
    YRecycleview recycleview;

    FollowVideoListAdapter adapter;
    ArrayList<BigVideoOneUserInfoModel> list = new ArrayList<>();
    LoadNetView loadNetView;
    Handler converDataHandler;
    private List<BigVideoOneUserInfoModel> mLocalDatas;
    private PageHelper<BigVideoOneUserInfoModel> mPageDaoImpl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycleview);

        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        loadNetView.setlayoutVisily(Constants.LOAD);
        recycleview = (YRecycleview) findViewById(R.id.yrecycle_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleview.setLayoutManager(gridLayoutManager);
        recycleview.setReFreshEnabled(true);
        recycleview.setLoadMoreEnabled(true);
        converDataHandler = new Handler();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        mLocalDatas = new FollowDataSave(this, Constants.VIDEO_GIRL_FOLLOW).getVideoGirlDataList(Constants.VIDEO_GIRL_FOLLOW_LIST);
        adapter = new FollowVideoListAdapter(this, size.x);
        recycleview.setAdapter(adapter);

        recycleview.setRefreshAndLoadMoreListener(new YRecycleview.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                list.clear();
                mPageDaoImpl.setCurrentPage(1);
                getData(Constants.REFRESH);

                converDataHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleview.setReFreshComplete();
                    }
                }, 500);
                recycleview.setNoMoreData(false);
            }

            @Override
            public void onLoadMore() {

                if (mPageDaoImpl.getCurrentPage() <= mPageDaoImpl.getPageNum()) {
                    mPageDaoImpl.nextPage();

                }

                getData(Constants.LOADMORE);



                converDataHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleview.setloadMoreComplete();
                        if (!mPageDaoImpl.hasNextPage()) {
                            recycleview.setNoMoreData(true);
                        }
                    }
                }, 1000);
            }
        });


        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                list.clear();
                mPageDaoImpl.setCurrentPage(1);
                getData(Constants.REFRESH);
            }
        });

        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                list.clear();
                mPageDaoImpl.setCurrentPage(1);
                getData(Constants.REFRESH);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        mLocalDatas = new FollowDataSave(this, Constants.VIDEO_GIRL_FOLLOW).getVideoGirlDataList(Constants.VIDEO_GIRL_FOLLOW_LIST);
        mPageDaoImpl = new PageHelper<>(mLocalDatas, 10);
        mPageDaoImpl.setCurrentPage(1);
        recycleview.setNoMoreData(false);
        getData(Constants.REFRESH);
    }


    public void getData(int what) {

        if (mPageDaoImpl.currentList().size() > 0) {
            list.addAll(mPageDaoImpl.currentList());
            adapter.setDatas(list);

            adapter.setOnItemClickListener(new FollowVideoListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, final int position, ImageView photoImg) {
                    Intent intent = new Intent(VideoFollowActivity.this, GirlShowVideoListInfoActivity.class);
                    intent.putExtra(Constants.LIVE_INFO_USER_INFO, list.get(position));
                    startActivity(intent);
                }
            });
            loadNetView.setVisibility(View.GONE);
        }else{
            if (Constants.REFRESH==what){
                converDataHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNetView.setVisibility(View.VISIBLE);
                        loadNetView.setlayoutVisily(Constants.NO_DATA);
                    }
                }, 1000);
            }
        }
        adapter.notifyDataSetChanged();


    }
}