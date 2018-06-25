package com.act.videochat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.youth.banner.loader.ImageLoader;

public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, final ImageView imageView) {

            Glide.with(context).load((String) path).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    imageView.setBackgroundDrawable(new BitmapDrawable(resource));
                }
                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    imageView.setBackgroundDrawable(placeholder);
                }
            });
        }
}
