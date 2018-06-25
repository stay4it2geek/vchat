package com.act.videochat.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.videochat.R;
import com.act.videochat.bean.BigVideoOneUserInfoModel;
import com.act.videochat.bean.CommonVideoListModel;
import com.act.videochat.util.TCUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoWorksListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int maxCount;
    LayoutInflater mLayoutInflater;
    Activity activity;
    BigVideoOneUserInfoModel headerInfo;
    ArrayList<CommonVideoListModel.HomeVideoInfoData> girlVideos;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public VideoWorksListAdapter(Activity activity, BigVideoOneUserInfoModel headerInfo, ArrayList<CommonVideoListModel.HomeVideoInfoData> girlVideos, int maxCount) {
        this.headerInfo = headerInfo;
        this.activity = activity;
        this.girlVideos = girlVideos;
        mLayoutInflater = LayoutInflater.from(activity);
        this.maxCount = maxCount;

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.girl_video_header_layout, parent, false));
        } else {
            return new Item2ViewHolder(mLayoutInflater.inflate(R.layout.item_video_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof Item1ViewHolder) {
            StaggeredGridLayoutManager.LayoutParams clp = (StaggeredGridLayoutManager.LayoutParams) ((Item1ViewHolder) holder).headerlayout.getLayoutParams();
            clp.setFullSpan(true);
            TCUtils.blurBgPic(activity, ((Item1ViewHolder) holder).backGroundImg, headerInfo.data.avatar.url, R.drawable.main_bkg);
            ((Item1ViewHolder) holder).nickName.setText(headerInfo.data.nickname);
            ((Item1ViewHolder) holder).topics.setText(headerInfo.data.topic);
            ((Item1ViewHolder) holder).videoCount.setText("共有" + maxCount + "个视频");

            Glide.with(activity).load(headerInfo.data.avatar.url).into(((Item1ViewHolder) holder).userImage);
            ((Item1ViewHolder) holder).tagLayout.removeAllViews();
            for (BigVideoOneUserInfoModel.Tag tag : headerInfo.data.tags) {
                TextView textView = new TextView(activity);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(5);
                drawable.setStroke(1, Color.parseColor("#cccccc"));
                drawable.setColor(Color.parseColor("#" + tag.color));
                textView.setBackgroundDrawable(drawable);
                textView.setText(tag.name);
                textView.setTextSize(13f);
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 105);
                params.setMargins(5, 5, 5, 5);//设置边距
                textView.setLayoutParams(params);
                ((Item1ViewHolder) holder).tagLayout.addView(textView);

            }

        } else if (holder instanceof Item2ViewHolder) {
            if(activity!=null && !activity.isDestroyed()) {
//                ((Item2ViewHolder) holder).videoCover.setLayoutParams(new LinearLayout.LayoutParams((screenWidth / 3), (screenWidth /2) + 135));
                Glide.with(activity).load(girlVideos.get(position - 1).cover).error(R.drawable.error_img).into(((Item2ViewHolder) holder).videoCover);
            }
            ((Item2ViewHolder) holder).videoCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(view, position - 1);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return girlVideos.size() + 1;
    }

    class Item1ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout headerlayout;
        private TextView nickName;
        private TextView topics;
        private CircleImageView userImage;
        private ImageView backGroundImg;
        private LinearLayout tagLayout;
        private TextView videoCount;

        public Item1ViewHolder(View view) {
            super(view);

            headerlayout = (LinearLayout) view.findViewById(R.id.headerlayout);
            nickName = (TextView) view.findViewById(R.id.nickName);
            topics = (TextView) view.findViewById(R.id.topics);
            tagLayout = (LinearLayout) view.findViewById(R.id.tagLayout);
            userImage = (CircleImageView) view.findViewById(R.id.userImage);
            backGroundImg = (ImageView) view.findViewById(R.id.backGroundImg);
            videoCount = (TextView) view.findViewById(R.id.videoCount);

        }
    }

    class Item2ViewHolder extends RecyclerView.ViewHolder {

        private ImageView videoCover;

        public Item2ViewHolder(View view) {
            super(view);
            videoCover = (ImageView) view.findViewById(R.id.videoCover);
        }
    }
}