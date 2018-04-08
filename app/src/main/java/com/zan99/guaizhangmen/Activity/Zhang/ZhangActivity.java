package com.zan99.guaizhangmen.Activity.Zhang;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.header.FlyRefreshHeader;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.TemplateActivity;
import com.zan99.guaizhangmen.Adapter.ZhangDakaAdapter;
import com.zan99.guaizhangmen.Adapter.ZhangFangtanAdapter;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.Util.ToastUtil;
import com.zan99.guaizhangmen.Widget.CustomerGridView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;


public class ZhangActivity extends AppCompatActivity {

    private ZhangFangtanAdapter adapter;
    private static int page = 1;
    private ArrayList<HashMap<String, String>> fangtanList = new ArrayList<HashMap<String, String>>();
    private LayoutInflater zInflater;
    private RelativeLayout dakaView;
    private boolean isRefresh = false;
    private boolean isCreate = true;
    private String template_type;

    @BindView(R.id.wifi404)
    LinearLayout wifi404;
    @BindView(R.id.fangtan)
    ListView fangtanView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private ArrayList<HashMap<String, String>> dakaList = new ArrayList<HashMap<String, String>>();
    private ZhangDakaAdapter zhangDakaAdapter;
    private Boolean allowload=true;
    public static int widthone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhang);
        ButterKnife.bind(this);
        title.setText(R.string.zhang);
        zInflater = LayoutInflater.from(this);


        DisplayMetrics dm =getResources().getDisplayMetrics();
        int widhtdm=dm.widthPixels; // 屏幕宽度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int screenWidth = (int) (widhtdm/density);//屏幕宽度(dp)

        widthone=(screenWidth-50)/4;

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onStart() {
        super.onStart();

        fangtanList.clear();
        //dakaList.clear();
        page = 1;
        initView();
        loadAll();
    }

    private void initView() {

        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        refreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

                if(allowload){
                    allowload=false;
                    fangtanList.clear();
                    dakaList.clear();
                    page = 1;
                    loadAll();

                }


            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {

                page++;
                loadAll();

                refreshlayout.finishLoadmore();

            }
        });

        dakaView = (RelativeLayout) View.inflate(this, R.layout.layout_zhang_daka, null);

        zhangDakaAdapter = new ZhangDakaAdapter(ZhangActivity.this,dakaList);

        RecyclerView dakaRecyclerView = dakaView.findViewById(R.id.daka);


//        LinearLayout dkll= dakaView.findViewById(R.id.dkll);
        LinearLayoutManager dakallm = new LinearLayoutManager(this);

        dakallm.setOrientation(LinearLayoutManager.HORIZONTAL);
        dakaRecyclerView.setLayoutManager(dakallm);

        dakaRecyclerView.setAdapter(null);
        dakaRecyclerView.setAdapter(zhangDakaAdapter);

        zhangDakaAdapter.setOnItemClickListener(new ZhangDakaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String author_id) {
                Intent intent = new Intent();
                intent.setClass(ZhangActivity.this, TemplateActivity.class);
                intent.putExtra("type","jiangshi");
                intent.putExtra("id",author_id);
                intent.putExtra("title",author_id);
                startActivity(intent);
            }
        });


        if(fangtanView.getHeaderViewsCount()==0){
            fangtanView.addHeaderView(dakaView);
        }
        adapter = new ZhangFangtanAdapter(ZhangActivity.this, fangtanList);

        fangtanView.setAdapter(adapter);
        fangtanView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(ZhangActivity.this, TemplateActivity.class);
                intent.putExtra("type","fangtan");
                L.d(view.getTag(R.id.fangtan_id).toString());
                intent.putExtra("id",view.getTag(R.id.fangtan_id).toString());
                startActivity(intent);
            }
        });



    }

    @butterknife.OnClick({R.id.wifi404})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wifi404:
                wifi404.setVisibility(View.GONE);
                loadAll();
                break;
        }
    }

    private void loadAll() {
        wifi404.setVisibility(View.GONE);
        RequestParams params = new RequestParams();
        if(MenModel.member_id.equals("")){
            MenModel.member_id = "0";
        }
        params.add("member_id", MenModel.member_id);

        params.add("page", String.valueOf(page));


        HttpUtil.post("admin.php/Systeminterface/interview_list/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if(statusCode==200){

                        JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf-8"));

                        if(jsonObject.getString("errcode").equals("0")){
                            initFangtanView(jsonObject.getJSONObject("dataList"));
                        }

                        allowload=true;
                    }
                } catch (UnsupportedEncodingException e) {
                    showNetError();
                    e.printStackTrace();
                    //BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/interview_list/",params.toString());
                }
                refreshLayout.finishRefresh();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showNetError();
                //BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/interview_list/",params.toString());
            }
        });
    }

    private void initDakaView(JSONArray dakaArray) {
        for(int i =0;i<dakaArray.size();i++){
            JSONObject tj = dakaArray.getJSONObject(i);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("daka_img","http://" + tj.getString("headImg"));
            map.put("author_id",tj.getString("authorId"));
            dakaList.add(map);
        }
        zhangDakaAdapter.notifyDataSetChanged();
    }

    private void initFangtanView(JSONObject jsonObject) {

        if(isCreate || isRefresh){
            initDakaView(jsonObject.getJSONArray("clockIn"));
            isCreate = false;
            isRefresh = false;
            refreshLayout.setEnableLoadmore(true);
        }

        JSONArray fangtanArray = jsonObject.getJSONArray("list");
        if(fangtanArray.isEmpty()){
            refreshLayout.setEnableLoadmore(false);
            ToastUtil.show(this,"已经到底啦", Toast.LENGTH_LONG);
            return;
        }
        for(int i = 0;i<fangtanArray.size();i++){
            JSONObject tj = fangtanArray.getJSONObject(i);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("profile",tj.getString("profile"));
            map.put("cover","http://"+tj.getString("cover"));
            map.put("time",tj.getString("timey"));
            map.put("name",tj.getString("name"));
            map.put("id",tj.getString("interviewId"));
            map.put("givelike",tj.getString("givelike"));
            map.put("discuss",tj.getString("discuss"));
            fangtanList.add(map);
        }

        adapter.notifyDataSetChanged();

    }

    private void showNetError() {
        wifi404.setVisibility(View.VISIBLE);
    }

}
