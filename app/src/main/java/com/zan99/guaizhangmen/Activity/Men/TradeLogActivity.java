package com.zan99.guaizhangmen.Activity.Men;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zan99.guaizhangmen.Adapter.TradeLogAdapter;
import com.zan99.guaizhangmen.BaseActivity;
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
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2017/11/29.
 */

public class TradeLogActivity extends BaseActivity {


    @BindView(R.id.tradelog)
    ListView tradelog;
    @BindView(R.id.back_left)
    Button back_left;
    @BindView(R.id.button2)
    LinearLayout button2;
    @BindView(R.id.empty)
    View empty;

    private ArrayList<HashMap<String, String>> tradelogdata;

    private int tradelog_page=1;
    private RefreshLayout smartLayout;
    private TradeLogAdapter tradeLogAdapter;
    private SweetAlertDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_log);
        ButterKnife.bind(this);
        pDialog=loadingpDialog(this);
        tradelog.setDividerHeight(0);
        tradelog_pageList(tradelog_page);
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
        smartLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                tradelog_page=1;
                tradelog_pageList(tradelog_page);
            }
        });

        //下拉加载
        smartLayout.setOnLoadmoreListener(new OnLoadmoreListener(){

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                tradelog_page++;
                tradelog_pageList(tradelog_page);
            }
        });
    }

    //请求获得的数据
    private void tradelog_pageList(final int page){

        //pDialog.show();
        String member_id=getIntent().getStringExtra("member_id");
        if(page==1){
            tradelogdata = new ArrayList<HashMap<String, String>>();
        }
        final String path = "/member.php/Interfaces/Recharge";
        final RequestParams params = new RequestParams();
        params.put("member_id",member_id);
        params.put("p",page);

        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String result = new String(responseBody, "utf-8");
                    System.out.println("result:"+result);
                    initData(new JSONObject(result),page);
                    pDialog.hide();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    getTyChaterrmsg(path,params.toString());
                    showemploy(empty,tradelog);

                } catch (JSONException e) {
                    e.printStackTrace();
                    getTyChaterrmsg(path,params.toString());
                    showemploy(empty,tradelog);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                getTyChaterrmsg(path,params.toString());
                showemploy(empty,tradelog);
            }
        });
    }


    private void initData(JSONObject jsonObject,int page){
        try {
            String data=jsonObject.getString("data");
            JSONArray jsonArray=new JSONArray(data);
            for (int i=0;i<jsonArray.length();i++){
                HashMap<String, String> map=new HashMap<String, String>();
                JSONObject json=jsonArray.getJSONObject(i);

                map.put("content",json.getString("content"));
                map.put("otime",json.getString("otime"));
                map.put("status",json.getString("status"));
                if(json.getString("status")=="1"||json.getString("status")=="4"){
                    map.put("money",json.getString("money"));
                }
                tradelogdata.add(map);
            }

            if(tradelogdata.size()<=0){
                showemploy(empty,tradelog);
            }else{
                empty.setVisibility(View.GONE);
                if(page==1){
                    tradeLogAdapter = new TradeLogAdapter(this, tradelogdata);
                    tradelog.setAdapter(tradeLogAdapter);
                    smartLayout.finishRefresh();
                }else{
                    tradeLogAdapter.notifyDataSetChanged();
                    smartLayout.finishLoadmore();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





}
