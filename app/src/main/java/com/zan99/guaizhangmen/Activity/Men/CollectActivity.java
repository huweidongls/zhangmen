package com.zan99.guaizhangmen.Activity.Men;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zan99.guaizhangmen.Activity.Guai.BookActivity;
import com.zan99.guaizhangmen.Adapter.CollectAdapte;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2017/11/29.
 */

public class CollectActivity extends BaseActivity {


    @BindView(R.id.collectList)
    ListView collectList;
    @BindView(R.id.back_left)
    Button back_left;
    @BindView(R.id.empty)
    View empty;
    @BindView(R.id.button2)
    LinearLayout button2;

    private ArrayList<HashMap<String, String>> collectdata;
    private int collect_page=1;
    private CollectAdapte adapteCollect;
    private RefreshLayout smartLayout;
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        collectList(collect_page);
        refresh();
    }

    @butterknife.OnClick({R.id.button2,R.id.back_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_left:
            case R.id.button2:
                finish();
                break;
        }
    }



    //刷新加载
    private void refresh(){

        smartLayout = (RefreshLayout)findViewById(R.id.smartLayout);

        if (isFirstEnter) {
            isFirstEnter = false;
            //smartLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
        }
        smartLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
            collect_page=1;
            collectList(collect_page);
            }
        });

        //下拉加载
        smartLayout.setOnLoadmoreListener(new OnLoadmoreListener(){

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                collect_page++;
                collectList(collect_page);
            }
        });
    }

    private void collectList(final int page){
        //String member_id=getIntent().getStringExtra("member_id");

        String member_id= MenModel.member_id;
        if(page==1){
            collectdata = new ArrayList<HashMap<String, String>>();
        }

        final String path = "/member.php/Interfaces/Collect";
        final RequestParams params = new RequestParams();
        params.put("member_id",member_id);
        params.put("p",page);

        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String result = new String(responseBody, "utf-8");
                    initData(result,page);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    getTyChaterrmsg(path,params.toString());
                    showemploy(empty,collectList);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getTyChaterrmsg(path,params.toString());
                showemploy(empty,collectList);
            }
        });
    }


    private void initData(String result,int page){
        try {
            JSONObject jsonObject=new JSONObject(result);
            String Member_Collection=jsonObject.getString("Member_Collection");
            JSONArray jsonArray=new JSONArray(Member_Collection);


            for (int i = 0; i < jsonArray.length(); i++){
                HashMap<String, String> map=new HashMap<String, String>();
                JSONObject myjObject = jsonArray.getJSONObject(i);
                String books_name=myjObject.getString("books_name");
                String books_img=myjObject.getString("books_img");
                String books_synopsis=myjObject.getString("books_synopsis");
                String ctime=myjObject.getString("ctime");
                String books_id=myjObject.getString("books_id");

                map.put("books_name",books_name);
                map.put("books_img",books_img);
                map.put("books_synopsis",books_synopsis);
                map.put("ctime",ctime);
                map.put("books_id",books_id);
                collectdata.add(map);
            }

            if(page==1){
                setAdapter();
                smartLayout.finishRefresh();
            }else{
                adapteCollect.notifyDataSetChanged();
                smartLayout.finishLoadmore();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(){
        if(collectdata.size()<=0){

            showemploy(empty,collectList);
        }else {
            empty.setVisibility(View.GONE);
            collectList.setVisibility(View.VISIBLE);
            adapteCollect = new CollectAdapte(CollectActivity.this, collectdata);
            collectList.setAdapter(adapteCollect);
            collectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> stringStringHashMap = collectdata.get(position);
                    Intent intent = new Intent(CollectActivity.this, BookActivity.class);
                    intent.putExtra("books_id", stringStringHashMap.get("books_id"));
                    startActivity(intent);
                }
            });
        }
    }



}
