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
import com.zan99.guaizhangmen.Activity.CommentInfoActivity;
import com.zan99.guaizhangmen.Activity.Guai.BookActivity;
import com.zan99.guaizhangmen.Activity.TemplateActivity;
import com.zan99.guaizhangmen.Adapter.CommentAdapte;
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

public class CommentActivity extends BaseActivity {


    @BindView(R.id.commentList)
    ListView commentList;
    @BindView(R.id.back_left)
    Button back_left;
    @BindView(R.id.empty)
    View empty;
    @BindView(R.id.button2)
    LinearLayout button2;


    private ArrayList<HashMap<String, String>> commentdata;

    private int comment_page=1;
    private CommentAdapte commentAdapte;
    private RefreshLayout smartLayout;
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        comment_page=1;
        commentList(comment_page);
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
                comment_page=1;
                commentList(comment_page);
            }
        });

        //下拉加载
        smartLayout.setOnLoadmoreListener(new OnLoadmoreListener(){

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
            comment_page++;
            commentList(comment_page);
            }
        });
    }

    //请求获得的数据
    private void commentList(final int page){

        //String member_id=getIntent().getStringExtra("member_id");

        String member_id= MenModel.member_id;
        if(page==1){
            commentdata = new ArrayList<HashMap<String, String>>();
        }
        final String path = "/member.php/Interfaces/CommenTarIes";
        final RequestParams params = new RequestParams();
        params.put("member_id",member_id);
        params.put("p",page);

        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject = null;
                try {
                    String result = new String(responseBody, "utf-8");
                    jsonObject = new JSONObject(result);
                    String comment_list=jsonObject.getString("comment");
                    initView(comment_list,page);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    getTyChaterrmsg(path,params.toString());
                    showemploy(empty,commentList);
                } catch (JSONException e) {
                    e.printStackTrace();
                    getTyChaterrmsg(path,params.toString());
                    showemploy(empty,commentList);
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getTyChaterrmsg(path,params.toString());
            }
        });
    }

    private void initView(String result,int page){
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(result);
            String comment_list=jsonObject.getString("comment_list");
            JSONArray jsonArray = new JSONArray(comment_list);

            for (int i=0;i<jsonArray.length();i++){
                HashMap<String, String> map=new HashMap<String, String>();
                String types=jsonArray.getJSONObject(i).getString("types");
                String comment_id=jsonArray.getJSONObject(i).getString("comment_id");
                String nick_name=jsonArray.getJSONObject(i).getString("nick_name");
                String head_img=jsonArray.getJSONObject(i).getString("head_img");
                String content=jsonArray.getJSONObject(i).getString("content");
                String otime=jsonArray.getJSONObject(i).getString("otime");
                String otimes=jsonArray.getJSONObject(i).getString("otimes");
                //书籍评论
                String particulars=jsonArray.getJSONObject(i).getString("particulars");
                //书籍回复的回复
                String revert=jsonArray.getJSONObject(i).getString("revert");
                // 访谈的评论
                String particureply=jsonArray.getJSONObject(i).getString("particureply");
                //访谈回复的回复
                String reply=jsonArray.getJSONObject(i).getString("reply");

                if(particulars!="null"){
                    JSONObject jsonObj=new JSONObject(particulars);
                    String books_id=jsonObj.getString("books_id");
                    map.put("books_id",books_id);
                }
                if(revert!="null"){
                    JSONObject jsonObj=new JSONObject(revert);
                    String books_id=jsonObj.getString("books_id");
                    map.put("books_id",books_id);
                }

                if(particureply!="null"){
                    JSONObject jsonObj=new JSONObject(particureply);
                    String interview_id=jsonObj.getString("interview_id");
                    map.put("interview_id",interview_id);
                }

                if(reply!="null"){
                    JSONObject jsonObj=new JSONObject(reply);
                    String interview_id=jsonObj.getString("interview_id");
                    map.put("interview_id",interview_id);
                }

                map.put("types",types);
                map.put("comment_id",comment_id);
                map.put("nick_name",nick_name);
                map.put("head_img",head_img);
                map.put("content",content);
                map.put("otimes",otimes);
                map.put("otime",otime);
                map.put("particulars",particulars);
                map.put("revert",revert);
                map.put("particureply",particureply);
                map.put("reply",reply);
                commentdata.add(map);
            }

            if(page==1){
                setAdapter();
                smartLayout.finishRefresh();
            }else{
                commentAdapte.notifyDataSetChanged();
                smartLayout.finishLoadmore();
            }
        } catch (JSONException e) {

            e.printStackTrace();
            showemploy(empty,commentList);
        }

    }

    private void setAdapter(){
        if(commentdata.size()<=0){

            showemploy(empty,commentList);
        }else {
            empty.setVisibility(View.GONE);
            commentList.setVisibility(View.VISIBLE);
            commentList = (ListView) findViewById(R.id.commentList);
            commentAdapte = new CommentAdapte(this, commentdata);
            commentList.setAdapter(commentAdapte);
            commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> stringStringHashMap = commentdata.get(position);
                    String types = stringStringHashMap.get("types");
                    if (types == "1") {
                        Intent intent = new Intent();
                        intent.setClass(CommentActivity.this, CommentInfoActivity.class);
                        intent.putExtra("comment_id", stringStringHashMap.get("comment_id"));
                        intent.putExtra("nick_name", stringStringHashMap.get("nick_name"));
                        intent.putExtra("head_img", stringStringHashMap.get("head_img"));
                        intent.putExtra("content", stringStringHashMap.get("content"));
                        intent.putExtra("date", stringStringHashMap.get("otime"));
                        intent.putExtra("id", stringStringHashMap.get("books_id")); //books_id interview_id
                        intent.putExtra("template_type", "book"); //book fangtan
                        startActivity(intent);
                    }

                    if (types == "2") { //
                        Intent intent = new Intent();
                        intent.setClass(CommentActivity.this, CommentInfoActivity.class);
                        intent.putExtra("comment_id", stringStringHashMap.get("comment_id"));
                        intent.putExtra("nick_name", stringStringHashMap.get("nick_name"));
                        intent.putExtra("head_img", stringStringHashMap.get("head_img"));
                        intent.putExtra("content", stringStringHashMap.get("content"));
                        intent.putExtra("date", stringStringHashMap.get("otime"));
                        intent.putExtra("id", stringStringHashMap.get("interview_id")); //books_id interview_id
                        intent.putExtra("template_type", "fangtan"); //book fangtan
                        startActivity(intent);
                    }
                }
            });
        }
    }





}
