package com.act.videochat.adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.act.videochat.R;
import com.act.videochat.bean.ChatGirlInfoComment;
import com.act.videochat.bean.ChatGirlInfoDetail;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class GirlInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ChatGirlInfoDetail detail;
    ArrayList<ChatGirlInfoComment.CommentTagList> commentsData;

    public GirlInfoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public void setFigurTagAndCommentTagsData(ChatGirlInfoDetail detail) {
        this.detail = detail;

    }

    public void setCommentsData(ArrayList<ChatGirlInfoComment.CommentTagList> commentsData) {
        this.commentsData = commentsData;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            return new FigureAndCommentTagsHolder(LayoutInflater.from(context).inflate(R.layout.item_figuretag, parent, false));
        } else {
            return new UserCommentHolder(LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FigureAndCommentTagsHolder) {
            if (detail != null && detail.data != null) {
                ((FigureAndCommentTagsHolder) holder).topic.setText(detail.data.topic + "");
                ((FigureAndCommentTagsHolder) holder).lastTime.setText(detail.data.lastTime + "");
                ((FigureAndCommentTagsHolder) holder).connectRate.setText(detail.data.connectRate + "%");
                ((FigureAndCommentTagsHolder) holder).weight.setText(detail.data.weight + "kg");
                ((FigureAndCommentTagsHolder) holder).height.setText(detail.data.height + "cm");
                ((FigureAndCommentTagsHolder) holder).city.setText(detail.data.city + "");
                ((FigureAndCommentTagsHolder) holder).like.setText(detail.data.like + "");
                ((FigureAndCommentTagsHolder) holder).dislike.setText(detail.data.dislike + "");
                ((FigureAndCommentTagsHolder) holder).constellation.setText(detail.data.constellation + "");

                ((FigureAndCommentTagsHolder) holder).figureTagsLayout.removeAllViewsInLayout();
                for (ChatGirlInfoDetail.GirlDetailInfo.FigureTags tag : detail.data.figureTags) {
                    TextView textView = new TextView(context);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setCornerRadius(50);
                    drawable.setStroke(1, Color.parseColor("#cccccc"));
                    drawable.setColor(Color.parseColor("#" + tag.color));
                    textView.setBackgroundDrawable(drawable);
                    textView.setText(tag.name);
                    textView.setTextSize(12f);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(168, 75);
                    params.setMargins(10, 28, 5, 5);//设置边距
                    textView.setLayoutParams(params);
                    ((FigureAndCommentTagsHolder) holder).figureTagsLayout.addView(textView);

                }
                ((FigureAndCommentTagsHolder) holder).impressLayout.removeAllViewsInLayout();
                for (ChatGirlInfoDetail.GirlDetailInfo.CommentTags tag : detail.data.commentTags) {
                    TextView textView = new TextView(context);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setCornerRadius(50);
                    drawable.setStroke(1, Color.parseColor("#cccccc"));
                    drawable.setColor(Color.parseColor("#" + tag.color));
                    textView.setBackgroundDrawable(drawable);
                    textView.setText(tag.name);
                    textView.setTextSize(12f);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(168, 75);
                    params.setMargins(10, 28, 5, 5);//设置边距
                    textView.setLayoutParams(params);
                    ((FigureAndCommentTagsHolder) holder).impressLayout.addView(textView);

                }
            }
        } else if (holder instanceof UserCommentHolder) {
            if (commentsData != null && commentsData != null) {
                Glide.with(context).load(commentsData.get(position - 1).avatar.url).placeholder(R.drawable.placehoder_img).into(((UserCommentHolder) holder).iv_avatar);
                ((UserCommentHolder) holder).commentTagsLayout.removeAllViewsInLayout();
                for (int index = 0; index < commentsData.get(position - 1).taglist.size(); index++) {
                    ChatGirlInfoComment.CommentTagList.Taglist tag = commentsData.get(position - 1).taglist.get(index);
                    TextView textView = new TextView(context);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setCornerRadius(50);
                    drawable.setStroke(1, Color.parseColor("#cccccc"));
                    drawable.setColor(Color.parseColor("#" + tag.color));
                    textView.setBackgroundDrawable(drawable);
                    textView.setText(tag.name);
                    textView.setTextSize(12f);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(168, 75);
                    params.setMargins(10, 28, 5, 5);//设置边距
                    textView.setLayoutParams(params);


                    ((UserCommentHolder) holder).name.setText(commentsData.get(position - 1).nickname + "");
                    if (index > 2) {
                        break;
                    }
                    ((UserCommentHolder) holder).commentTagsLayout.addView(textView);
                }

            }
        }

    }

    @Override
    public int getItemCount() {

        return commentsData != null ? commentsData.size() + 1 : 1;
    }

    public class FigureAndCommentTagsHolder extends RecyclerView.ViewHolder {

        TextView constellation;
        TextView topic;
        LinearLayout figureTagsLayout;
        LinearLayout impressLayout;
        TextView lastTime;
        TextView connectRate;
        TextView weight;
        TextView height;
        TextView city;
        TextView like;
        TextView dislike;

        public FigureAndCommentTagsHolder(View itemView) {
            super(itemView);
            topic = (TextView) itemView.findViewById(R.id.topic);
            figureTagsLayout = (LinearLayout) itemView.findViewById(R.id.figureTagsLayout);
            impressLayout = (LinearLayout) itemView.findViewById(R.id.impressLayout);
            lastTime = (TextView) itemView.findViewById(R.id.lastTime);
            connectRate = (TextView) itemView.findViewById(R.id.connectRate);
            weight = (TextView) itemView.findViewById(R.id.weight);
            height = (TextView) itemView.findViewById(R.id.height);
            city = (TextView) itemView.findViewById(R.id.city);
            like = (TextView) itemView.findViewById(R.id.like);
            dislike = (TextView) itemView.findViewById(R.id.dislike);
            constellation = (TextView) itemView.findViewById(R.id.constellation);
        }


    }


    public class UserCommentHolder extends RecyclerView.ViewHolder {

        TextView name;
        CircleImageView iv_avatar;
        LinearLayout commentTagsLayout;


        public UserCommentHolder(View itemView) {
            super(itemView);
            iv_avatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
            name = (TextView) itemView.findViewById(R.id.name);
            commentTagsLayout = (LinearLayout) itemView.findViewById(R.id.commentTagsLayout);
        }
    }
}
