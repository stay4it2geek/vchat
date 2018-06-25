package com.act.videochat.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.act.videochat.ApiUrls;
import com.act.videochat.Constants;
import com.act.videochat.R;
import com.act.videochat.activity.TCVodPlayerActivity;
import com.act.videochat.adapter.CommonVideoListAdapter;
import com.act.videochat.bean.CommonVideoListModel;
import com.act.videochat.bean.SmallPlayVideoInfoModel;
import com.act.videochat.manager.OkHttpClientManager;
import com.act.videochat.util.CommonUtil;
import com.act.videochat.view.LoadNetView;
import com.act.videochat.view.YRecycleview;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.act.videochat.manager.OkHttpClientManager.createChart;
import static com.act.videochat.manager.OkHttpClientManager.createNumData;
import static com.act.videochat.manager.OkHttpClientManager.getStringRandom;

public class CommonVideoListFragment extends ScrollAbleFragment {

    YRecycleview recycleview;
    int currentPage;
    CommonVideoListAdapter adapter;
    View view;
    String categoryId;
    LoadNetView loadNetView;
    private MyHandler converDataHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recycleview, null, false);
        if (getArguments() != null) {
            categoryId = getArguments().getString(Constants.TAG_ID);
        }
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);

        recycleview = (YRecycleview) view.findViewById(R.id.yrecycle_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleview.setLayoutManager(gridLayoutManager);
        recycleview.setReFreshEnabled(true);
        recycleview.setLoadMoreEnabled(true);
        converDataHandler = new MyHandler();
        recycleview.setRefreshAndLoadMoreListener(new YRecycleview.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                getData(categoryId, "1", Constants.REFRESH);
                converDataHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleview.setReFreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore() {
                getData(categoryId, (currentPage + 1) + "", Constants.LOADMORE);
                converDataHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleview.setloadMoreComplete();
                    }
                }, 2000);
            }
        });
        getData(categoryId, "1", Constants.REFRESH);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;     //截断事件的传递
            }
        });
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getData(categoryId, "1", Constants.REFRESH);
            }
        });

        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getData(categoryId, "1", Constants.REFRESH);
            }
        });

        return view;
    }

    ArrayList<CommonVideoListModel.HomeVideoInfoData> details = new ArrayList<>();

    class MyHandler extends Handler {
        ArrayList<SmallPlayVideoInfoModel> entities = new ArrayList<>();

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NetWorkError) {
                final CommonVideoListModel result = CommonUtil.parseJsonWithGson((String) msg.obj, CommonVideoListModel.class);
                currentPage = result.currPage;
                ArrayList<CommonVideoListModel.HomeVideoInfoData> girlDetail = result.data;
                if (msg.what == Constants.REFRESH) {
                    currentPage = 1;
                    details.clear();
                    entities.clear();
                }
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (result.maxCount > 0) {
                    details.addAll(girlDetail);
                    if (adapter == null) {
                        adapter = new CommonVideoListAdapter(getActivity(), size.x);
                        recycleview.setAdapter(adapter);
                        adapter.setOnItemClickListener(new CommonVideoListAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getActivity(), TCVodPlayerActivity.class);
                                intent.putExtra(Constants.LIVE_INFO_LIST, entities);
                                intent.putExtra(Constants.LIVE_INFO_POSITION, position);
                                intent.putExtra(Constants.LIVE_INFO_CATAGORY_ID, categoryId);
                                intent.putExtra(Constants.LIVE_INFO_CURRENTPAGE, currentPage);
                                intent.putExtra(Constants.LIVE_INFO_MAXCOUNT, result.maxPage);
                                intent.putExtra(Constants.VIDEO_COVER, details.get(position).cover);
                                getActivity().startActivity(intent);
                            }
                        });
                    }
                    for (final CommonVideoListModel.HomeVideoInfoData data : girlDetail) {
                        String url = ApiUrls.SMALL_PLAY_VIDEO_INO_HREF;
                        RequestBody formBody = new FormBody.Builder()
                                .add("userId", "0")
                                .add("userKey", "")
                                .add("macid", createChart(6) + "-" + getStringRandom(4) + "-" + getStringRandom(4) + "-" + createNumData(4) + "-" + createNumData(6) + getStringRandom(6))
                                .add("videoId", data.id)
                                .build();
                        Call call = OkHttpClientManager.newInstance(getActivity()).newCall(new Request.Builder().url(url).post(formBody).build());
                        call.enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String str = response.body().string();
                                SmallPlayVideoInfoModel entity = CommonUtil.parseJsonWithGson(str, SmallPlayVideoInfoModel.class);
                                entity.data.coverUrl = data.cover;
                                entities.add(entity);
                                adapter.setDatas(entities);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadNetView.setVisibility(View.GONE);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                    if (result.maxCount < currentPage) {
                        recycleview.setNoMoreData(true);
                    }
                }
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    }

    ;
    public void getData(final String categoryId, String startPage, final int what) {
        if (categoryId == null) {
            converDataHandler.sendEmptyMessage(Constants.NetWorkError);
            return;
        }
        String url = "http://youmei.xiumei99.com/homepage";
        OkHttpClientManager.parseRequestGirlHomePage(getActivity(), url, converDataHandler, what, categoryId, startPage);
    }


    @Override
    public View getScrollableView() {
        return recycleview;
    }
}
