package com.act.videochat.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.act.videochat.ApiUrls;
import com.act.videochat.Constants;
import com.act.videochat.OnScrollShowHideListner;
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
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.act.videochat.manager.OkHttpClientManager.getStringRandom;

public class CommonVideoListFragment extends ScrollAbleFragment {

    YRecycleview recycleview;
    int currentPage;
    CommonVideoListAdapter adapter;
    View view;
    String categoryId;
    LoadNetView loadNetView;
    MyHandler converDataHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recycleview, null, false);
        if (getArguments() != null) {
            categoryId = getArguments().getString(Constants.TAG_ID);
        }
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
        loadNetView.setlayoutVisily(Constants.LOAD);
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
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                getData(categoryId, (currentPage + 1) + "", Constants.LOADMORE);
                converDataHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleview.setloadMoreComplete();
                    }
                }, 1000);
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
                }
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (result.maxCount > 0) {
                    details.addAll(girlDetail);
                    if (adapter == null) {
                        adapter = new CommonVideoListAdapter(getActivity(), size.x);
                        adapter.setDatas(details);
                        recycleview.setAdapter(adapter);
                        adapter.setOnItemClickListener(new CommonVideoListAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position, ImageView photoImg) {
                                ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.scale);
                                photoImg.startAnimation(scaleAnimation);

                                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        startPlay(result.maxPage,position);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();

                    if (result.maxCount < currentPage) {
                        recycleview.setNoMoreData(true);
                    }
                    loadNetView.setVisibility(View.GONE);
                }else{
                    if(msg.what==Constants.LOADMORE){
                        if (result.maxPage < currentPage) {
                            recycleview.setNoMoreData(true);
                        }
                    }else{
                        loadNetView.setlayoutVisily(Constants.NO_DATA);
                    }
                }
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    }


    public void getData(final String categoryId, String startPage, final int what) {
        if (categoryId == null) {
            converDataHandler.sendEmptyMessage(Constants.NetWorkError);
            return;
        }
        OkHttpClientManager.parseRequestGirlHomePage(getActivity(), ApiUrls.COMMON_VIDEO_LIST_HOMEPAGE_HREF, converDataHandler, what, categoryId, startPage,"","");
    }


    @Override
    public View getScrollableView() {
        return recycleview;
    }


    private void startPlay(final int maxPage, final int position) {


        final Intent intent = new Intent(getActivity(), TCVodPlayerActivity.class);
        intent.putExtra(Constants.LIVE_INFO_LIST, details);
        intent.putExtra(Constants.LIVE_INFO_POSITION, position);
        intent.putExtra(Constants.LIVE_INFO_CATAGORY_ID, categoryId);
        intent.putExtra(Constants.LIVE_INFO_VIDEO_COVER, details.get(position).cover);
        intent.putExtra(Constants.LIVE_INFO_CURRENTPAGE, currentPage);
        intent.putExtra(Constants.LIVE_INFO_MAXCOUNT,maxPage);
        RequestBody formBody = new FormBody.Builder()
                .add("userId", "0")
                .add("userKey", "")
                .add("macid", getStringRandom(20))
                .add("videoId", details.get(position).id).build();

        Call call = OkHttpClientManager.newInstance(getActivity()).newCall(new Request.Builder().url(ApiUrls.SMALL_PLAY_VIDEO_INO_HREF).post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String entityStr = response.body().string();
                final SmallPlayVideoInfoModel model = CommonUtil.parseJsonWithGson(entityStr, SmallPlayVideoInfoModel.class);
                intent.putExtra(Constants.LIVE_INFO_VIDEO_ID,model.data.vid);
                intent.putExtra(Constants.LIVE_INFO_ID,model.data.id);
                intent.putExtra(Constants.LIVE_INFO_AVATAR_URL,model.data.avatar.url);
                intent.putExtra(Constants.LIVE_INFO_VIDEO_URL,model.data.url);
                getActivity().startActivity(intent);
            }
        });

    }

    OnScrollShowHideListner listner;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listner= (OnScrollShowHideListner) context;

    }
    private int distance;

    private boolean visible = true;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recycleview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(distance < -ViewConfiguration.getTouchSlop() && !visible){
                    //显示fab
                    listner.onScrollShow();
                    distance = 0;
                    visible = true;
                }else if(distance > ViewConfiguration.getTouchSlop() && visible){
                    //隐藏
                    listner.onScrollHide();
                    distance = 0;
                    visible = false;
                }
                if ((dy > 0 && visible) || (dy < 0 && !visible))//向下滑并且可见  或者  向上滑并且不可见
                    distance += dy;
            }
        });
    }

}
