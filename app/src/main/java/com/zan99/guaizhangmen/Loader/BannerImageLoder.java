package com.zan99.guaizhangmen.Loader;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by Administrator on 2017/11/27.
 */

public class BannerImageLoder extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Picasso.with(context).load(path.toString()).into(imageView);
    }

    @Override
    public ImageView createImageView(Context context) {
        return super.createImageView(context);
    }

}
