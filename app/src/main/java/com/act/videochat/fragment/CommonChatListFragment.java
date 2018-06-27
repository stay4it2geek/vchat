package com.act.videochat.fragment;

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
import android.widget.ImageView;

import com.act.videochat.ApiUrls;
import com.act.videochat.Constants;
import com.act.videochat.R;
import com.act.videochat.adapter.CommonChatListAdapter;
import com.act.videochat.bean.CommonChatListModel;
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
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                getData(tagId, (currentPage + 1) + "", Constants.LOADMORE);
                converDataHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleview.setloadMoreComplete();
                    }
                }, 1000);
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

                            }
                        });
                    }
                    adapter.notifyDataSetChanged();

                    if (result.maxPage < currentPage) {
                        recycleview.setNoMoreData(true);
                    }
                    loadNetView.setVisibility(View.GONE);
                } else {
                    loadNetView.setlayoutVisily(Constants.NO_DATA);
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
        OkHttpClientManager.parseRequestGirlHomePage(getActivity(), ApiUrls.HOME_CHAT_USER_LIST_HREF, converDataHandler, what, tagId, startPage, "97728", "e9e71fed976fd74763236b86ee3a93b2");
    }


    @Override
    public View getScrollableView() {
        return recycleview;
    }


    private void startPlay(final int maxPage, final int position) {


        RequestBody formBody = new FormBody.Builder()
                .add("userId", "")
                .add("userKey", "")
                .add("macid", getStringRandom(20))
//                .add("videoId", videoDetails.get(position).id)
                .build();

        Call call = OkHttpClientManager.newInstance(getActivity()).newCall(new Request.Builder().url(ApiUrls.SMALL_PLAY_VIDEO_INO_HREF).post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

//                getActivity().startActivity(intent);
            }
        });

    }


}
