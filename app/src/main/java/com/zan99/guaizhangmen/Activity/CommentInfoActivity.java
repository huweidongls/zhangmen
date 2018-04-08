package com.zan99.guaizhangmen.Activity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ldoublem.loadingviewlib.view.LVEatBeans;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.Guai.CommentsActivity;
import com.zan99.guaizhangmen.Adapter.BooksDiscussDialogAdapter;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

import static com.zan99.guaizhangmen.BaseActivity.loadingpDialog;
import static com.zan99.guaizhangmen.Util.CustomerUtil.hideSoftInputFromWindow;
import static com.zan99.guaizhangmen.Util.CustomerUtil.showSoftInputFromWindow;

/**
 * Created by Administrator on 2017/12/8.
 */

public class CommentInfoActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    private SweetAlertDialog pDialog;
    private String comment_id;
    private String action_id;
    private String template_type;
    @BindView(R.id.nick_name)
    TextView nick_name;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.headimg)
    ImageView headimg;
    @BindView(R.id.xiaolian)
    ImageView xiaolian;
    @BindView(R.id.bottominput)
    LinearLayout bottominput;
    @BindView(R.id.bottombtn)
    LinearLayout bottombtn;
    @BindView(R.id.loadView)
    LinearLayout loadView;
    @BindView(R.id.bodyView)
    LinearLayout bodyView;
    @BindView(R.id.loadingAni)
    LVEatBeans loadingAni;
    private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private ListView dcusview;
    private BooksDiscussDialogAdapter dadapter;
    private EmojiconEditText cmEditEmojicon;
    private Button cmbtn;
    private FrameLayout emojicons;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_info);
        ButterKnife.bind(this);
        comment_id = getIntent().getStringExtra("comment_id");
        action_id = getIntent().getStringExtra("id");
        template_type = getIntent().getStringExtra("template_type");
        pDialog = loadingpDialog(this);
        pDialog.show();
        //loadingAni.startAnim();
        initView();
        loadData();
    }

    

    @Override
    protected void onStart() {
        super.onStart();
        if(MenModel.member_id.equals("0") || MenModel.member_id.equals("")){
            bottombtn.setVisibility(View.GONE);

        }else{

            bottombtn.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {

        emojicons = (FrameLayout) findViewById(R.id.emojicons);

        timer.setText(getIntent().getStringExtra("timer"));
        content.setText(getIntent().getStringExtra("content"));
        date.setText(getIntent().getStringExtra("date"));
        nick_name.setText(getIntent().getStringExtra("nick_name"));
        Picasso.with(this).load(getIntent().getStringExtra("head_img")).into(headimg);

        cmEditEmojicon = (EmojiconEditText) findViewById(R.id.mEditEmojicon);
        setEmojiconFragment();
        cmEditEmojicon.setTag(R.id.comment_id, comment_id);
        cmEditEmojicon.setTag(R.id.nick_name,getIntent().getStringExtra("nick_name"));
        cmEditEmojicon.setTag(R.id.to_name,getIntent().getStringExtra("nick_name"));
        cmEditEmojicon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    // 此处为得到焦点时的处理内容

                    emojicons.setVisibility(View.GONE);
                    //Toast.makeText(IndexListShowActivity.this,"此处为得到焦点时的处理内容",Toast.LENGTH_SHORT).show();
                } else {
                    // 此处为失去焦点时的处理内容
                    //Toast.makeText(IndexListShowActivity.this,"此处为失去焦点时的处理内容",Toast.LENGTH_SHORT).show();
                }
            }
        });


        dcusview = (ListView) findViewById(R.id.content_list_view);
        dadapter = new BooksDiscussDialogAdapter(this, list);
        dcusview.setAdapter(dadapter);
        //
        if((!MenModel.member_id.equals("0")) && (!MenModel.member_id.equals(""))) {
            dcusview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cmEditEmojicon.setTag(R.id.comment_id, list.get(position).get("comment_id"));
                    cmEditEmojicon.setTag(R.id.to_name, list.get(position).get("nick_name"));
                    String to_name = view.getTag(R.id.nick_name).toString();
                    cmEditEmojicon.setHint("@" + to_name);
                    showSoftInputFromWindow(CommentInfoActivity.this, cmEditEmojicon);
                }
            });
        }

        cmbtn  = (Button) findViewById(R.id.cmbtn);
        cmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = cmEditEmojicon.getText().toString().trim();
                if(!TextUtils.isEmpty(text)){
                    String comment_id = cmEditEmojicon.getTag(R.id.comment_id).toString();
                    if(comment_id != null){
                        toAsyncComment(comment_id,text);
                    }
                }
//                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0);
                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.commentInfo).getWindowToken(), 0);
                emojicons.setVisibility(View.GONE);
                //hideSoftInputFromWindow(CommentInfoActivity.this);
            }
        });
    }

    private void toAsyncComment(String comment_id, String text) {
        String member_id = MenModel.member_id;
        final RequestParams params = new RequestParams();
        params.put("point_id", comment_id);
        params.put("content", text);
        params.put("auser_id", member_id);
        String url = "";
        if(template_type.equals("book")){
            url = "author.php/Nexts/insertcomment";
            params.put("chapter_id", action_id);
        }else{
            url = "admin.php/Systeminterface/insertdiscuss";
            params.put("interview_id", action_id);
        }
        final String path=url;

        HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf8"));
                    addDiscuss(jsonObject.getJSONObject("dataList"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg(path,params.toString());
                }
                cmEditEmojicon.setText("");
                cmEditEmojicon.setHint("评论内容");

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg(path,params.toString());
            }
        });
    }

    private void addDiscuss(JSONObject temp) {
        HashMap<String, String> map = new HashMap<String, String>();
        if(template_type.equals("book")){
            map.put("nick_name", temp.getString("nickName"));
            map.put("head_img", "http://"+temp.getString("urlImg"));
            map.put("content", temp.getString("content"));
            map.put("timer", temp.getString("otimeTime"));
            map.put("to_name", cmEditEmojicon.getTag(R.id.to_name).toString());
            map.put("date", temp.getString("otimeTime"));
            map.put("comment_id", temp.getString("commentId"));
            map.put("to_user_id",temp.getString("auserId"));
        }else if(template_type.equals("fangtan")){
            map.put("comment_id", temp.getString("discussId"));
            map.put("to_user_id",temp.getString("auserId"));
            map.put("content", temp.getString("content"));
            map.put("nick_name", temp.getString("names"));
            map.put("head_img", temp.getString("head_img"));
            map.put("timer", temp.getString("time"));
            map.put("date", temp.getString("date"));
            map.put("to_name", cmEditEmojicon.getTag(R.id.to_name).toString());
        }

        list.add(map);
        dadapter.notifyDataSetChanged();
    }

    public void back(View view){
        finish();
    }

    private void loadData() {
        final RequestParams parm = new RequestParams();
        String url = "";
        if(template_type.equals("book")){
            url = "author.php/Nexts/commentout";
            parm.add("comment_id",comment_id);
        }else if(template_type.equals("fangtan")){
            url = "admin.php/Systeminterface/click_discuss";
            parm.add("discuss_id",comment_id);
        }
        final String path=url;
        HttpUtil.post(url, parm, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf8"));
                    if(jsonObject.getString("errcode").equals("0")){
                        initDiscussView(jsonObject.getJSONObject("dataList"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg(path,parm.toString());
                }

                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg(path,parm.toString());
                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }
            }
        });
    }

    private void initDiscussView(JSONObject data) {
        JSONArray dataList = data.getJSONArray("list");
        if(!dataList.isEmpty()) {
            for (int n = 0; n < dataList.size(); n++) {
                JSONObject temp = dataList.getJSONObject(n);
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject tempm = temp.getJSONObject("member");
                JSONObject tempc = temp.getJSONObject("comment");

                map.put("nick_name", tempm.getString("pointName"));
                map.put("head_img", "http://" + tempm.getString("urlImg"));
                map.put("content", tempc.getString("content"));
                map.put("timer", tempc.getString("stimetime"));
                map.put("date", tempc.getString("otimetime"));
                map.put("to_name", tempm.getString("aitename"));
                if(template_type.equals("book")){
                    map.put("comment_id", temp.getString("commentId"));
                }else{
                    map.put("comment_id", tempc.getString("discussId"));
                }
                list.add(map);
            }

            dadapter.notifyDataSetChanged();
        }
        loadingAni.stopAnim();
        loadView.setVisibility(View.GONE);
        bodyView.setVisibility(View.VISIBLE);
    }

    private void setEmojiconFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(false))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(cmEditEmojicon, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(cmEditEmojicon);
    }



    @butterknife.OnClick({R.id.xiaolian,R.id.mEditEmojicon})
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.xiaolian:
//                cmEditEmojicon.clearFocus();
//
//
//
//                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.commentInfo).getWindowToken(), 0);
                emojicons.setVisibility(View.VISIBLE);
                break;

            case R.id.mEditEmojicon:

                emojicons.setVisibility(View.GONE);
                break;
        }
    }



}
