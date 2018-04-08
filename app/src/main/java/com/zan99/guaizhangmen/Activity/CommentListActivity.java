package com.zan99.guaizhangmen.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ldoublem.loadingviewlib.view.LVEatBeans;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zan99.guaizhangmen.Adapter.BookDiscussAdapter;
import com.zan99.guaizhangmen.Adapter.LinearLayoutListViewBookDiscussAdapter;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.Widget.CustomerGridView;
import com.zan99.guaizhangmen.Widget.ListViewLinearlayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import io.github.rockerhieu.emojicon.EmojiconEditText;

import static com.zan99.guaizhangmen.BaseActivity.loadingpDialog;

/**
 * Created by Administrator on 2017/12/8.
 */

public class CommentListActivity extends AppCompatActivity {
    private String action_id;
    private int page = 1;
    private SweetAlertDialog pDialog;
    private ArrayList<HashMap<String ,String>> list = new ArrayList<HashMap<String, String>>();
    private ListViewLinearlayout list_view;
    private LinearLayoutListViewBookDiscussAdapter discussAdapter;
    private SmartRefreshLayout refreshLayout;
    private boolean isRefresh = false;
    private boolean isCreate = true;
    private String template_type;
    @BindView(R.id.empty)
    View empty;
    @BindView(R.id.back_layout)
    LinearLayout back_layout;
    @BindView(R.id.back_left)
    Button back_left;
    @BindView(R.id.loadView)
    LinearLayout loadView;
    @BindView(R.id.bodyView)
    RelativeLayout bodyView;
    @BindView(R.id.loadingAni)
    LVEatBeans loadingAni;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        ButterKnife.bind(this);
        loadingAni.startAnim();
        back_layout.setVisibility(View.VISIBLE);

        list_view = (ListViewLinearlayout) findViewById(R.id.list_view);
        discussAdapter = new LinearLayoutListViewBookDiscussAdapter(this, list);
        list_view.setAdapter(discussAdapter);
        list_view.setOnItemClickListener(new ListViewLinearlayout.OnItemClickListener() {
            @Override
            public void onItemClicked(View v, Object obj, int i) {
                Intent intent = new Intent();
                intent.setClass(CommentListActivity.this, CommentInfoActivity.class);
                intent.putExtra("comment_id", list.get(i).get("comment_id"));
                intent.putExtra("nick_name", list.get(i).get("nick_name"));
                intent.putExtra("head_img", list.get(i).get("head_img"));
                intent.putExtra("content", list.get(i).get("content"));
                intent.putExtra("date", list.get(i).get("otime_date"));
                intent.putExtra("timer", list.get(i).get("otime_time"));
                intent.putExtra("id", action_id);
                intent.putExtra("template_type", template_type);
                startActivity(intent);
            }

        });
        action_id = getIntent().getStringExtra("id");
        template_type = getIntent().getStringExtra("template_type");
        String books_name = getIntent().getStringExtra("title");
        TextView titleview = (TextView) findViewById(R.id.title);
        titleview.setText(books_name);
        pDialog = loadingpDialog(this);
        //pDialog.show();

        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        page = 1;

    }

    @OnClick({R.id.back_layout, R.id.back_left})
    public void backClick(View v){
        finish();
    }


    private void initEvent() {
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        refreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refresh();
                refreshlayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                loadData();
                refreshlayout.finishLoadmore(2000);
            }
        });
    }

    private void refresh() {
        page = 1;
        isRefresh = true;
    }

    private void loadData() {
        final RequestParams parm = new RequestParams();
        parm.add("page",String.valueOf(page));
        String url = "";
        if(template_type.equals("book")){
            parm.add("books_id",action_id);
            url = "author.php/Nexts/booksCommentAll";
        }else if(template_type.equals("fangtan")){
            url = "admin.php/Systeminterface/all_discuss";
            parm.add("interview_id",action_id);
        }
        final String path=url;
        HttpUtil.post(url, parm, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf8"));

                    if(jsonObject.getString("errcode").equals("0")){
                        initView(jsonObject.getJSONArray("dataList"));
                    }else{
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                        showEmpty();

                    }
                } catch (UnsupportedEncodingException e) {
                    showEmpty();
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg(path,parm.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }
                pDialog.hide();
                BaseActivity.getTyChaterrmsg(path,parm.toString());
            }
        });
    }

    private void showEmpty() {
        BaseActivity.showemploy(empty,list_view);
    }

    private void initView(JSONArray dataList) {
        if(isRefresh){
            list.clear();
            discussAdapter.notifyDataSetChanged();
        }

        if(template_type.equals("book")){
            for (int i = 0; i < dataList.size(); i++) {
                JSONObject temp = dataList.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("nick_name", temp.getString("names"));
                map.put("head_img", "http://" + temp.getString("urlimg"));
                map.put("content", temp.getString("content"));
                map.put("number", temp.getString("zsum"));
                map.put("otime_date", temp.getString("dates"));
                map.put("otime_time", temp.getString("times"));
                map.put("comment_id", temp.getString("commentId"));
                map.put("to_user_id",temp.getString("auserId"));
                list.add(map);
            }
        } else if(template_type.equals("fangtan")){
            for (int i = 0; i < dataList.size(); i++) {
                JSONObject temp = dataList.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("nick_name", temp.getString("ame"));
                map.put("head_img", "http:/\"n/" + temp.getString("headimg"));
                map.put("content", temp.getString("content"));
                map.put("number", temp.getString("count"));
                map.put("otime_date", temp.getString("date"));
                map.put("otime_time", temp.getString("time"));
                map.put("comment_id", temp.getString("discuss_id"));
                map.put("to_user_id",temp.getString("auser_id"));
                list.add(map);
            }
        }

        discussAdapter.notifyDataSetChanged();

        if(pDialog.isShowing()){
            pDialog.dismiss();
        }

        if(isCreate){
            isCreate = false;
            initEvent();
        }

        loadingAni.stopAnim();
        loadView.setVisibility(View.GONE);
        bodyView.setVisibility(View.VISIBLE);
    }


}
