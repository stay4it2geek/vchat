package com.act.videochat.activity;

import android.app.Service;
import android.content.Intent;
import android.graphics.Point;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.videochat.ApiUrls;
import com.act.videochat.Constants;
import com.act.videochat.R;
import com.act.videochat.adapter.CommonVideoListAdapter;
import com.act.videochat.bean.CommonVideoListModel;
import com.act.videochat.bean.SmallPlayVideoInfoModel;
import com.act.videochat.fragment.CommonVideoListFragment;
import com.act.videochat.manager.OkHttpClientManager;
import com.act.videochat.util.CommonUtil;
import com.act.videochat.util.TCUtils;
import com.act.videochat.view.YRecycleviewRefreshFootView;
import com.bumptech.glide.Glide;
import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.act.videochat.manager.OkHttpClientManager.createChart;
import static com.act.videochat.manager.OkHttpClientManager.createNumData;
import static com.act.videochat.manager.OkHttpClientManager.getStringRandom;

/**
 * Created by hans on 2017/12/5.
 */
public class TCVodPlayerActivity extends AppCompatActivity implements ITXVodPlayListener {
    private static final String TAG = "TCVodPlayerActivity";
    private VerticalViewPager mVerticalViewPager;
    private MyPagerAdapter mPagerAdapter;
    private TXCloudVideoView mTXCloudVideoView;
    private TextView mTvBack;

    private ImageView mIvCover;
    // 发布者id 、视频地址、 发布者名称、 头像URL、 封面URL
    private List<SmallPlayVideoInfoModel> mTCLiveInfoList;
    private int mInitTCLiveInfoPosition;
    private int mCurrentPosition;

    /**
     * SDK播放器以及配置
     */
    private TXVodPlayer mTXVodPlayer;
    private int mMaxCount;
    private int mCurrentPage;
    private String mCatagoryId;
    ArrayList<CommonVideoListModel.HomeVideoInfoData> details = new ArrayList<>();
    private YRecycleviewRefreshFootView loadfooterview;


    class PlayerInfo {
        public TXVodPlayer txVodPlayer;
        public String playURL;
        public boolean isBegin;
        public View playerView;
        public int pos;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initDatas();
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

    private void initPhoneListener() {
        if (mPhoneListener == null)
            mPhoneListener = new TXPhoneStateListener(mTXVodPlayer);
        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    private void initDatas() {
        Intent intent = getIntent();
        mTCLiveInfoList = (List<SmallPlayVideoInfoModel>) intent.getSerializableExtra(Constants.LIVE_INFO_LIST);
        mInitTCLiveInfoPosition = intent.getIntExtra(Constants.LIVE_INFO_POSITION, 0);
        mCurrentPage = intent.getIntExtra(Constants.LIVE_INFO_CURRENTPAGE, 0);
        mMaxCount = intent.getIntExtra(Constants.LIVE_INFO_MAXCOUNT, 0);
        mCatagoryId = intent.getStringExtra(Constants.LIVE_INFO_CATAGORY_ID);

    }

    private void initViews() {
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.player_cloud_view);
        mIvCover = (ImageView) findViewById(R.id.player_iv_cover);
        loadfooterview = (YRecycleviewRefreshFootView) findViewById(R.id.yrecycle_view_foot_load);

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
                TXLog.i(TAG, "mVerticalViewPager, onPageScrolled position = " + position);
//                mCurrentPosition = position;
            }

            @Override
            public void onPageSelected(int position) {
                TXLog.i(TAG, "mVerticalViewPager, onPageSelected position = " + position);
                mCurrentPosition = position;
                // 滑动界面，首先让之前的播放器暂停，并seek到0
                TXLog.i(TAG, "滑动后，让之前的播放器暂停，mTXVodPlayer = " + mTXVodPlayer);
                if (mTXVodPlayer != null) {
                    mTXVodPlayer.seek(0);
                    mTXVodPlayer.pause();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mVerticalViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                TXLog.i(TAG, "mVerticalViewPager, transformPage pisition = " + position + " mCurrentPosition" + mCurrentPosition);
                if (position != 0) {
                    return;
                }

                ViewGroup viewGroup = (ViewGroup) page;
                mIvCover = (ImageView) viewGroup.findViewById(R.id.player_iv_cover);
                mTXCloudVideoView = (TXCloudVideoView) viewGroup.findViewById(R.id.player_cloud_view);


                PlayerInfo playerInfo = mPagerAdapter.findPlayerInfo(mCurrentPosition);
                if (playerInfo != null) {
                    playerInfo.txVodPlayer.resume();
                    mTXVodPlayer = playerInfo.txVodPlayer;
                }
            }
        });

        mPagerAdapter = new MyPagerAdapter();
        mVerticalViewPager.setAdapter(mPagerAdapter);
    }

    class MyPagerAdapter extends PagerAdapter {

        ArrayList<PlayerInfo> playerInfoList = new ArrayList<>();


        protected PlayerInfo instantiatePlayerInfo(int position) {
            TXCLog.d(TAG, "instantiatePlayerInfo " + position);

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

            SmallPlayVideoInfoModel tcLiveInfo = mTCLiveInfoList.get(position);
            playerInfo.playURL = tcLiveInfo.data.url;
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

                TXCLog.d(TAG, "destroyPlayerInfo " + position);
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

        public void onDestroy() {
            for (PlayerInfo playerInfo : playerInfoList) {
                playerInfo.txVodPlayer.stopPlay(true);
            }
            playerInfoList.clear();
        }

        @Override
        public int getCount() {
            return mTCLiveInfoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TXCLog.i(TAG, "MyPagerAdapter instantiateItem, position = " + position);
            SmallPlayVideoInfoModel tcLiveInfo = mTCLiveInfoList.get(position);

            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.view_player_content, null);
            view.setId(position);
            // 封面
            ImageView coverImageView = (ImageView) view.findViewById(R.id.player_iv_cover);
            TCUtils.blurBgPic(TCVodPlayerActivity.this, coverImageView, tcLiveInfo.data.coverUrl, R.drawable.bg);
            // 头像
            CircleImageView ivAvatar = (CircleImageView) view.findViewById(R.id.player_civ_avatar);
            Glide.with(TCVodPlayerActivity.this).load(tcLiveInfo.data.avatar.url).error(R.mipmap.default_head).into(ivAvatar);

            // 获取此player
            TXCloudVideoView playView = (TXCloudVideoView) view.findViewById(R.id.player_cloud_view);
            PlayerInfo playerInfo = instantiatePlayerInfo(position);
            playerInfo.playerView = playView;
            playerInfo.txVodPlayer.setPlayerView(playView);
            playerInfo.txVodPlayer.startPlay(playerInfo.playURL);
            Log.e("playerInfo.playURL", playerInfo.playURL + "000");
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            TXCLog.i(TAG, "MyPagerAdapter destroyItem, position = " + position);

            destroyPlayerInfo(position);

            container.removeView((View) object);
        }
    }

    private void initPlayerSDK() {
        mVerticalViewPager.setCurrentItem(mInitTCLiveInfoPosition);
    }

    private void restartPlay() {
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
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.stopPlay(clearLastFrame);
        }
    }

    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {
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
                TXLog.i(TAG, "onPlayEvent, event I FRAME, player = " + player);
                mIvCover.setVisibility(View.GONE);
            }
        } else if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) {
            if (mTXVodPlayer == player) {
                TXLog.i(TAG, "onPlayEvent, event prepared, player = " + player);
                mTXVodPlayer.resume();
            }
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            PlayerInfo playerInfo = mPagerAdapter.findPlayerInfo(player);
            if (playerInfo != null && playerInfo.isBegin) {
                mIvCover.setVisibility(View.GONE);
                TXCLog.i(TAG, "onPlayEvent, event begin, cover remove");
            }
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {

    }


    /**
     * ==========================================来电监听==========================================
     */
    private PhoneStateListener mPhoneListener = null;

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




}
