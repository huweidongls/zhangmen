package com.zan99.guaizhangmen.Model;

import android.content.Context;

/**
 * Created by Administrator on 2017/11/28.
 */

public class BaseModel {
    public static String GUAI_BOOK_IMG = "imageView2/2/w/";
    private static int height;
    private static int width;
    public static boolean showMenActivity = false;
    private static float scale;

    public BaseModel(int screenHeight, int screenWidth, float scale) {
        this.height = screenHeight;
        this.width = screenWidth;
        this.scale = scale;
        initImgSize();
    }

    public static float getScale() {
        return scale;
    }

    public static int getHeight() {
        return height;
    }

    public static int getWidth() {
        return width;
    }

    public static String getFullImg(String imgurl){
        return "http://"+imgurl;//+"?imageView2/2/w/"+(String.valueOf(width));//+"/h/"+(String.valueOf(height));
    }


    private void initImgSize() {

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
