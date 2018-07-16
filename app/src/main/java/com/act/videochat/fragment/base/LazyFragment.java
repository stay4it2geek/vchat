package com.act.videochat.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;


public abstract class LazyFragment extends BaseFragment {

    private boolean hasLoaded = false;

    private boolean isCreated = false;

    private boolean isVisibleToUser = false;
    private View view;

    @Override
    protected void initViews(View view, @Nullable Bundle savedInstanceState) {
        isCreated = true;
        this.view = view;
        lazyLoad(this.view, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;//注：关键步骤
        super.setUserVisibleHint(isVisibleToUser);
        lazyLoad(view, null);
    }

    private void lazyLoad(View view, Bundle savedInstanceState) {
        if (!isVisibleToUser || hasLoaded || !isCreated) {
            return;
        }
        lazyInitView(view, savedInstanceState);
        hasLoaded = true;//注：关键步骤，确保数据只加载一次
    }

    public abstract void lazyInitView(View view, Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isCreated = false;
        hasLoaded = false;
    }

}
