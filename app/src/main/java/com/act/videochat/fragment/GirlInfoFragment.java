package com.act.videochat.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.act.videochat.ApiUrls;
import com.act.videochat.Constants;
import com.act.videochat.R;
import com.act.videochat.adapter.GirlInfoAdapter;
import com.act.videochat.bean.ChatGirlInfoComment;
import com.act.videochat.bean.ChatGirlInfoDetail;
import com.act.videochat.fragment.base.LazyFragment;
import com.act.videochat.manager.OkHttpClientManager;
import com.act.videochat.util.CommonUtil;
import com.act.videochat.view.NormalDecoration;
import com.act.videochat.view.YRecycleview;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class GirlInfoFragment extends LazyFragment {

    @BindView(R.id.recycler_view)
    YRecycleview recyclerView;

    private GirlInfoAdapter adapter;


    String id;

    private int currentPage;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_girl_info;
    }

    @Override
    protected void initData() {
        id = getArguments().getString(Constants.USERID);
        adapter = new GirlInfoAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        requestDetailData(id);
        requestCommentInfo(id);
    }

    @Override
    public void lazyInitView(View view, Bundle savedInstanceState) {


        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addItemDecoration(new NormalDecoration(ContextCompat.getColor(mActivity, R.color.mainGrayF8), (int) mActivity.getResources().getDimension(R.dimen.one)));


        recyclerView.setReFreshEnabled(false);
        recyclerView.setLoadMoreEnabled(true);

        recyclerView.setRefreshAndLoadMoreListener(new YRecycleview.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                currentPage = currentPage + 1;
                requestCommentInfo(id);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setloadMoreComplete();
                    }
                }, 500);
            }
        });


    }

    ArrayList<ChatGirlInfoComment.CommentTagList> commentTagLists = new ArrayList<>();

    private void requestCommentInfo(String id) {

        RequestBody formBody = new FormBody.Builder()
                .add("userId", Constants.USERID)
                .add("userKey", Constants.USERKEY)
                .add("vid", id)
                .add("page", currentPage + "")
                .add("pageCount", "30")
                .build();

        Call call = OkHttpClientManager.newInstance(getActivity()).newCall(new Request.Builder().url(ApiUrls.GIRL_INFO_COMMENT_HREF).post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                final ChatGirlInfoComment comment = CommonUtil.parseJsonWithGson(str, ChatGirlInfoComment.class);

                if (comment != null && comment.data != null && comment.data.size() > 0) {
                    commentTagLists.addAll(comment.data);
                    adapter.setCommentsData(commentTagLists);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            if (comment.maxPage < currentPage) {
                                recyclerView.setNoMoreData(true);
                            }

                        }
                    });
                }
            }
        });

    }

    private void requestDetailData(final String id) {

        RequestBody formBody = new FormBody.Builder()
                .add("userId", Constants.USERID)
                .add("userKey", Constants.USERKEY)
                .add("vid", id)
                .build();

        Call call = OkHttpClientManager.newInstance(getActivity()).newCall(new Request.Builder().url(ApiUrls.GIRL_INFO_DETAIL_DATA_HREF).post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                ChatGirlInfoDetail detail = CommonUtil.parseJsonWithGson(str, ChatGirlInfoDetail.class);
                adapter.setFigurTagAndCommentTagsData(detail);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }


}
