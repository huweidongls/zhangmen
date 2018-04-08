package com.zan99.guaizhangmen.Util;

/**
 * Created by Administrator on 2017/11/28.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/10/25.
 */

public class BitMap implements Runnable {

    public String urlpath=null;
    public static Bitmap map = null;
    private Handler handler;

    public BitMap(String urlpath, Handler hd){
        this.urlpath=urlpath;
        handler = hd;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        URL url=null;
        try {
            url = new URL(urlpath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setUseCaches(true);
            //conn.connect();
            System.out.println("dd:开始");
            int dd=conn.getResponseCode();
            System.out.println("dd:"+dd);

            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);

            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
            // imageView.setImageBitmap(map);

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            map = null;
        }
    }

    public static Bitmap getimgBitmap(){
        return map;
    }

}