package com.act.videochat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.act.videochat.R;
import com.act.videochat.bean.SmallPlayVideoInfoModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CommonVideoListAdapter extends RecyclerView.Adapter<CommonVideoListAdapter.MyViewHolder> {

    private int screenWidth;
    private Context mContext;
    private ArrayList<SmallPlayVideoInfoModel> datas;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public CommonVideoListAdapter(Context context,int screenWidth) {
        mContext = context;
        this.screenWidth = screenWidth;
    }
    public  void setDatas(ArrayList<SmallPlayVideoInfoModel> datas){
        this.datas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_cover_img_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.photoImg.setLayoutParams(new LinearLayout.LayoutParams((screenWidth / 2), (screenWidth / 2) + 175));
        Glide.with(mContext).load(datas!=null&&datas.size()>0?datas.get(position).data.coverUrl:"").placeholder(R.drawable.placehoder_img).error(R.drawable.error_img).into(holder.photoImg);//加载网络图片
        holder.photoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas!=null&&datas.size()>0?datas.size():0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView photoImg;

        public MyViewHolder(View view) {
            super(view);
            photoImg = (ImageView) view.findViewById(R.id.photoImg);
        }
    }

}
