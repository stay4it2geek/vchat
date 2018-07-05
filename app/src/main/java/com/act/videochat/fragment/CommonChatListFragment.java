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
import android.widget.ImageView;

import com.act.videochat.ApiUrls;
import com.act.videochat.Constants;
import com.act.videochat.OnScrollShowHideListner;
import com.act.videochat.R;
import com.act.videochat.activity.GirlInfoDetailActivity;
import com.act.videochat.adapter.CommonChatListAdapter;
import com.act.videochat.bean.CommonChatListModel;
import com.act.videochat.manager.OkHttpClientManager;
import com.act.videochat.util.CommonUtil;
import com.act.videochat.view.LoadNetView;
import com.act.videochat.view.YRecycleview;


import java.util.ArrayList;

public class CommonChatListFragment extends ScrollAbleFragment {

    YRecycleview recycleview;
    int currentPage;
    CommonChatListAdapter adapter;
    View view;
    String tagId;
    LoadNetView loadNetView;
    MyHandler converDataHandler;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recycleview, null, false);
        if (getArguments() != null) {
            tagId = getArguments().getString(Constants.TAG_ID);
        }
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
        loadNetView.setlayoutVisily(Constants.LOAD);
        recycleview = (YRecycleview) view.findViewById(R.id.yrecycle_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleview.setLayoutManager(gridLayoutManager);
        recycleview.setReFreshEnabled(true);
        recycleview.setLoadMoreEnabled(true);
        converDataHandler = new MyHandler();
        recycleview.setRefreshAndLoadMoreListener(new YRecycleview.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                getData(tagId, "1", Constants.REFRESH);
                converDataHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleview.setReFreshComplete();
                    }
                },500);
            }

            @Override
            public void onLoadMore() {
                getData(tagId, (currentPage + 1) + "", Constants.LOADMORE);
                converDataHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleview.setloadMoreComplete();
                    }
                }, 500);
            }
        });
        getData(tagId, "1", Constants.REFRESH);
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
                getData(tagId, "1", Constants.REFRESH);
            }
        });

        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getData(tagId, "1", Constants.REFRESH);
            }
        });

        return view;
    }


    ArrayList<CommonChatListModel.HomeChatInfoData> chatDetails = new ArrayList<>();


    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NetWorkError) {
                final CommonChatListModel result = CommonUtil.parseJsonWithGson((String) msg.obj, CommonChatListModel.class);
                currentPage = result.currPage;
                ArrayList<CommonChatListModel.HomeChatInfoData> details = result.data;
                if (msg.what == Constants.REFRESH) {
                    currentPage = 1;
                    chatDetails.clear();
                }
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (details != null && details.size() > 0) {
                    chatDetails.addAll(details);
                    if (adapter == null) {
                        adapter = new CommonChatListAdapter(getActivity(), size.x);
                        adapter.setDatas(chatDetails);
                        recycleview.setAdapter(adapter);
                        adapter.setOnItemClickListener(new CommonChatListAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position, ImageView photoImg) {
                                Intent intent = new Intent(getActivity(), GirlInfoDetailActivity.class);
                                intent.putExtra("online",chatDetails.get(position).online);
                                intent.putExtra(Constants.USERID,chatDetails.get(position).id);
                                intent.putExtra(Constants.CHAT_GIRL,chatDetails.get(position));
                                    startActivity(intent);
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();

                    if (result.maxPage < currentPage) {
                        recycleview.setNoMoreData(true);
                    }
                    loadNetView.setVisibility(View.GONE);
                } else {
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


    public void getData(final String tagId, String startPage, final int what) {
        if (tagId == null) {
            converDataHandler.sendEmptyMessage(Constants.NetWorkError);
            return;
        }
        OkHttpClientManager.parseRequestGirlHomePage(getActivity(), ApiUrls.HOME_CHAT_USER_LIST_HREF, converDataHandler, what, tagId, startPage, Constants.USERID, Constants.USERKEY);
    }


    @Override
    public View getScrollableView() {
        return recycleview;
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
                    //iv_go_uploading.setVisibility(View.VISIBLE);
                   listner.onScrollShow();
                    distance = 0;
                    visible = true;
                }else if(distance > ViewConfiguration.getTouchSlop() && visible){
                    //隐藏
                    //iv_go_uploading.setVisibility(View.GONE);
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
