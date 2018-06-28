package com.act.videochat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.act.videochat.R;
import com.act.videochat.bean.CommonVideoListModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CommonVideoListAdapter extends RecyclerView.Adapter<CommonVideoListAdapter.MyViewHolder> {

    private int screenWidth;
    private Context mContext;
    private ArrayList<CommonVideoListModel.HomeVideoInfoData> datas;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position,ImageView imageView);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public CommonVideoListAdapter(Context context, int screenWidth) {
        mContext = context;
        this.screenWidth = screenWidth;
    }

    public void setDatas(ArrayList<CommonVideoListModel.HomeVideoInfoData> datas) {
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
        holder.photoImg.setLayoutParams(new FrameLayout.LayoutParams((screenWidth / 2), (screenWidth / 2) + 175));
        if (mContext != null) {
            Glide.with(mContext).load(datas != null && datas.size() > 0 ? datas.get(position).cover : "").placeholder(R.drawable.placehoder_img).error(R.drawable.error_img).into(holder.photoImg);//加载网络图片
        }
        holder.photoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position,holder.photoImg);
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas != null && datas.size() > 0 ? datas.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView photoImg;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
        }
    }

}
