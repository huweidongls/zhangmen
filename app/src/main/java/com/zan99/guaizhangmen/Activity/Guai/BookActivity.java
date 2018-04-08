package com.zan99.guaizhangmen.Activity.Guai;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.ldoublem.loadingviewlib.view.LVEatBeans;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.CommentInfoActivity;
import com.zan99.guaizhangmen.Activity.CommentListActivity;
import com.zan99.guaizhangmen.Activity.Men.CommentActivity;
import com.zan99.guaizhangmen.Activity.Men.RecargeGuaidouActivity;
import com.zan99.guaizhangmen.Activity.TemplateActivity;
import com.zan99.guaizhangmen.Adapter.BookChapterAdapter;
import com.zan99.guaizhangmen.Adapter.BookDiscussAdapter;
import com.zan99.guaizhangmen.Adapter.BooksDiscussDialogAdapter;
import com.zan99.guaizhangmen.Adapter.LinearLayoutListViewBookDiscussAdapter;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CircleTransform;
import com.zan99.guaizhangmen.Util.CustomerUtil;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.Widget.CustomerGridView;
import com.zan99.guaizhangmen.Widget.ListViewLinearlayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

import static com.zan99.guaizhangmen.Util.CustomerUtil.hideSoftInputFromWindow;
import static com.zan99.guaizhangmen.Util.CustomerUtil.showSoftInputFromWindow;

/**
 * Created by Administrator on 2017/11/27.
 */

public class BookActivity extends BaseActivity implements  View.OnClickListener,EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    private View inflate;
    private Dialog dialog;
    private ArrayList<HashMap<String, String>> discussList;
    private EmojiconEditText mEditEmojicon;
    private String books_id;
    private String books_name;
    private String autherName;
    private ArrayList<HashMap<String, String>> dlist;
    private EmojiconEditText cmEditEmojicon;
    private ListViewLinearlayout discussListView;
    private SweetAlertDialog loaddialog;
    private LinearLayoutListViewBookDiscussAdapter discussAdapter;

    @BindView(R.id.emojicons)
    FrameLayout emojicons;
    @BindView(R.id.xiaolian)
    ImageView xiaolian;
    @BindView(R.id.booksimg)  ImageView booksimg;
    @BindView(R.id.books_title)
    TextView editText;
    @BindView(R.id.bookname)  TextView bookname;
    @BindView(R.id.thumb)  TextView thumbtext;
    @BindView(R.id.collection)  TextView collectiontext;
    @BindView(R.id.auther_account_img)
    CircleImageView auther_account_img;
    @BindView(R.id.auther_content_info)  TextView auther_content_info;
    @BindView(R.id.books_synopsis)  TextView books_synopsis;
    @BindView(R.id.auther_content_detailed_profile)  TextView auther_content_detailed_profile;
    @BindView(R.id.emoji_btn) Button emoji_btn;
    @BindView(R.id.zan_img) ImageView zan_img;
    @BindView(R.id.sou_img) ImageView sou_img;
    @BindView(R.id.bottominput) LinearLayout bottominput;
    @BindView(R.id.discuss) ListViewLinearlayout discuss;
    @BindView(R.id.pinglunqu) LinearLayout pinglunqu;
    @BindView(R.id.show_all) TextView show_all;
    @BindView(R.id.back_left) Button back_left;
    @BindView(R.id.button2) LinearLayout button2;
    @BindView(R.id.bqIcon) LinearLayout bqIcon;
    @BindView(R.id.loadView)
    LinearLayout loadView;
    @BindView(R.id.bodyView)
    LinearLayout bodyView;
    @BindView(R.id.loadingAni)
    LVEatBeans loadingAni;
    private static String isComment;
    private String member_id;
    private ArrayList<HashMap<String, String>> chapterList;
    private BooksDiscussDialogAdapter dadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        ButterKnife.bind(this);
        books_id = getIntent().getStringExtra("books_id");
        member_id = MenModel.member_id;
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadData(books_id);
        initEvent();
        emojicon();
    }

    //emoji 图片显示部分
    private void emojicon(){
        mEditEmojicon = (EmojiconEditText) findViewById(R.id.mEditEmojicon);
        setEmojiconFragment(false);

        mEditEmojicon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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


    }


    private void initEvent() {
        if(member_id.equals("0") || member_id.equals("")){
            bottominput.setVisibility(View.GONE);
            bqIcon.setVisibility(View.GONE);
        }else{
            bottominput.setVisibility(View.VISIBLE);
            bqIcon.setVisibility(View.VISIBLE);
        }
        mEditEmojicon = (EmojiconEditText) findViewById(R.id.mEditEmojicon);
        setEmojiconFragment(false);
        mEditEmojicon.setTag(R.id.comment_id,"0");
        mEditEmojicon.setTag(R.id.to_user_id,"0");
        mEditEmojicon.setTag(R.id.nick_name,"");
        mEditEmojicon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        emoji_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojicons.setVisibility(View.GONE);
                String text = mEditEmojicon.getText().toString();
                if(!TextUtils.isEmpty(text)){
                    insertComment(text,"0");
                }
            }
        });

    }


    private void insertComment(String text,final String point_id){
        final RequestParams params = new RequestParams();
        params.put("chapter_id",books_id);
        params.put("content",text);
        params.put("point_id",point_id);
        params.put("auser_id",member_id);

        HttpUtil.post("author.php/Nexts/insertcomment", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {
                        JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf8"));
                        String errmsg=jsonObject.getString("errmsg");
//                        Toast.makeText(BookActivity.this,errmsg,Toast.LENGTH_SHORT).show();
                        if(jsonObject.getString("errcode").equals("0")){
                            addDiscussWai(jsonObject.getJSONObject("dataList"));
                        }else{

                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        getTyChaterrmsg("author.php/Nexts/insertcomment",params.toString());
                    }
                    mEditEmojicon.setText("");
                    mEditEmojicon.setHint("评论内容");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getTyChaterrmsg("author.php/Nexts/insertcomment",params.toString());
            }
        });
    }

    private void addDiscussWai(JSONObject temp) {
//        if(discussList.size() > 0){
//            discussList.remove(discussList.size()-1);
//        }
//        ArrayList<HashMap<String, String> > t = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("nick_name", temp.getString("nickName"));
        map.put("head_img", "http://" + temp.getString("head_img"));
        map.put("content", temp.getString("content"));
        map.put("number", "0");
        map.put("otime_date", temp.getString("otimeDate"));
        map.put("otime_time", temp.getString("otimeTime"));
        map.put("comment_id", temp.getString("commentId"));
        map.put("to_user_id",temp.getString("auserId"));

        discussList.add(0,map);
        discussList.remove(discussList.size()-1);

        discussAdapter.notifyDataSetChanged();
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    private void loadData(String boods_id) {
        loaddialog=loadingpDialog(this);
        //loaddialog.show();


        final RequestParams params = new RequestParams();
        params.put("books_id",boods_id);
        params.put("member_id",member_id);
        HttpUtil.post("author.php/Nexts/listarray",params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject json  = null;
                try {
                    json = JSON.parseObject(new String(responseBody, "utf-8"));
                    initview(json.getJSONObject("dataList"));
                    if(loaddialog.isShowing()){
                        loaddialog.dismiss();
                    }
                    //hideSoftInputFromWindow(BookActivity.this);hideSoftInputFromWindow(BookActivity.this);
                    mEditEmojicon.setText("");
                } catch (UnsupportedEncodingException e) {
                    getTyChaterrmsg("author.php/Nexts/listarray",params.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 显示空页面
                if(loaddialog.isShowing()){
                    loaddialog.dismiss();
                }
                getTyChaterrmsg("author.php/Nexts/listarray",params.toString());
            }
        });
    }

    private void initview(JSONObject result) {

        setBoodsInfo(result.getJSONObject("books"));
        setAutherInfo(result.getJSONObject("author"));
        setZhangjieInfo(result.getJSONArray("chapterList"));
        setDiscussInfo(result.getJSONArray("commentList"));

        loadingAni.stopAnim();
        loadView.setVisibility(View.GONE);
        bodyView.setVisibility(View.VISIBLE);
    }

    private void setBoodsInfo(JSONObject books) {
        Picasso.with(BookActivity.this).load("http://"+books.getString("booksImg")).resize(320, 440).into(booksimg);
        bookname.setText(books.getString("booksName"));
        books_name = books.getString("booksName");
        editText.setText(books.getString("booksName"));
        thumbtext.setText(books.getString("thumb"));
        books_synopsis.setText(books.getString("booksSynopsis"));
        if(books.getString("isZan").equals("1")){
            zan_img.setImageResource(R.drawable.bshow_04);
            zan_img.setTag(R.id.is_zan,1);
        }else{
            zan_img.setTag(R.id.is_zan,0);
        }
        if(books.getString("isSave").equals("1")){
            sou_img.setImageResource(R.drawable.bshow_06);
            sou_img.setTag(R.id.is_save,1);
        }else{
            sou_img.setTag(R.id.is_save,0);
        }
        collectiontext.setText(books.getString("collection"));
        isComment=books.getString("isComment");

        Log.e("111",isComment);
        if(isComment.equals("0") || TextUtils.isEmpty(member_id)){
            pinglunqu.setVisibility(View.GONE);
            discuss.setVisibility(View.GONE);
            bottominput.setVisibility(View.GONE);
        }
    }



    private void setAutherInfo(JSONObject auther) {
        try {

            Picasso.with(BookActivity.this).load("http://"+auther.getString("headImg")).into(auther_account_img);
            SpannableString spannableString = new SpannableString(""+auther.getString("name") + "\u3000\u3000\n" + auther.getString("profile"));
            int startSpan = auther.getString("name").length()+2;
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#222222")), 0, startSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.NORMAL), 0, startSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(20,true), 0, startSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            auther_content_info.setText(spannableString);
            auther_content_detailed_profile.setText("\u3000\u3000"+auther.getString("detailedProfile"));
            autherName = auther.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    private void setZhangjieInfo(JSONArray chapter_list) {
        GridView chapterlistview= (GridView) findViewById(R.id.chapter);
        chapterList = new ArrayList<HashMap<String, String>>();


        for(int i=0;i<chapter_list.size();i++){
            HashMap<String, String> map = new HashMap<String, String>();
            JSONObject temp = chapter_list.getJSONObject(i);
            //cadapter.add(temp.getString("chaptreName"));
            map.put("durationMsec", temp.getString("durationMsec"));
            map.put("createTime", temp.getString("createTime"));
            map.put("chapterImg", temp.getString("chapterImg"));
            map.put("chaptreName", temp.getString("chaptreName"));
            map.put("id",temp.getString("chapterId"));
            chapterList.add(map);
        }


        BookChapterAdapter adapter=new BookChapterAdapter(this,chapterList);
        chapterlistview.setAdapter(adapter);

        chapterlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

              checkCanRead(chapterList.get(i).get("id").toString());


            }
        });

    }


    private void checkCanRead(final String id) {
        final RequestParams param = new RequestParams();
        if(member_id.equals("")){
            member_id="0";
        }

        param.put("member_id",member_id);   // 用户ID
        param.put("chapter_id",id);
        HttpUtil.post("author.php/Nexts/clickchapter", param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonobeject = JSON.parseObject(new String(responseBody, "utf8"));

                    if(jsonobeject.getString("errcode").equals("0")){
                        //books_id
                        Intent intent = new Intent(BookActivity.this, TemplateActivity.class);
                        intent.putExtra("type","zhangjie");
                        intent.putExtra("books_id",books_id);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    }else{
                        String type = "";
                        String payjiage = "0";

                        if(!jsonobeject.getString("errcode").equals("1")){
                            JSONObject tj = jsonobeject.getJSONObject("dataList");
                            type = tj.getString("type");
                            payjiage = tj.getString("money");
                        }
                        if(MenModel.member_id.equals("") || MenModel.member_id.equals("0")){
                            Toast.makeText(BookActivity.this,"请先登录！",Toast.LENGTH_SHORT).show();
                        }else{
                            showAlertDialog(jsonobeject.getString("errcode"),id, type, payjiage);
                        }

                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    getTyChaterrmsg("author.php/Nexts/clickchapter",param.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getTyChaterrmsg("author.php/Nexts/clickchapter",param.toString());
            }
        });
    }

    public  void showAlertDialog(String errcode, final String id, final String type, final String jiage) {
        String uid = MenModel.member_id;
        if(errcode.equals("1")){
            // 没有阅读权限
            String title = "";
            if(uid.equals("0")){
                title = "本章节仅限会员阅读";
            }else{
                title = "本章节仅限VIP会员阅读";
            }
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("提示")
                    .setContentText(title)
                    .setConfirmText("关闭")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }else {
            String title = "";
            if (errcode.equals("2")) {
                title = "该章节为付费课程,是否花费"+jiage+"积分购买";
            } else if (errcode.equals("3")) {
                title = "该课程为付费课程,是否花费"+jiage+"积分购买";
            }
            // 付费章节没有购买
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("提示")
                    .setContentText(title)
                    .setConfirmText("购买")
                    .setCancelText("关闭")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            topayFir(id, type);
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
    }

    private void topayFir(final String id, String type) {

        final RequestParams param = new RequestParams();
        param.put("member_id", member_id);
        param.put("bcid", id);
        param.put("type", 2);


        HttpUtil.post("author.php/Nexts/buybooks", param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = JSON.parseObject(new String(responseBody,"utf8"));
                    payResult(jsonObject.getString("errcode"), id,jsonObject.getString("dataList"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    getTyChaterrmsg("author.php/Nexts/buybooks",param.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                getTyChaterrmsg("author.php/Nexts/buybooks",param.toString());

            }
        });
    }

    private void payResult(String code, String id,String fee){

        if(code.equals("0")){

            SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
            editor.putString("fee", L.encrypt(fee,MenModel.LKEY));
            editor.commit();
            // 购买成功
            Intent intent = new Intent();
            intent.setClass(BookActivity.this, TemplateActivity.class);
            intent.putExtra("id",id);
            intent.putExtra("books_id",books_id);
            intent.putExtra("type","zhangjie");
            startActivity(intent);
        }else if (code.equals("4")){
            new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("提示")
                    .setContentText("余额不足")
                    .setConfirmText("前往充值")
                    .setCancelText("关闭")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            // 前往充值页面
                            Intent intent = new Intent(BookActivity.this, RecargeGuaidouActivity.class);
                            startActivity(intent);
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    }).show();
        } else if(code.equals("5")){
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("提示")
                    .setContentText("网络连接失败")
                    .setConfirmText("关闭")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    }).show();
        }
    }




    private void setDiscussInfo(JSONArray comment_list) {
        discussList = new ArrayList<HashMap<String, String>>();
        discussList.clear();
        for (int i = 0; i < comment_list.size(); i++) {
            JSONObject temp = comment_list.getJSONObject(i);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("nick_name", temp.getString("nickName"));
            map.put("head_img", "http://" + temp.getString("head_img"));
            map.put("content", temp.getString("content"));
            map.put("number", temp.getString("zsum"));
            map.put("otime_date", temp.getString("otimeDate"));
            map.put("otime_time", temp.getString("otimeTime"));
            map.put("comment_id", temp.getString("commentId"));
            map.put("to_user_id",temp.getString("auserId"));
            discussList.add(map);
        }
        discussListView = (ListViewLinearlayout) findViewById(R.id.discuss);
        discussListView.removeAllViews();
        discussAdapter = new LinearLayoutListViewBookDiscussAdapter(BookActivity.this, discussList);
        discussListView.setAdapter(discussAdapter);

        discussListView.setOnItemClickListener(new ListViewLinearlayout.OnItemClickListener(){

            @Override
            public void onItemClicked(View v, Object obj, int position) {
                Intent intent = new Intent();
                intent.setClass(BookActivity.this, CommentInfoActivity.class);
                intent.putExtra("comment_id", discussList.get(position).get("comment_id"));
                intent.putExtra("nick_name", discussList.get(position).get("nick_name"));
                intent.putExtra("head_img", discussList.get(position).get("head_img"));
                intent.putExtra("content", discussList.get(position).get("content"));
                intent.putExtra("date", discussList.get(position).get("otime_date"));
                intent.putExtra("timer", discussList.get(position).get("otime_time"));
                intent.putExtra("id", books_id);
                intent.putExtra("template_type", "book");
                startActivity(intent);
            }
        });
    }


    @butterknife.OnClick({R.id.show_all,R.id.xiaolian,R.id.mEditEmojicon,R.id.back_left,R.id.button2})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.show_all:

                Intent intent = new Intent();
                intent.setClass(BookActivity.this, CommentListActivity.class);
                intent.putExtra("id",books_id);
                intent.putExtra("title",books_name);
                intent.putExtra("template_type","book");
                startActivity(intent);
                break;
            case R.id.xiaolian:
                emojicons.setVisibility(View.VISIBLE);
                break;
            case R.id.mEditEmojicon:
                emojicons.setVisibility(View.GONE);
                break;
            case R.id.button2:
            case R.id.back_left:
                finish();
                break;
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mEditEmojicon, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEditEmojicon);
    }

    @OnClick({R.id.zan_img,R.id.thumb,R.id.sou_img,R.id.collection})
    public void clickNum(View view){
        switch (view.getId()){
            case R.id.zan_img:
                setZan();
                break;
            case R.id.thumb:
                setZan();
                break;
            case R.id.sou_img:
                setSou();
                break;
            case R.id.collection:
                setSou();
                break;
        }
    }

    private void setSou() {
        String member_id = MenModel.member_id;
        String sta = "-";
        String is_save = sou_img.getTag(R.id.is_save).toString();
        int number = Integer.parseInt(collectiontext.getText().toString());
        if(is_save.equals("1")){
            sou_img.setImageResource(R.drawable.bshow_05);
            number--;
            sou_img.setTag(R.id.is_save,0);
        }else{
            sta = "+";
            sou_img.setImageResource(R.drawable.bshow_06);
            number++;
            sou_img.setTag(R.id.is_save,1);
        }
        collectiontext.setText(String.valueOf(number));
        CustomerUtil.changeSaveOrZan(books_id, "sou", sta, member_id);
    }

    private void setZan() {
        String member_id = MenModel.member_id;
        String sta = "-";
        String is_zan = zan_img.getTag(R.id.is_zan).toString();
        int number = Integer.parseInt(thumbtext.getText().toString());
        if(is_zan.equals("1")){
            zan_img.setImageResource(R.drawable.bshow_03);
            number--;
            zan_img.setTag(R.id.is_zan,0);
        }else{
            sta = "+";
            zan_img.setImageResource(R.drawable.bshow_04);
            number++;
            zan_img.setTag(R.id.is_zan,1);
        }
        thumbtext.setText(String.valueOf(number));
        CustomerUtil.changeSaveOrZan(books_id, "zan", sta , member_id);
    }

    public void backFinish(){
        finish();
    }
}
