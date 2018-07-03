package com.act.videochat.activity;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.videochat.ApiUrls;
import com.act.videochat.Constants;
import com.act.videochat.R;
import com.act.videochat.adapter.ComFragmentAdapter;
import com.act.videochat.bean.ChatGirlInfoBase;
import com.act.videochat.fragment.GirlInfoFragment;
import com.act.videochat.manager.OkHttpClientManager;
import com.act.videochat.util.CommonUtil;
import com.act.videochat.util.DeviceUtil;
import com.act.videochat.util.GlideImageLoader;
import com.act.videochat.util.ScreenUtil;
import com.act.videochat.util.StatusBarUtil;
import com.act.videochat.view.ColorFlipPagerTitleView;
import com.act.videochat.view.JudgeNestedScrollView;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.youth.banner.Banner;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GirlInfoDetailActivity extends BaseActivity {
    @BindView(R.id.iv_header)
    Banner banner;
    @BindView(R.id.onlinestatus)
    TextView onlinestatus;
    @BindView(R.id.online_dot)
    TextView online_dot;
    @BindView(R.id.collapse)
    CollapsingToolbarLayout collapse;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.scrollView)
    JudgeNestedScrollView scrollView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.toolbar_avatar)
    CircleImageView toolbarAvatar;
    @BindView(R.id.toolbar_username)
    TextView toolbarUsername;
    @BindView(R.id.buttonBarLayout)
    ButtonBarLayout buttonBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.magic_indicator_title)
    MagicIndicator magicIndicatorTitle;
    @BindView(R.id.fl_activity)
    FrameLayout flActivity;

    @BindView(R.id.nickName)
    TextView nickName;


    @BindView(R.id.identity)
    TextView identity;


    @BindView(R.id.fansCount)
    TextView fansCount;

    @BindView(R.id.star_Layout)
    LinearLayout star_Layout;


    private int mOffset = 0;
    private int mScrollY = 0;
    int toolBarPositionY = 0;
    private String[] mTitles = new String[]{"资料"};
    private List<String> mDataList = Arrays.asList(mTitles);


    @Override
    public int setLayoutId() {
        return R.layout.activity_girl_detail;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initView();
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {

            }

            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                requestInfoBaseData(getIntent().getStringExtra(Constants.USERID));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshlayout.finishRefresh();
                    }
                },1000);
            }
        });
    }

    @Override
    public void initData() {
        requestInfoBaseData(getIntent().getStringExtra(Constants.USERID));
    }

    private void initView() {
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);

        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }

            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }
        });

        /**
         * 判断是否是华为手机并且是否有虚拟导航键
         */
        if (DeviceUtil.isHUAWEI() && DeviceUtil.checkDeviceHasNavigationBar(this.getApplicationContext())) {
            getContentResolver().registerContentObserver(Settings.System.getUriFor
                    ("navigationbar_is_min"), true, mNavigationStatusObserver);
        }
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                dealWithViewPager();
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            int lastScrollY = 0;
            int h = DensityUtil.dp2px(170);
            int color = ContextCompat.getColor(getApplicationContext(), R.color.mainWhite) & 0x00ffffff;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int[] location = new int[2];
                magicIndicator.getLocationOnScreen(location);
                int yPosition = location[1];
                if (yPosition < toolBarPositionY) {
                    scrollView.setNeedScroll(false);
                } else {
                    scrollView.setNeedScroll(true);

                }

                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    buttonBarLayout.setAlpha(1f * mScrollY / h);
                    toolbar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                }
                if (scrollY == 0) {
                    ivBack.setImageResource(R.drawable.back_white);
                } else {
                    ivBack.setImageResource(R.drawable.back_black);
                }

                lastScrollY = scrollY;
            }
        });
        buttonBarLayout.setAlpha(0);
        toolbar.setBackgroundColor(0);

        List<Fragment> fragments = new ArrayList<>();
        GirlInfoFragment fragment1 = new GirlInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USERID, getIntent().getStringExtra(Constants.USERID) + "");
        fragment1.setArguments(bundle);
        fragments.add(fragment1);
        viewPager.setAdapter(new ComFragmentAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(3);
        initMagicIndicator();
        initMagicIndicatorTitle();
    }

    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.65f);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(GirlInfoDetailActivity.this, R.color.mainBlack));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(GirlInfoDetailActivity.this, R.color.mainRed));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index, false);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 2));
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(ContextCompat.getColor(GirlInfoDetailActivity.this, R.color.mainRed));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    private void initMagicIndicatorTitle() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(GirlInfoDetailActivity.this, R.color.mainBlack));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(GirlInfoDetailActivity.this, R.color.mainRed));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index, false);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 2));
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(ContextCompat.getColor(GirlInfoDetailActivity.this, R.color.mainRed));
                return indicator;
            }
        });
        magicIndicatorTitle.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicatorTitle, viewPager);

    }

    private ContentObserver mNavigationStatusObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            dealWithHuaWei();
        }
    };

    private void dealWithViewPager() {
        toolBarPositionY = toolbar.getHeight();
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        params.height = ScreenUtil.getScreenHeightPx(getApplicationContext()) - toolBarPositionY - magicIndicator.getHeight() + 1;
        viewPager.setLayoutParams(params);
    }

    /**
     * 处理华为虚拟键显示隐藏问题导致屏幕高度变化，ViewPager的高度也需要重新测量
     */
    private void dealWithHuaWei() {
        flActivity.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dealWithViewPager();
                flActivity.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    private void requestInfoBaseData(String id) {

        RequestBody formBody = new FormBody.Builder()
                .add("userId", Constants.USERID)
                .add("userKey", Constants.USERKEY)
                .add("vid", id)
                .build();

        Call call = OkHttpClientManager.newInstance(this).newCall(new Request.Builder().url(ApiUrls.GIRL_INFO_DETAIL_BASE_HREF).post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                final ChatGirlInfoBase base = CommonUtil.parseJsonWithGson(str, ChatGirlInfoBase.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        ArrayList<String> list = new ArrayList<>();
                        for (ChatGirlInfoBase.Photos photo : base.data.photos) {
                            list.add(photo.url);
                        }
                        FrameLayout.LayoutParams linearParams = new FrameLayout.LayoutParams(size.x, size.x);
                        banner.setLayoutParams(linearParams);
                        banner.setImages(list).setImageLoader(new GlideImageLoader()).start();
                        banner.setIndicatorGravity(Gravity.RIGHT);
                        for (int index = 0; index < base.data.level; index++) {
                            TextView textView = new TextView(GirlInfoDetailActivity.this);
                            textView.setBackground(ContextCompat.getDrawable(GirlInfoDetailActivity.this, R.drawable.star));
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(37, 37);
                            params.setMargins(3, 5, 6, 3);//设置边距
                            params.gravity = Gravity.CENTER_VERTICAL;
                            textView.setLayoutParams(params);
                            star_Layout.addView(textView);
                        }
                        toolbarUsername.setText(base.data.nickname);
                        nickName.setText(base.data.nickname);
                        fansCount.setText(base.data.fans + "粉丝");
                        identity.setText(base.data.identity);
                        if (GirlInfoDetailActivity.this != null) {
                            Glide.with(GirlInfoDetailActivity.this).load(base.data.avatar.url).error(R.drawable.error_img).into(toolbarAvatar);
                        }


                        int online = getIntent().getIntExtra("online", 0);
                        switch (online) {
                            case 0:
                                onlinestatus.setText("离线");
                                online_dot.setBackground(ContextCompat.getDrawable(GirlInfoDetailActivity.this, R.drawable.circle_dot_offline_shape));
                                break;

                            case 1:
                                onlinestatus.setText("在线");
                                online_dot.setBackground(ContextCompat.getDrawable(GirlInfoDetailActivity.this, R.drawable.circle_dot_online_shape));
                                break;

                            case 2:
                                onlinestatus.setText("在聊");
                                online_dot.setBackground(ContextCompat.getDrawable(GirlInfoDetailActivity.this, R.drawable.circle_dot_talking_shape));
                                break;

                            case 3:
                                onlinestatus.setText("活跃");
                                online_dot.setBackground(ContextCompat.getDrawable(GirlInfoDetailActivity.this, R.drawable.circle_dot_active_shape));
                                break;

                            case 4:
                                onlinestatus.setText("勿扰");
                                online_dot.setBackground(ContextCompat.getDrawable(GirlInfoDetailActivity.this, R.drawable.circle_dot_nodisturb_shape));
                                break;
                        }


                    }
                });


            }
        });

    }


    public void back(View view) {
        this.finish();
    }


}
