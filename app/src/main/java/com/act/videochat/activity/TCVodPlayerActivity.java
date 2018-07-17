package com.act.videochat.activity;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.videochat.ApiUrls;
import com.act.videochat.Constants;
import com.act.videochat.R;
import com.act.videochat.bean.BigVideoOneUserInfoModel;
import com.act.videochat.bean.CommonVideoListModel;
import com.act.videochat.bean.SmallPlayVideoInfoModel;
import com.act.videochat.bean.WatchMessageEvent;
import com.act.videochat.manager.OkHttpClientManager;
import com.act.videochat.util.CommonUtil;
import com.act.videochat.util.FileUtils;
import com.act.videochat.util.LoginDataSave;
import com.act.videochat.util.TCUtils;
import com.act.videochat.util.VipDataSave;
import com.act.videochat.util.WatchDataSave;
import com.act.videochat.view.FragmentDialog;
import com.act.videochat.view.YRecycleviewRefreshFootView;
import com.bumptech.glide.Glide;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.act.videochat.manager.OkHttpClientManager.createChart;
import static com.act.videochat.manager.OkHttpClientManager.createNumData;
import static com.act.videochat.manager.OkHttpClientManager.getStringRandom;


public class TCVodPlayerActivity extends AppCompatActivity implements ITXVodPlayListener {
    VerticalViewPager mVerticalViewPager;
    MyPagerAdapter mPagerAdapter;
    TXCloudVideoView mTXCloudVideoView;
    TextView mTvBack;
    ImageView mIvCover;
    int mInitTCLiveInfoPosition;
    int mCurrentPosition;
    YRecycleviewRefreshFootView yrecycle_view_loadMore;
    TXVodPlayer mTXVodPlayer;
    int mMaxCount;
    int mCurrentPage;
    String mCatagoryId;
    String videoUrl = "";
    WatchDataSave watchDataSave;
    VipDataSave vipsave;
    BigVideoOneUserInfoModel model;
    String isVipPrivate;
    String isVipStorage;
    FileUtils fileUtils;
    MyHandler converDataHandler = new MyHandler();
    HashMap<String, String> map = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        yrecycle_view_loadMore = (YRecycleviewRefreshFootView) findViewById(R.id.yrecycle_view_loadMore);
        initData();
        watchDataSave = new WatchDataSave(this, "VideoInfo");
        vipsave = new VipDataSave(this);
        fileUtils = new FileUtils();
        EventBus.getDefault().register(this);
    }

    void initData() {
        Intent intent = getIntent();
        mInitTCLiveInfoPosition = intent.getIntExtra(Constants.LIVE_INFO_POSITION, 0);
        mCurrentPage = intent.getIntExtra(Constants.LIVE_INFO_CURRENTPAGE, 0);
        mMaxCount = intent.getIntExtra(Constants.LIVE_INFO_MAXCOUNT, 0);
        mCatagoryId = intent.getStringExtra(Constants.LIVE_INFO_CATAGORY_ID);
        videoUrl = intent.getStringExtra(Constants.LIVE_INFO_VIDEO_URL);
        String avatarUrl = intent.getStringExtra(Constants.LIVE_INFO_AVATAR_URL);
        String videoId = intent.getStringExtra(Constants.LIVE_INFO_VIDEO_ID);
        if (videoUrl != null && !videoUrl.equals("")) {
            map.put(videoUrl, mInitTCLiveInfoPosition + "");
            map.put("avatar", avatarUrl);
            map.put("vid", videoId);
        }
        initViews();
        initPlayerSDK();
        initPhoneListener();
        //在这里停留，让列表界面卡住几百毫秒，给sdk一点预加载的时间，形成秒开的视觉效果
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    void initViews() {
        Intent intent = getIntent();
        ArrayList<CommonVideoListModel.HomeVideoInfoData> details = (ArrayList<CommonVideoListModel.HomeVideoInfoData>) intent.getSerializableExtra(Constants.LIVE_INFO_LIST);
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.player_cloud_view);
        mIvCover = (ImageView) findViewById(R.id.player_iv_cover);
        mTvBack = (TextView) findViewById(R.id.player_tv_back);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mVerticalViewPager = (VerticalViewPager) findViewById(R.id.vertical_view_pager);
        mVerticalViewPager.setOffscreenPageLimit(3);
        mVerticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                mCurrentPosition = position;
                // 滑动界面，首先让之前的播放器暂停，并seek到0
                if (mTXVodPlayer != null) {
                    mTXVodPlayer.seek(0);
                    mTXVodPlayer.pause();
                }
                if (mCurrentPage < mMaxCount && mPagerAdapter != null && position + 1 == mPagerAdapter.getDataSize()) {
                    getData(mCatagoryId, (mCurrentPage + 1) + "");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mVerticalViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                if (position != 0) {
                    return;
                }

                ViewGroup viewGroup = (ViewGroup) page;
                mIvCover = (ImageView) viewGroup.findViewById(R.id.player_iv_cover);
                mTXCloudVideoView = (TXCloudVideoView) viewGroup.findViewById(R.id.player_cloud_view);
                PlayerInfo playerInfo = mPagerAdapter.findPlayerInfo(mCurrentPosition);
                judgeVip(playerInfo);
            }
        });
        mPagerAdapter = new MyPagerAdapter();
        mPagerAdapter.setDatas(details);
        mVerticalViewPager.setAdapter(mPagerAdapter);
    }

    class PlayerInfo {
        public TXVodPlayer txVodPlayer;
        public String playURL;
        public boolean isBegin;
        public View playerView;
        public int pos;
    }

    class MyPagerAdapter extends PagerAdapter {

        ArrayList<PlayerInfo> playerInfoList = new ArrayList<>();
        ArrayList<String> vipplayerInfoList = new ArrayList<>();
        ArrayList<CommonVideoListModel.HomeVideoInfoData> list = new ArrayList<>();
        public void setDatas(ArrayList<CommonVideoListModel.HomeVideoInfoData> details) {
            list.addAll(details);
        }

        public int getDataSize() {
            return list != null ? list.size() : 0;
        }
        protected PlayerInfo instantiatePlayerInfo(String url, int position) {
            PlayerInfo playerInfo = new PlayerInfo();
            TXVodPlayer vodPlayer = new TXVodPlayer(TCVodPlayerActivity.this);
            vodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
            vodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
            vodPlayer.setVodListener(TCVodPlayerActivity.this);
            TXVodPlayConfig config = new TXVodPlayConfig();
            config.setCacheFolderPath(Environment.getExternalStorageDirectory().getPath() + "/txcache");
            config.setMaxCacheItems(5);
            vodPlayer.setConfig(config);
            vodPlayer.setAutoPlay(false);
            playerInfo.playURL = url;
            playerInfo.txVodPlayer = vodPlayer;
            playerInfo.pos = position;
            playerInfoList.add(playerInfo);
            return playerInfo;
        }
        protected void destroyPlayerInfo(int position) {
            while (true) {
                PlayerInfo playerInfo = findPlayerInfo(position);
                if (playerInfo == null)
                    break;
                playerInfo.txVodPlayer.stopPlay(true);
                playerInfoList.remove(playerInfo);
            }
        }
        public PlayerInfo findPlayerInfo(int position) {
            for (int i = 0; i < playerInfoList.size(); i++) {
                PlayerInfo playerInfo = playerInfoList.get(i);
                if (playerInfo.pos == position) {
                    return playerInfo;
                }
            }
            return null;
        }

        public PlayerInfo findPlayerInfo(TXVodPlayer player) {
            for (int i = 0; i < playerInfoList.size(); i++) {
                PlayerInfo playerInfo = playerInfoList.get(i);
                if (playerInfo.txVodPlayer == player) {
                    return playerInfo;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final View view = LayoutInflater.from(container.getContext()).inflate(R.layout.view_player_content, null);
            view.setId(position);
            ImageView coverImageView = (ImageView) view.findViewById(R.id.player_iv_cover);
            final CircleImageView ivAvatar = (CircleImageView) view.findViewById(R.id.player_civ_avatar);
            final TXCloudVideoView playView = (TXCloudVideoView) view.findViewById(R.id.player_cloud_view);
            coverImageView.setVisibility(View.VISIBLE);
            TCUtils.blurBgPic(TCVodPlayerActivity.this, coverImageView, list.get(position).cover, R.drawable.main_bkg);
            if (map.get(videoUrl) != null && !map.get(videoUrl).equals(position + "")) {
                RequestBody formBody = new FormBody.Builder()
                        .add("userId", "0")
                        .add("userKey", "")
                        .add("macid", createChart(6) + "-" + getStringRandom(4) + "-" + getStringRandom(4) + "-" + createNumData(4) + "-" + createNumData(6) + getStringRandom(6))
                        .add("videoId", list.get(position).id + "")
                        .build();
                Call call = OkHttpClientManager.newInstance(TCVodPlayerActivity.this).newCall(new Request.Builder().url(ApiUrls.SMALL_PLAY_VIDEO_INO_HREF).post(formBody).build());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String str = response.body().string();
                        final SmallPlayVideoInfoModel entity = CommonUtil.parseJsonWithGson(str, SmallPlayVideoInfoModel.class);

                        ivAvatar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                OkHttpClientManager.parseRequestGirlBigVideoOne(TCVodPlayerActivity.this, ApiUrls.BIG_VIDEO_ONE_INFO_HREF, converDataHandler, entity.data.vid);

                            }
                        });

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TCVodPlayerActivity.this != null && !TCVodPlayerActivity.this.isDestroyed()) {
                                    Glide.with(TCVodPlayerActivity.this).load(entity.data.avatar.url).error(R.mipmap.default_head).into(ivAvatar);
                                }
                                PlayerInfo playerInfo = instantiatePlayerInfo(entity.data.url, position);
                                playerInfo.playerView = playView;
                                playerInfo.txVodPlayer.setPlayerView(playView);
                                playerInfo.txVodPlayer.startPlay(playerInfo.playURL);
                                if (vipplayerInfoList.size() < 8 && watchDataSave.getDataList().size() < 8) {
                                    vipplayerInfoList.add(playerInfo.playURL);
                                    WatchMessageEvent watchMessageEvent = new WatchMessageEvent(vipplayerInfoList);
                                    EventBus.getDefault().post(watchMessageEvent);
                                }
                            }
                        });

                    }
                });
            } else {
                if (TCVodPlayerActivity.this != null && !TCVodPlayerActivity.this.isDestroyed()) {
                    Glide.with(TCVodPlayerActivity.this).load(map.get("avatar")).error(R.mipmap.default_head).into(ivAvatar);
                }
                PlayerInfo playerInfo = instantiatePlayerInfo(videoUrl, position);
                playerInfo.playerView = playView;
                playerInfo.txVodPlayer.setPlayerView(playView);
                playerInfo.txVodPlayer.startPlay(playerInfo.playURL);
                if (vipplayerInfoList.size() < 8 && watchDataSave.getDataList().size() < 8) {
                    vipplayerInfoList.add(playerInfo.playURL);
                    WatchMessageEvent watchMessageEvent = new WatchMessageEvent(vipplayerInfoList);
                    EventBus.getDefault().post(watchMessageEvent);
                }
                ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        OkHttpClientManager.parseRequestGirlBigVideoOne(TCVodPlayerActivity.this, ApiUrls.BIG_VIDEO_ONE_INFO_HREF, converDataHandler, map.get("vid"));

                    }
                });
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            destroyPlayerInfo(position);
            container.removeView((View) object);
        }
    }

    void initPlayerSDK() {
        mVerticalViewPager.setCurrentItem(mInitTCLiveInfoPosition);
    }
    void initPhoneListener() {
        if (mPhoneListener == null)
            mPhoneListener = new TXPhoneStateListener(mTXVodPlayer);
        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
    void restartPlay() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onResume();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.resume();
        }
        isVipPrivate = vipsave.getVipData();
        isVipStorage = fileUtils.readInfo(Constants.VIP);
        PlayerInfo playerInfo = mPagerAdapter.findPlayerInfo(mCurrentPosition);
        judgeVip(playerInfo);
    }
    Dialog dialogs;
    private void judgeVip(PlayerInfo playerInfo) {
        if (playerInfo != null) {
            if ((isVipPrivate != null && (isVipPrivate.contains(new LoginDataSave(TCVodPlayerActivity.this).getLoginPhone() + "isVip"))
                    || (isVipStorage != null && (isVipStorage.contains(new LoginDataSave(TCVodPlayerActivity.this).getLoginPhone() + "isVip"))))) {
                playerInfo.txVodPlayer.resume();
                mTXVodPlayer = playerInfo.txVodPlayer;
                if(dialogs!=null){
                    dialogs.dismiss();
                }
            } else {
                long l = System.currentTimeMillis() - watchDataSave.getTimeData();
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);

                if (day > 0 && hour > 0) {
                    watchDataSave.clearWatchCountDataList();
                }
                if (watchDataSave.getDataList() == null || watchDataSave.getDataList().size() == 0) {
                    playerInfo.txVodPlayer.resume();
                    mTXVodPlayer = playerInfo.txVodPlayer;
                } else {
                    if (watchDataSave.getDataList().size() < 9 && watchDataSave.getDataList().contains(playerInfo.playURL)) {
                        playerInfo.txVodPlayer.resume();
                        mTXVodPlayer = playerInfo.txVodPlayer;

                    } else {
                        FragmentDialog.newInstance(false, "非VIP会员一天只能观看8个哦!", "成为VIP会员可以无限观看哦!", "成为永久会员", "取消", "", "", false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(Dialog dialog) {
                                dialogs=dialog;
                                dialog.dismiss();
                                LoginDataSave dataSave = new LoginDataSave(TCVodPlayerActivity.this);
                                if ("isLogin".equals(dataSave.getLoginData())) {
                                    startActivity(new Intent(TCVodPlayerActivity.this, BuyVipActivity.class));
                                } else {
                                    startActivity(new Intent(TCVodPlayerActivity.this, LoginActivity.class));
                                }


                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                dialog.dismiss();

                            }
                        }).show(getSupportFragmentManager(), "");
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onPause();
        }
        if (mTXVodPlayer != null) {
            mTXVodPlayer.pause();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
            mTXCloudVideoView = null;
        }
        stopPlay(true);
        mTXVodPlayer = null;

        if (mPhoneListener != null) {
            TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
            mPhoneListener = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.stopPlay(clearLastFrame);
        }
    }

    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {
        findViewById(R.id.player_iv_cover_bkg).setVisibility(View.GONE);
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            int width = param.getInt(TXLiveConstants.EVT_PARAM1);
            int height = param.getInt(TXLiveConstants.EVT_PARAM2);
            if (width > height) {
                player.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
            } else {
                player.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
            }
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            restartPlay();
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 视频I帧到达，开始播放

            PlayerInfo playerInfo = mPagerAdapter.findPlayerInfo(player);
            if (playerInfo != null) {
                playerInfo.isBegin = true;
            }
            if (mTXVodPlayer == player) {
                mIvCover.setVisibility(View.GONE);
            }
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {
            if (mTXVodPlayer == player) {
                mTXVodPlayer.resume();
            }
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            PlayerInfo playerInfo = mPagerAdapter.findPlayerInfo(player);
            if (playerInfo != null && playerInfo.isBegin) {
                mIvCover.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {}

    PhoneStateListener mPhoneListener = null;

    static class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TXVodPlayer> mPlayer;
        public TXPhoneStateListener(TXVodPlayer player) {
            mPlayer = new WeakReference<>(player);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TXVodPlayer player = mPlayer.get();
            switch (state) {
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (player != null) player.setMute(true);
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (player != null) player.setMute(true);
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (player != null) player.setMute(false);
                    break;
            }
        }
    }

    public void getData(final String categoryId, String startPage) {
        if (categoryId == null) {
            OkHttpClientManager.parseRequestGirlSmallVideoList(this, ApiUrls.COMMON_VIDEO_SMALL_LIST_HREF, converDataHandler, Constants.LOADMORE, categoryId, startPage);
        } else {
            OkHttpClientManager.parseRequestGirlHomePage(this, ApiUrls.COMMON_VIDEO_LIST_HOMEPAGE_HREF, converDataHandler, Constants.LOADMORE, categoryId, startPage, "", "");
        }
    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.INFO) {
                final CommonVideoListModel result = CommonUtil.parseJsonWithGson((String) msg.obj, CommonVideoListModel.class);
                mCurrentPage = result.currPage;
                ArrayList<CommonVideoListModel.HomeVideoInfoData> girlDetail = result.data;
                if (result.maxCount > 0) {
                    mPagerAdapter.setDatas(girlDetail);
                    yrecycle_view_loadMore.setVisibility(View.GONE);
                    mPagerAdapter.notifyDataSetChanged();
                }
            } else {
                model = CommonUtil.parseJsonWithGson((String) msg.obj, BigVideoOneUserInfoModel.class);
                Intent intent = new Intent(TCVodPlayerActivity.this, GirlShowListInfoActivity.class);
                intent.putExtra(Constants.LIVE_INFO_USER_INFO, model);
                startActivity(intent);
            }
        }
    }

    public void back(View view) {
        this.finish();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void Event(WatchMessageEvent watchMessageEvent) {
        watchDataSave.setWatchDataList(watchMessageEvent.getMessage());
    }

}