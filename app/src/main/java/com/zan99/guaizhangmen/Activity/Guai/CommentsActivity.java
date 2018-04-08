package com.zan99.guaizhangmen.Activity.Guai;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.Men.CollectActivity;
import com.zan99.guaizhangmen.Adapter.BookDiscussAdapter;
import com.zan99.guaizhangmen.Adapter.BooksDiscussDialogAdapter;
import com.zan99.guaizhangmen.Adapter.CollectAdapte;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CircleTransform;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.github.rockerhieu.emojicon.EmojiconEditText;

import static com.zan99.guaizhangmen.Util.CustomerUtil.hideSoftInputFromWindow;
import static com.zan99.guaizhangmen.Util.CustomerUtil.showSoftInputFromWindow;

/**
 * Created by Administrator on 2017/11/27.
 */

public class CommentsActivity extends BaseActivity  {

    @BindView(R.id.emoji_btn) Button emoji_btn;
    @BindView(R.id.comments) ListView comments;
    private int commentspage=1;
    private RefreshLayout smartLayout;
    private static boolean isFirstEnter = true;
    private ArrayList<HashMap<String, String>> data;
    private BookDiscussAdapter adapter;
    private View inflate;
    private Dialog dialog;
    private ArrayList<HashMap<String, String>> dlist;
    private EmojiconEditText cmEditEmojicon;
    private String member_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);
        refresh();
        //initData(commentspage);
        SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
        member_id= L.decrypt(pref.getString("member_id",""),MenModel.LKEY);

    }


    //刷新加载
    private void refresh() {

        smartLayout = (RefreshLayout) findViewById(R.id.smartLayout);

        if (isFirstEnter) {
            isFirstEnter = false;
            smartLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
        }
        smartLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                commentspage = 1;
                initData(commentspage);
            }
        });

        //下拉加载
        smartLayout.setOnLoadmoreListener(new OnLoadmoreListener() {

            @Override
            public void onLoadmore(com.scwang.smartrefresh.layout.api.RefreshLayout refreshlayout) {
                commentspage++;
                initData(commentspage);
            }
        });
    }
    private void initData(final int page){
        final String path = "author.php/Nexts/BooksCommentAll";
        final RequestParams params = new RequestParams();
        params.put("books_id",getIntent().getStringExtra("books_id"));
        params.put("page",page);
        if(page==1){
            data = new ArrayList<HashMap<String, String>>();
        }

        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    System.out.println("result:"+result);
                    initresult(result,page,path,params.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg(path,params.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg(path,params.toString());
            }
        });
    }

    private void initresult(String result,int page,String path,String params){
        try {

            JSONObject jsonObject=new JSONObject(result);
            JSONArray jsonArray=jsonObject.getJSONArray("dataList");
            for (int i=0;i<jsonArray.length();i++){
                JSONObject object=jsonArray.getJSONObject(i);

                HashMap<String,String> map=new HashMap<String,String>();

                map.put("nick_name",object.getString("names"));
                map.put("content",object.getString("content"));
                map.put("otime_date",object.getString("dates"));
                map.put("otime_time",object.getString("times"));
                map.put("number",object.getString("zsum"));
                //http://oadwi6tsa.bkt.clouddn.com/01a7d4ef2e354fe0b4e797fc6faca349.png
                String urlimg=object.getString("urlimg");

                if(urlimg=="null"){
                    map.put("head_img","http://oadwi6tsa.bkt.clouddn.com/01a7d4ef2e354fe0b4e797fc6faca349.png");
                }else{
                    map.put("head_img","http://"+urlimg);
                }


                System.out.println("img1:"+object.getString("urlimg"));
                data.add(map);
            }

            if(page==1){
                setAdapter();
                smartLayout.finishRefresh();
            }else{
                adapter.notifyDataSetChanged();
                smartLayout.finishLoadmore();
            }


        } catch (JSONException e) {
            e.printStackTrace();
            BaseActivity.getTyChaterrmsg(path,params);
        }
    }

    private void setAdapter(){
        if(data.size()<=0){
            //empty.setVisibility(View.VISIBLE);
            comments.setVisibility(View.GONE);
        }else {
            //empty.setVisibility(View.GONE);
            comments.setVisibility(View.VISIBLE);
            adapter = new BookDiscussAdapter(CommentsActivity.this, data);
            comments.setAdapter(adapter);
            comments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> stringStringHashMap = data.get(position);
//                    Intent intent = new Intent(CollectActivity.this, BookActivity.class);
//                    intent.putExtra("books_id", stringStringHashMap.get("books_id"));
//                    startActivity(intent);
                    String res_name = stringStringHashMap.get("nick_name");
                    String comment_id =stringStringHashMap.get("comment_id");
                    showDiscussInfo(comment_id,position);
                }
            });


        }
    }


    private void showDiscussInfo(final String comment_id, int key) {

        dialog = new Dialog(this,R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.layout_discuss_dialog, null);

        ImageView headimg = (ImageView) inflate.findViewById(R.id.headimg);
        TextView nick_name  = (TextView) inflate.findViewById(R.id.nick_name);
        TextView timer  = (TextView) inflate.findViewById(R.id.timer);
        TextView date  = (TextView) inflate.findViewById(R.id.date);
        final TextView content  = (TextView) inflate.findViewById(R.id.content);
        Picasso.with(this).load(data.get(key).get("head_img")).transform(new CircleTransform()).into(headimg);


        nick_name.setText(data.get(key).get("nick_name"));
        timer.setText(data.get(key).get("otime_time"));
        date.setText(data.get(key).get("otime_date"));
        content.setText(data.get(key).get("content"));

        loadDiscussInfo(comment_id);

        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 0;//设置Dialog距离底部的距离
//       将属性设置给窗体
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        lp.width = d.getWidth()-10;
        lp.height = d.getHeight() - 200;
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框

    }

    private void loadDiscussInfo(final String comment_id) {
        cmEditEmojicon = (EmojiconEditText) inflate.findViewById(R.id.mEditEmojicon);
        final RequestParams params = new RequestParams();
        params.put("comment_id",comment_id);
        HttpUtil.post("author.php/Nexts/commentout",params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    com.alibaba.fastjson.JSONObject jsonObject = null;
                    try {
                        jsonObject = JSON.parseObject(new String(responseBody,"utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    initDiscussDialogList(jsonObject.getJSONObject("dataList"));

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg("author.php/Nexts/commentout",params.toString());
            }
        });


        emoji_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = cmEditEmojicon.getText().toString();
                if(!TextUtils.isEmpty(text)){
                   // insertComment(member_id,text,"0");
                }
            }
        });

    }

    private void initDiscussDialogList(com.alibaba.fastjson.JSONObject jsonObject) {
        dlist = new ArrayList<HashMap<String, String>>();

        com.alibaba.fastjson.JSONArray dataList = jsonObject.getJSONArray("list");
        if(!dataList.isEmpty()){

            try {
                for (int n = 0; n < dataList.size(); n++){
                    com.alibaba.fastjson.JSONObject temp = dataList.getJSONObject(n);
                    HashMap<String, String> map = new HashMap<String, String>();
                    com.alibaba.fastjson.JSONObject tempm = temp.getJSONObject("member");
                    com.alibaba.fastjson.JSONObject tempc = temp.getJSONObject("comment");

                    map.put("nick_name", tempm.getString("aitename"));
                    map.put("head_img", "http://"+tempm.getString("urlImg"));
                    map.put("content", tempc.getString("content"));
                    map.put("timer", tempc.getString("otimetime"));
                    map.put("to_name", tempm.getString("pointName"));
                    map.put("comment_id", tempc.getString("commentId"));
                    dlist.add(map);
                }
            } catch (com.alibaba.fastjson.JSONException e) {
                e.printStackTrace();
            }

            ListView dcusview = (ListView) inflate.findViewById(R.id.content_list_view);

            BooksDiscussDialogAdapter dadapter = new BooksDiscussDialogAdapter(CommentsActivity.this, dlist);
            dcusview.setAdapter(dadapter);
            dcusview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String comment_id = view.getTag(R.id.tag_comment).toString();

                    cmEditEmojicon.setTag(R.id.tag_comment, comment_id);
                    String to_name = view.getTag(R.id.to_name).toString();
                    cmEditEmojicon.setHint("@"+ to_name);
                    showSoftInputFromWindow(CommentsActivity.this, cmEditEmojicon);
                }
            });
         /*

            Button cmbtn  = (Button) inflate.findViewById(R.id.cmbtn);
            cmbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView content  = (TextView) inflate.findViewById(R.id.content);
                    SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
                    String auser_id=L.decrypt(pref.getString("member_id",""),MenModel.LKEY);
                    String conString=content.getText().toString().trim();
                    insertComment(auser_id,conString,comment_id);
                    //hideSoftInputFromWindow(BookActivity.this);
                    //content.setText("");
                }
            });

            Button cmbtn = (Button) inflate.findViewById(R.id.cmbtn);

            cmbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView content  = (TextView) inflate.findViewById(R.id.content);
                    String text = content.getText().toString().trim();
                    if(!TextUtils.isEmpty(text)){
                        String comment_id = cmEditEmojicon.getTag(R.id.tag_comment).toString();
                        if(comment_id != null){
                            toAsyncComment(comment_id,text);
                        }
                    }
                    hideSoftInputFromWindow(BookActivity.this);
                }
            });
*/
        }
    }



}
