package com.act.videochat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.videochat.ApiUrls;
import com.act.videochat.Constants;
import com.act.videochat.R;
import com.act.videochat.bean.HomeVideoTagModel;
import com.act.videochat.manager.OkHttpClientManager;
import com.act.videochat.util.CommonUtil;
import com.act.videochat.view.LoadNetView;
import com.act.videochat.view.scrollable.ScrollableLayout;
import com.flyco.tablayout.SlidingTabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DuplicateVideoFragment extends Fragment {

    private ViewPager view_pager;
    private ScrollableLayout mScrollLayout;
    private SlidingTabLayout mSlidingTabLayout;
    private List<String> tabTitles = new ArrayList<>();
    private LoadNetView loadNetView;

    public static DuplicateVideoFragment newInstance() {
        DuplicateVideoFragment f = new DuplicateVideoFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.duplicate_fragment, container, false);
        loadNetView = (LoadNetView) view.findViewById(R.id.loadnetview);
        loadNetView.setlayoutVisily(Constants.LOAD);
        mScrollLayout = (ScrollableLayout) view.findViewById(R.id.scrollableLayout);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.tab_layout);
        view_pager = (ViewPager) view.findViewById(R.id.view_pager);
        init();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                init();
            }
        });

        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                init();
            }
        });
    }

    ArrayList<ScrollAbleFragment> fragmentList = new ArrayList<>();

    public void init() {
        RequestBody formBody = new FormBody.Builder().build();
        Call call = OkHttpClientManager.newInstance(getActivity()).newCall(new Request.Builder().url(ApiUrls.HOME_VIDEO_TAG_HREF).post(formBody).build());
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadNetView.setlayoutVisily(Constants.RELOAD);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadNetView.setVisibility(View.GONE);
                    }
                });
                String str = response.body().string();
                HomeVideoTagModel entity = CommonUtil.parseJsonWithGson(str, HomeVideoTagModel.class);
                for (HomeVideoTagModel.TagInfoData data : entity.data) {
                    if (data.name.equals("关注") || data.name.equals("推荐")) {
                        continue;
                    }
                    tabTitles.add(data.name);
                    CommonVideoListFragment fragment = new CommonVideoListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment", "TwoCateFragment");
                    bundle.putString(Constants.TAG_ID, data.id + "");
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                }
                if (fragmentList.size() > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
                            view_pager.setAdapter(adapter);
                            view_pager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
                            view_pager.setOffscreenPageLimit(tabTitles.size());
                            view_pager.setCurrentItem(0);
                            mSlidingTabLayout.setViewPager(view_pager);
                            mScrollLayout.getHelper().setCurrentScrollableContainer(fragmentList.get(0));
                            view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                                @Override
                                public void onPageSelected(int index) {
                                    mScrollLayout.getHelper().setCurrentScrollableContainer(fragmentList.get(index));
                                }

                                @Override
                                public void onPageScrolled(int arg0, float arg1, int arg2) {
                                }

                                @Override
                                public void onPageScrollStateChanged(int arg0) {

                                }
                            });
                        }
                    });
                }
            }
        });
    }


    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<ScrollAbleFragment> fragmentList;

        public MyFragmentPagerAdapter(FragmentManager fm, List<ScrollAbleFragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }
    }

}