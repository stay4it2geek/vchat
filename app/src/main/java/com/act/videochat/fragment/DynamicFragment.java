package com.act.videochat.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.act.videochat.R;
import com.act.videochat.adapter.MineDynamicAdapter;
import com.act.videochat.bean.MineDynamicBean;
import com.act.videochat.fragment.base.LazyFragment;
import com.act.videochat.view.NormalDecoration;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class DynamicFragment extends LazyFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private MineDynamicAdapter adapter;

    public static DynamicFragment getInstance() {
        return new DynamicFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dynamic;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void lazyInitView(View view, Bundle savedInstanceState) {
        final List<MineDynamicBean> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final MineDynamicBean questionBean = new MineDynamicBean();
            questionBean.setContent("使用NestedScrollView+ViewPager+RecyclerView+SmartRefreshLayout打造酷炫下拉视差效果并解决各种滑动冲突" + i);
            if (i % 1 == 0) {
                questionBean.setType(0);
            }
            if (i % 2 == 0) {
                questionBean.setType(1);
            }
            if (i % 3 == 0) {
                questionBean.setType(2);
            }
            data.add(questionBean);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addItemDecoration(new NormalDecoration(ContextCompat.getColor(mActivity, R.color.mainGrayF8), (int) mActivity.getResources().getDimension(R.dimen.one)));

        adapter = new MineDynamicAdapter(mActivity);
        recyclerView.setAdapter(adapter);
        adapter.addAll(data);
        adapter.setNoMore(R.layout.view_no_more);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(mActivity, "---position---" + position, Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                Log.d(TAG, "----onMoreShow");
                adapter.addAll(data);
            }

            @Override
            public void onMoreClick() {

            }
        });
    }
}
