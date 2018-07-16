package com.act.videochat.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.act.videochat.R;
import com.act.videochat.bean.CommonChatListModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class CommonChatListAdapter extends RecyclerView.Adapter<CommonChatListAdapter.MyViewHolder> {

    private int screenWidth;
    private Context mContext;

    private List<CommonChatListModel.HomeChatInfoData> datas;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position, ImageView imageView);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public CommonChatListAdapter(Context context, int screenWidth) {
        mContext = context;
        this.screenWidth = screenWidth;


    }

    public void setDatas(List<CommonChatListModel.HomeChatInfoData> datas) {
        this.datas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_cover_img_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.photoImg.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, screenWidth));
        if (mContext != null) {
            Glide.with(mContext).load(datas != null && datas.size() > 0 ? datas.get(position).avatar.url : "").placeholder(R.drawable.placehoder_img).error(R.drawable.error_img).into(holder.photoImg);//加载网络图片
        }
        holder.photoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position, holder.photoImg);
            }
        });
        int startLevel = Integer.parseInt(datas.get(position).level != null && !datas.get(position).level.equals("") ? datas.get(position).level : "0");
        holder.star_Layout.removeAllViewsInLayout();
        for (int index = 0; index < startLevel; index++) {
            TextView textView = new TextView(mContext);
            textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.star));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(37, 37);
            params.setMargins(3, 2, 6,3);//设置边距
            params.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(params);
            holder.star_Layout.addView(textView);
        }

        int online = datas.get(position).online != null ? datas.get(position).online : 0;
        switch (online) {
            case 0:
                holder.onlinestatus.setText("离线");
                holder.online_dot.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_dot_offline_shape));
                break;

            case 1:
                holder.onlinestatus.setText("在线");
                holder.online_dot.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_dot_online_shape));
                break;

            case 2:
                holder.onlinestatus.setText("在聊");
                holder.online_dot.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_dot_talking_shape));
                break;

            case 3:
                holder.onlinestatus.setText("活跃");
                holder.online_dot.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_dot_active_shape));
                break;

            case 4:
                holder.onlinestatus.setText("勿扰");
                holder.online_dot.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_dot_nodisturb_shape));
                break;
        }
        holder.nickName.setText(datas.get(position).nickname + "");
        holder.vcoinPerMinute.setText((datas.get(position).vcoinPerMinute != null && !datas.get(position).vcoinPerMinute.equals("") ? datas.get(position).vcoinPerMinute : "0"));
        holder.topicContent.setText(datas.get(position).topic);
    }

    @Override
    public int getItemCount() {
        return datas != null && datas.size() > 0 ? datas.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout starnickLayout;
        private TextView online_dot;
        private TextView onlinestatus;
        private TextView vcoinPerMinute;
        private RelativeLayout onlinelayout;
        private TextView topicContent;
        private TextView nickName;
        private LinearLayout star_Layout;
        private ImageView photoImg;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            nickName = (TextView) view.findViewById(R.id.nickName);
            online_dot = (TextView) view.findViewById(R.id.online_dot);
            onlinestatus = (TextView) view.findViewById(R.id.onlinestatus);
            topicContent = (TextView) view.findViewById(R.id.topicContent);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
            star_Layout = (LinearLayout) view.findViewById(R.id.star_Layout);
            vcoinPerMinute = (TextView) view.findViewById(R.id.vcoinPerMinute);
            onlinelayout = (RelativeLayout) view.findViewById(R.id.onlinelayout);
            starnickLayout = (RelativeLayout) view.findViewById(R.id.starnick_Layout);
            star_Layout.setVisibility(View.VISIBLE);
            view.findViewById(R.id.vcoinPerMinuteAdd).setVisibility(View.VISIBLE);
            nickName.setVisibility(View.VISIBLE);
            topicContent.setVisibility(View.VISIBLE);
            vcoinPerMinute.setVisibility(View.VISIBLE);
            onlinelayout.setVisibility(View.VISIBLE);
            starnickLayout.setVisibility(View.VISIBLE);
        }
    }

}
