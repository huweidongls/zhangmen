package com.zan99.guaizhangmen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.MenuActivity;
import com.zan99.guaizhangmen.Activity.WebActivity;
import com.zan99.guaizhangmen.Model.BaseModel;
import com.zan99.guaizhangmen.Util.NetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/5.
 */

public class WelcomeActivity extends AppCompatActivity{
    private ViewPager viewPager;
    private List<ImageView> imageViews;
    private int imagesize=0;
    LinearLayout linearLayout;
    private int timeCount = 0;
    private boolean continueCount = true;
    private boolean goinCount=true;
    private int endTimenum=6;
    private TextView textgoin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Bundle bundle = getIntent().getExtras();
        String imgList = bundle.getString("imglist");
        JSONArray jsonArray = JSON.parseArray(imgList);
        linearLayout = (LinearLayout) findViewById(R.id.point);
        textgoin= (TextView) findViewById(R.id.textgoin);
        textgoin.setText("跳过"+endTimenum+"s");
        imageViews = new ArrayList<ImageView>();
        for(int i=0;i<jsonArray.size();i++){
            final JSONObject tjs = jsonArray.getJSONObject(i);
            ImageView imageView = new ImageView(WelcomeActivity.this);
//            Picasso.with(this).load(BaseModel.getFullImg(tjs.getString("img"))).into(imageView);
            Picasso.with(this).load("http://"+tjs.getString("img")).into(imageView);
            imageView.setTag(R.id.load_url, tjs.getString("imgsrc"));
            imageView.setTag(R.id.url_title, tjs.getString("title"));
            imageView.setScaleType(ImageView.ScaleType. CENTER_CROP);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    continueCount=false;
                    Intent intent = new Intent(WelcomeActivity.this, WebActivity.class);
                    intent.putExtra("url",tjs.getString("imgsrc"));
                    intent.putExtra("title", tjs.getString("title"));
                    intent.putExtra("type", "tuiguang");
                    startActivity(intent);
                }
            });
            imageViews.add(imageView);
        }
        imagesize=imageViews.size();

        initWelcome();
        initPoint(0);

        handler.sendMessageDelayed(handler.obtainMessage(-1),1000);
    }

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


    private int countNum() {//数秒
        timeCount++;

        textgoin.setText("跳过"+endTimenum+"s");
        endTimenum--;
        if(timeCount == 6){
            goinCount=false;
            continueCount=false;
            Intent intent = new Intent(WelcomeActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        }

        return timeCount;
    }


    private void initWelcome() {
        viewPager = (ViewPager) findViewById(R.id.ad_img);
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return imageViews.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(imageViews.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(imageViews.get(position));

                return imageViews.get(position);
            }
        };

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                initPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        LinearLayout start_up_pass = (LinearLayout) findViewById(R.id.start_up_pass);
        start_up_pass.setVisibility(View.VISIBLE);
        start_up_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(goinCount){
                    continueCount=false;
                    Intent intent = new Intent(WelcomeActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }


    /**
     * 初始化底部小点
     */
    private void initPoint(int position){


        linearLayout.removeAllViews();

        //循环取得小点图片
        for (int i = 0; i < imagesize; i++) {
            ImageView iv=new ImageView(this);
            if(i==position){
                iv.setImageResource(R.drawable.ic_dot_focused);
            }else{
                iv.setImageResource(R.drawable.ic_dot_normal);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 10, 0);
            iv.setLayoutParams(lp);
            linearLayout.addView(iv);
        }


    }



}
