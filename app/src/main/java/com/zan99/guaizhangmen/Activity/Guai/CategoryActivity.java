package com.zan99.guaizhangmen.Activity.Guai;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zan99.guaizhangmen.Adapter.CategoryViewAdapter;
import com.zan99.guaizhangmen.Adapter.CategoryViewPagerListAdapther;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Fragment.GuaiFragment;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Widget.MagicTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2017/11/27.
 */

public class CategoryActivity  extends AppCompatActivity {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.back_left)
    Button back_left;
    @BindView(R.id.LinearLayout1)
    LinearLayout LinearLayout1;

    private JSONArray categoryList;
    List<String> mTitleDataList = new ArrayList<String>();
    private int category_position = 0;
    private boolean hasXangmu = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);

        this.categoryList = GuaiFragment.categoryList;
        for(int i = 0;i<categoryList.size();i++){
            JSONObject tjson = categoryList.getJSONObject(i);
            mTitleDataList.add(tjson.getString("name"));
        }

        category_position = getIntent().getIntExtra("position",0);
//isXMOpen
        RequestParams param = new RequestParams();
        param.put("isXMOpen",0);
        HttpUtil.get("author.php/Nexts/getAllCategoryAndBooksList",param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf-8"));
                    if(jsonObject.getString("errcode").equals("0")){
                        initView(jsonObject.getJSONArray("dataList"));
                        if(jsonObject.getString("hasXiangMu").equals("1")){
                            hasXangmu = true;
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg("author.php/Nexts/listarray","");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg("author.php/Nexts/listarray","");
            }
        });


    }

    private void initView(JSONArray dataList) {
        LayoutInflater inflater = getLayoutInflater();
        final List<View> viewPagerList = new ArrayList<View>();

        if(hasXangmu){
            // 项目单独适配
            for(int i=0;i<dataList.size()-1;i++){

                JSONObject tjson = dataList.getJSONObject(i);
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
                JSONArray ttjson = tjson.getJSONArray("dataList");
                // 获取子节点
                if(!ttjson.isEmpty()) {
                    // 遍历子节点下的书籍信息
                    for (int k = 0; k < ttjson.size(); k++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject tttjson = ttjson.getJSONObject(k);
                        map.put("books_name", tttjson.getString("booksName"));
                        map.put("books_id", tttjson.getString("booksId"));
                        map.put("books_img", "http://" + tttjson.getString("booksImg"));
                        map.put("books_time", tttjson.getString("date"));
                        map.put("books_synopsis", tttjson.getString("booksSynopsis"));
                        map.put("auther_name", tttjson.getString("autherName"));
                        arrayList.add(map);
                    }
                }

                // viewpager对应页面
                View tview = inflater.inflate(R.layout.layout_category_view_pager_item, null);

                // viewpager 内listview适配
                ListView tlistview = (ListView) tview.findViewById(R.id.list_view);

                tlistview.setAdapter(new CategoryViewPagerListAdapther(CategoryActivity.this,arrayList, false));

                tlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String books_id = view.getTag(R.id.books_id).toString();
                        Intent intent = new Intent(CategoryActivity.this, BookActivity.class);
                        intent.putExtra("books_id",books_id);
                        startActivity(intent);
                    }
                });
                // 加入viewpager
                viewPagerList.add(tview);
            }

            // 适配招商项目
            JSONObject tjson = dataList.getJSONObject(dataList.size()-1);
            JSONArray ttjson = tjson.getJSONArray("dataList");
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
            for (int k = 0; k < ttjson.size(); k++) {
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject tttjson = ttjson.getJSONObject(k);
                map.put("books_name", tttjson.getString("booksName"));
                map.put("books_id", tttjson.getString("booksId"));
                map.put("books_img", "http://" + tttjson.getString("booksImg"));
                map.put("books_time", tttjson.getString("date"));
                map.put("books_synopsis", tttjson.getString("booksSynopsis"));
                map.put("auther_name", tttjson.getString("autherName"));
                arrayList.add(map);
            }

            // viewpager对应页面
            View tview = inflater.inflate(R.layout.layout_category_view_pager_item, null);

            // viewpager 内listview适配
            ListView tlistview = (ListView) tview.findViewById(R.id.list_view);

            tlistview.setAdapter(new CategoryViewPagerListAdapther(CategoryActivity.this,arrayList, true));

            tlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String books_id = view.getTag(R.id.books_id).toString();
                    Intent intent = new Intent(CategoryActivity.this, BookActivity.class);
                    intent.putExtra("books_id",books_id);
                    startActivity(intent);
                }
            });
            // 加入viewpager
            viewPagerList.add(tview);

        }else{
            // 项目单独适配
            for(int i=0;i<dataList.size();i++){

                JSONObject tjson = dataList.getJSONObject(i);
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
                JSONArray ttjson = tjson.getJSONArray("dataList");
                // 获取子节点
                if(!ttjson.isEmpty()) {
                    // 遍历子节点下的书籍信息
                    for (int k = 0; k < ttjson.size(); k++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject tttjson = ttjson.getJSONObject(k);
                        map.put("books_name", tttjson.getString("booksName"));
                        map.put("books_id", tttjson.getString("booksId"));
                        map.put("books_img", "http://" + tttjson.getString("booksImg"));
                        map.put("books_time", tttjson.getString("date"));
                        map.put("books_synopsis", tttjson.getString("booksSynopsis"));
                        map.put("auther_name", tttjson.getString("autherName"));
                        arrayList.add(map);
                    }
                }

                // viewpager对应页面
                View tview = inflater.inflate(R.layout.layout_category_view_pager_item, null);

                // viewpager 内listview适配
                ListView tlistview = (ListView) tview.findViewById(R.id.list_view);

                tlistview.setAdapter(new CategoryViewPagerListAdapther(CategoryActivity.this,arrayList, false));

                tlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String books_id = view.getTag(R.id.books_id).toString();
                        Intent intent = new Intent(CategoryActivity.this, BookActivity.class);
                        intent.putExtra("books_id",books_id);
                        startActivity(intent);
                    }
                });
                // 加入viewpager
                viewPagerList.add(tview);
            }
        }

        viewPager.setAdapter(new CategoryViewAdapter(viewPagerList));
        final MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);

        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleDataList == null ? 0 : mTitleDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                MagicTitleView magicTitleView = new MagicTitleView(context);
                magicTitleView.setText(mTitleDataList.get(index));
                magicTitleView.setNormalColor(Color.parseColor("#505050"));
                magicTitleView.setSelectedColor(Color.parseColor("#C71521"));
                magicTitleView.setTextSize(18);
                magicTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return magicTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.parseColor("#C71521"));
                return indicator;
            }
        });

        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
//        commonNavigator.setFocusable(category_position);

        viewPager.setCurrentItem(category_position);
    }

    public void backFinish(View v){
        new Thread () {
            public void run () {
                try {
                    Instrumentation inst= new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent. KEYCODE_BACK);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
//        finish();
    }

    @butterknife.OnClick({R.id.back_left,R.id.LinearLayout1})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_left:
            case R.id.LinearLayout1:
                finish();
                break;
        }


    }


}
