package com.zan99.guaizhangmen;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zan99.guaizhangmen.Activity.MenuActivity;
import com.zan99.guaizhangmen.Model.BaseModel;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.Util.NetUtils;
import com.zan99.guaizhangmen.Util.SPUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private boolean continueCount = true;
    private boolean showAd = false;
    private int timeCount = 0;
    private int initTimeCount = 3;
    private ArrayList<HashMap<String, String>> adinfo = new ArrayList<>();
    private SPUtil spUtil;
    private String imglist = "";


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            countNum();
            if (continueCount) {
                handler.sendMessageDelayed(handler.obtainMessage(-1),1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        spUtil = new SPUtil(this, "main");
        if(NetUtils.isConnected(MainActivity.this)){
            getAd();
        }

        handler.sendMessageDelayed(handler.obtainMessage(-1),1000);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        float scale = metrics.density;
        BaseModel baseModel = new BaseModel(screenHeight,screenWidth ,scale);
    }

    private void getAd() {
        String lastId = spUtil.getString("tgid");

        if(lastId == null){
            spUtil.putValues(new SPUtil.ContentValue("tgid", "0"));
        }
//        if(isAppOnForeground()){
//            lastId="0";
//        }


        HttpUtil.get("admin.php/Systeminterface/boot_up_generalize?generalizeId="+lastId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                continueCount = false;
                if(statusCode == 200){
                    try {
                        JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf8"));
                        //if(jsonObject.getString("errcode").equals("0")){
                            JSONObject dataList = jsonObject.getJSONObject("dataList");
                            L.d(dataList.getString("isActivityOpen"));
                            if(dataList.getString("isActivityOpen").equals("yes")){
                                BaseModel.showMenActivity = true;
                            }

                            JSONArray jsonArray = dataList.getJSONArray("imgList");

                            if(jsonArray.size() != 0){
                                showAd = true;
                                spUtil.putValues(new SPUtil.ContentValue("tgid", dataList.getString("generalizeId")));
                                imglist = dataList.getString("imgList");
                            }
//                            else{
//                                toNextActivity();
//                                finish();
//                            }
                        //}else{
//                            toNextActivity();
//                            finish();
                        //}
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toNextActivity();
                finish();
            }
        });
    }

    private int countNum() {//数秒
        timeCount++;
        if(timeCount == 2){

            if(showAd){
                System.out.println("showAd");
                continueCount = false;
                showAd();
            }

            if (!NetUtils.isConnected(MainActivity.this)) {
                continueCount = false;
                toNextActivity();
                finish();
            }
        }

        if (timeCount == initTimeCount) {
            continueCount = false;
            toNextActivity();
            finish();
        }
        return timeCount;
    }

    private void showAd() {
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imglist",imglist);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }

    public void toNextActivity() {
        Intent intent = null;
        intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
}
