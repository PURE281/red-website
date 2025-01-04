package com.jbb.library_common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.R;

import java.io.File;

public class GlideUtils {

    public static void display(Context mContext, String url, ImageView view) {
        Glide.with(mContext).load(url).into(view);
    }

    public static void display(Context mContext, String url, ImageView view, RequestOptions glideOptions) {
        Glide.with(mContext).load(url).apply(glideOptions).into(view);
    }


    public static void downLoad(String imageUrl, int width, int height) {
        Glide.with(BaseApplication.getContext())
                .load(imageUrl)
                .preload();
    }


    public static void saveBitmap(final String imgUrl, final ImageView imageView) {
        Glide.with(BaseApplication.getContext())
                .asBitmap()
                .load(imgUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        String targetFile = FileUtil.getTargetFile(BaseApplication.getContext(), imgUrl);
                        BitmapUtil.saveBitmap(resource,targetFile);
                        imageView.setTag(targetFile);
                        imageView.setImageBitmap(resource);
                    }
                });


    }
}
