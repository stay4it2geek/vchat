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
import com.act.videochat.bean.ChatTagModel;
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

public class DuplicateChatFragment extends Fragment {

    ViewPager view_pager;
    ScrollableLayout mScrollLayout;
    SlidingTabLayout mSlidingTabLayout;
    List<String> tabTitles = new ArrayList<>();
    LoadNetView loadNetView;

    public static DuplicateChatFragment newInstance() {
        DuplicateChatFragment f = new DuplicateChatFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.duplicate_fragment, container, false);
        mScrollLayout = (ScrollableLayout) view.findViewById(R.id.scrollableLayout);
        loadNetView = (LoadNetView) view.findViewById(R.id.loadnetview);
        loadNetView.setlayoutVisily(Constants.LOAD);
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
        Call call = OkHttpClientManager.newInstance(getActivity()).newCall(new Request.Builder().url(ApiUrls.HOME_CHAT_TAG_HREF).post(formBody).build());
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
                ChatTagModel entity = CommonUtil.parseJsonWithGson(str, ChatTagModel.class);
                for (ChatTagModel.ChatTagData data : entity.data) {
                    if (data.name.equals("关注")) {
                        continue;
                    }
                    tabTitles.add(data.name);
                    CommonChatListFragment fragment = new CommonChatListFragment();
                    Bundle bundle = new Bundle();
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


    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        List<ScrollAbleFragment> fragmentList;

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
