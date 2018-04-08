package com.zan99.guaizhangmen.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ldoublem.loadingviewlib.view.LVEatBeans;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.Men.RecargeGuaidouActivity;
import com.zan99.guaizhangmen.Adapter.BookChapterAdapter;
import com.zan99.guaizhangmen.Adapter.LinearLayoutListViewBookDiscussAdapter;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Service.DownloadService;
import com.zan99.guaizhangmen.SqlUtil.BooksDatabaseHelper;
import com.zan99.guaizhangmen.Util.CircleTransform;
import com.zan99.guaizhangmen.Util.Consts;
import com.zan99.guaizhangmen.Util.CustomerUtil;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.Widget.ListViewLinearlayout;
import com.zan99.videoview.TxVideoPlayerController;
import com.zan99.videoview.Util;
import com.zan99.videoview.VideoPlayer;
import com.zan99.videoview.VideoPlayerManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

/**
 * Created by Administrator on 2017/11/28.
 */

public class TemplateActivity extends BaseActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private BooksDatabaseHelper booksDatabaseHelper;
    private SQLiteDatabase db;

    private String id;
    private String template_type;
    private String books_id;
    VideoPlayer videoPlayer;
    TxVideoPlayerController playerController;
    MediaPlayer audioPlayer;
    private TextView position;
    private TextView duration;
    private ImageView mKuaijin;
    private ImageView mkuaitui;
    private ImageView upData;
    private ImageView mRestartPaus;


    @BindView(R.id.back_left)
    Button back_left;
    @BindView(R.id.back_layout)
    LinearLayout back_layout;


    @BindView(R.id.info_title)
    TextView info_title;
    @BindView(R.id.info_time)
    TextView info_time;
    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.discuss_layout)
    LinearLayout discuss_layout;
    @BindView(R.id.bottominput)
    LinearLayout bottominput;
    @BindView(R.id.emojicons)
    FrameLayout emojicons;
    @BindView(R.id.emoji_btn)
    Button emoji_btn;
    @BindView(R.id.ltzan)
    LinearLayout ltzan;
    @BindView(R.id.zan_img)
    ImageView zan_img;
    @BindView(R.id.thumb)
    TextView thumb;
    @BindView(R.id.empty)
    View empty;
    @BindView(R.id.show_all)
    TextView show_all;

    @BindView(R.id.loadView)
    LinearLayout loadView;
    @BindView(R.id.bodyView)
    LinearLayout bodyView;
    @BindView(R.id.loadingAni)
    LVEatBeans loadingAni;
    private SeekBar seek;
    private int durationNum;
    private int positionNum;
    private boolean audioIsPlay = false;
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;
    private ArrayList<HashMap<String, String>> discussList;
    private ListViewLinearlayout discussListView;
    private boolean audioPlayerIsRelease = false;
    private SweetAlertDialog pDialog;
    private Boolean isComment = false;
    private EmojiconEditText mEditEmojicon;
    private LinearLayoutListViewBookDiscussAdapter discussAdapter;
    private ArrayList<HashMap<String, String>> chapterList;
    private String audioSrc;
    private String audioName;

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    updateProgress();
                    break;
                case 2:
//                    loadOver();
                    videoPlayer.start();
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_template);
        ButterKnife.bind(this);
        loadingAni.startAnim();
        mEditEmojicon = (EmojiconEditText) findViewById(R.id.mEditEmojicon);
        id = getIntent().getStringExtra("id");
        template_type = getIntent().getStringExtra("type");

        loadData();//加载页面
//        content.addView(getMp3View("abv.cn/music/光辉岁月.mp3"));
//        loadOver();
        creatSql();

        back_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        back_layout.setVisibility(View.VISIBLE);
    }

    /**
     * 创建音频数据库
     */
    private void creatSql() {
        booksDatabaseHelper = new BooksDatabaseHelper(this, Consts.DATABASE_VERSION);
        db = booksDatabaseHelper.getWritableDatabase();
        db.close();
    }


    @Override
    protected void onStart() {
        super.onStart();
//        content.removeAllViews();
        pDialog = loadingpDialog(this);
        //pDialog.show();

        setEmojiconFragment(false);

        mEditEmojicon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    emojicons.setVisibility(View.GONE);
                    //Toast.makeText(TemplateActivity.this,"此处为得到焦点时的处理内容",Toast.LENGTH_SHORT).show();
                } else {
                    // 此处为失去焦点时的处理内容
                    //Toast.makeText(TemplateActivity.this,"此处为失去焦点时的处理内容",Toast.LENGTH_SHORT).show();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                }
            }
        });
    }

    private void loadData() {

        verifyStoragePermissions(this);

        String url = "";

        final RequestParams params = new RequestParams();
        params.add("member_id", MenModel.member_id);

        if (template_type.equals("fangtan")) {

            // 访谈  开启评论
            initPingLunView();

            if (MenModel.member_id.equals("0") || MenModel.member_id.equals("")) {
                bottominput.setVisibility(View.GONE);
                ltzan.setVisibility(View.GONE);
            } else {

                bottominput.setVisibility(View.VISIBLE);
                ltzan.setVisibility(View.VISIBLE);
            }

            url = "admin.php/Systeminterface/interview_info";
            params.add("id", id);
        } else if (template_type.equals("jiangshi")) {
            url = "admin.php/Systeminterface/author_info";
            params.add("author_id", id);
        } else if (template_type.equals("zhangjie")) {
            books_id = getIntent().getStringExtra("books_id");
            url = "author.php/Nexts/commentContent";
            params.add("chapter_id", id);
            params.add("books_id", books_id);
        }
        final String path = url;
        HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (statusCode == 200) {
                        JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf8"));
                        if (jsonObject.getString("errcode").equals("0")) {
                            if (template_type.equals("fangtan")) {
                                initViewFT(jsonObject.getJSONObject("dataList"));
                            } else if (template_type.equals("zhangjie")) {

                                initViewzj(jsonObject.getJSONObject("dataList")); //dataList
                            } else if (template_type.equals("jiangshi")) {
                                initViewJs(jsonObject.getJSONObject("dataList"));
                            }
//                            loadOver();
                        } else {
                            setEmptyPager();
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    setEmptyPager();
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg(path, params.toString());
                }
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 显示空页面
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                setEmptyPager();
                BaseActivity.getTyChaterrmsg(path, params.toString());

            }
        });
    }

    private void loadOver() {
        loadingAni.stopAnim();
        loadView.setVisibility(View.GONE);
        bodyView.setVisibility(View.VISIBLE);
    }

    private void initPingLunView() {

        //bottominput.setVisibility(View.VISIBLE);
        emoji_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojicons.setVisibility(View.GONE);
                String text = mEditEmojicon.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    insertComment(text);
                }
            }
        });
    }


    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }


    private void insertComment(String text) {
        final RequestParams params = new RequestParams();
        params.put("point_id", "0");
        params.put("content", text);
        params.put("interview_id", id);
        params.put("auser_id", MenModel.member_id);

        HttpUtil.post("admin.php/Systeminterface/insertdiscuss", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {
                        JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf8"));
                        String errcode = jsonObject.getString("errcode");
                        String errmsg = jsonObject.getString("errmsg");
                        if (errcode != "0") {
                            L.d(errmsg + ":" + errcode);
                            Toast.makeText(TemplateActivity.this, errmsg + ":" + errcode, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TemplateActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                        }

                        if (jsonObject.getString("errcode").equals("0")) {
                            addDiscussWai(jsonObject.getJSONObject("dataList"));
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/insertdiscuss", params.toString());
                    }
                    //hideSoftInputFromWindow(TemplateActivity.this);
                    emojicons.setVisibility(View.GONE);
                    mEditEmojicon.setText("");
                    mEditEmojicon.setHint("评论内容");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/insertdiscuss", params.toString());

            }
        });
    }

    private void addDiscussWai(JSONObject temp) {

        if (discussList.size() > 0) {
            discussList.remove(discussList.size() - 1);
        }

        HashMap<String, String> map = new HashMap<String, String>();

        map.put("comment_id", temp.getString("interviewId"));
        map.put("to_user_id", "");
        map.put("content", temp.getString("content"));
        map.put("nick_name", temp.getString("names"));
        map.put("head_img", "http://" + temp.getString("head_img"));
        map.put("timer", temp.getString("time"));
        map.put("date", temp.getString("date"));
        map.put("number", "0");

        map.put("to_name", "");

        discussList.add(0, map);

        discussAdapter.notifyDataSetChanged();

    }

    private void initViewJs(JSONObject jsonobj) {
        JSONArray jsonArray = jsonobj.getJSONArray("list");
        if (jsonArray.isEmpty()) {
            return;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject tj = jsonArray.getJSONObject(i);
            if (tj.getString("title").equals("text")) {
                content.addView(getTextView(tj.getString("src")));
            } else if (tj.getString("title").equals("img")) {
                content.addView(getImageView(tj.getString("src")));
            }
        }

        String name = jsonobj.getString("name");
        String img = jsonobj.getString("img");
        String prof = jsonobj.getString("profile");
        TextView title = (TextView) findViewById(R.id.title);
        TextView jsname = (TextView) findViewById(R.id.jsname);
        ImageView header = (ImageView) findViewById(R.id.head_img);
        TextView profile = (TextView) findViewById(R.id.profile);
        findViewById(R.id.jiangshi).setVisibility(View.VISIBLE);

        title.setText(name);
        jsname.setText(name);
        profile.setText(prof);
        Picasso.with(this).load("http://" + img).transform(new CircleTransform()).into(header);
        loadOver();
    }


    private void setEmptyPager() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.empty);
        content.addView(imageView, lp);
        emojicons.setVisibility(View.GONE);
        ltzan.setVisibility(View.GONE);
    }

    private void initViewFT(JSONObject dataList) {

        JSONObject info = dataList.getJSONObject("infolist");

        info_title.setText(info.getString("name"));
        info_time.setText(info.getString("otime"));
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(info.getString("name"));
        Button backBtn = (Button) findViewById(R.id.back_left);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        JSONArray contentInfo = info.getJSONArray("list");

        setViews(contentInfo);

        discuss_layout.setVisibility(View.VISIBLE);
        setDiscussView(dataList.getJSONArray("discuss"));

        String isLike = info.getString("isLike");
        String givelike = info.getString("givelike");
        if (isLike.equals("1")) {
            zan_img.setImageResource(R.drawable.bshow_04);
            zan_img.setTag(R.id.is_zan, 1);
        } else {
            zan_img.setTag(R.id.is_zan, 0);
        }
        thumb.setText(givelike);

    }


    private void initViewzj(JSONObject jsonObject) {
        JSONObject dataList = jsonObject.getJSONObject("chapterId");
        audioName = dataList.getString("chaptreName");
        info_title.setText(audioName);
        info_time.setText(dataList.getString("createTime"));
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(audioName);
        Button backBtn = (Button) findViewById(R.id.back_left);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        JSONArray contentInfo = dataList.getJSONArray("list");
        if (contentInfo != null) {
            setViews(contentInfo);
        }

        JSONArray chapterlist = jsonObject.getJSONArray("chapterlist");
        if (chapterlist != null) {
            setChapterview(chapterlist);
        }
    }

    private void setChapterview(JSONArray chapterlist) {
        GridView chapterlistview = (GridView) findViewById(R.id.chapter);
        LinearLayout ll = (LinearLayout) findViewById(R.id.chaptertitle);
        ll.setVisibility(View.VISIBLE);
        chapterList = new ArrayList<HashMap<String, String>>();


        for (int i = 0; i < chapterlist.size(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            JSONObject temp = chapterlist.getJSONObject(i);
            map.put("durationMsec", temp.getString("durationMsec"));
            map.put("createTime", temp.getString("createTime"));
            map.put("chapterImg", temp.getString("chapterImg"));
            map.put("chaptreName", temp.getString("chaptreName"));
            map.put("id", temp.getString("chapterId"));
            chapterList.add(map);
        }


        BookChapterAdapter adapter = new BookChapterAdapter(this, chapterList, id);
        chapterlistview.setAdapter(adapter);

        chapterlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!id.equals(chapterList.get(i).get("id").toString())) {
                    checkCanRead(chapterList.get(i).get("id").toString());
                }

            }
        });


    }

    private void checkCanRead(final String chapter_id) {
        final RequestParams param = new RequestParams();
        String member_id = MenModel.member_id;
        if (member_id.equals("")) {
            member_id = "0";
        }

        param.put("member_id", member_id);   // 用户ID
        param.put("chapter_id", chapter_id);
        HttpUtil.post("author.php/Nexts/clickchapter", param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonobeject = JSON.parseObject(new String(responseBody, "utf8"));
                    System.out.println("result:" + new String(responseBody, "utf8"));
                    if (jsonobeject.getString("errcode").equals("0")) {
                        //books_id
//                        finish();
//                        Intent intent = new Intent(TemplateActivity.this, TemplateActivity.class);
//                        intent.putExtra("type","zhangjie");
//                        intent.putExtra("books_id",books_id);
//                        intent.putExtra("id",chapter_id);
//                        startActivity(intent);
                        content.removeAllViews();
                        id = chapter_id;
                        pDialog.show();
                        loadData();
                    } else {
                        String type = "";
                        String payjiage = "0";
                        if (!jsonobeject.getString("errcode").equals("1")) {
                            JSONObject tj = jsonobeject.getJSONObject("dataList");
                            type = tj.getString("type");
                            payjiage = tj.getString("money");
                        }
                        showAlertDialog(jsonobeject.getString("errcode"), chapter_id, type, payjiage);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg("author.php/Nexts/clickchapter", param.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg("author.php/Nexts/clickchapter", param.toString());
            }
        });
    }

    public void showAlertDialog(String errcode, final String id, final String type, final String jiage) {
        String uid = MenModel.member_id;
        if (errcode.equals("1")) {
            // 没有阅读权限
            String title = "";
            if (uid.equals("0")) {
                title = "本章节仅限会员阅读";
            } else {
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
        } else {
            String title = "";
            if (errcode.equals("2")) {
                title = "该章节为付费章节,是否花费" + jiage + "积分购买";
            } else if (errcode.equals("3")) {
                title = "该课程为付费课程,是否花费" + jiage + "积分购买";
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
        param.put("member_id", MenModel.member_id);
        param.put("bcid", id);
        param.put("type", type);
        L.d(param.toString());

        HttpUtil.post("author.php/Nexts/buybooks", param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf8"));
                    payResult(jsonObject.getString("errcode"), id);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg("author.php/Nexts/buybooks", param.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg("author.php/Nexts/buybooks", param.toString());
                try {
                    L.d(new String(responseBody, "utf8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void payResult(String code, String chapter_id) {
        if (code.equals("0")) {
            // 购买成功
//            Intent intent = new Intent();
//            intent.setClass(TemplateActivity.this, TemplateActivity.class);
//            intent.putExtra("id",id);
//            intent.putExtra("books_id",books_id);
//            intent.putExtra("type","zhangjie");
//            startActivity(intent);
//            finish();
            content.removeAllViews();
            id = chapter_id;
            pDialog.show();
            loadData();
        } else if (code.equals("4")) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("提示")
                    .setContentText("余额不足")
                    .setConfirmText("前往充值")
                    .setCancelText("关闭")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            // 前往充值页面
                            Intent intent = new Intent(TemplateActivity.this, RecargeGuaidouActivity.class);
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
        } else if (code.equals("5")) {
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


    private void setViews(JSONArray contentInfo) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.topMargin = 8;
        for (int i = 0; i < contentInfo.size(); i++) {
            JSONObject js = contentInfo.getJSONObject(i);
            if (js.getString("title").equals("text")) {
                content.addView(getTextView(js.getString("src")), lp);
            } else if (js.getString("title").equals("video")) {
                getVideoView(js);
//                content.addView(getMp3View("abv.cn/music/光辉岁月.mp3"), lp);
            } else if (js.getString("title").equals("img")) {
                content.addView(getImageView(js.getString("src")), lp);
            } else if (js.getString("title").equals("audio")) {
                content.addView(getMp3View(js.getString("src")), lp);
            }
        }
    }

    private void setDiscussView(JSONArray discuss) {
        discussList = new ArrayList<HashMap<String, String>>();
        if (!discuss.isEmpty()) {
            System.out.println("discuss.size():" + discuss.size());
            for (int i = 0; i < discuss.size(); i++) {
                JSONObject temp = discuss.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("nick_name", temp.getString("name"));
                map.put("head_img", "http://" + temp.getString("headimg"));
                map.put("content", temp.getString("content"));
                map.put("number", temp.getString("count"));
                map.put("otime_date", temp.getString("date"));
                map.put("otime_time", "time");
                map.put("comment_id", temp.getString("discuss_id")); //id
                map.put("to_user_id", "0");
                discussList.add(map);
            }
            discussListView = (ListViewLinearlayout) findViewById(R.id.discuss);
            discussListView.removeAllViews();
            discussAdapter = new LinearLayoutListViewBookDiscussAdapter(this, discussList);
            discussListView.setAdapter(discussAdapter);
            discussListView.setOnItemClickListener(new ListViewLinearlayout.OnItemClickListener() {
                @Override
                public void onItemClicked(View parent, Object view, int position) {
                    String title = info_title.getText().toString();
                    String comment_id = discussList.get(position).get("comment_id");
                    /*
                    Intent intent = new Intent();
                    intent.setClass(TemplateActivity.this, CommentListActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("id", comment_id);
                    intent.putExtra("template_type",template_type);
                    startActivity(intent);
                    */

                    Intent intent = new Intent();
                    intent.setClass(TemplateActivity.this, CommentInfoActivity.class);
                    intent.putExtra("comment_id", discussList.get(position).get("comment_id"));
                    intent.putExtra("nick_name", discussList.get(position).get("nick_name"));
                    intent.putExtra("head_img", discussList.get(position).get("head_img"));
                    intent.putExtra("content", discussList.get(position).get("content"));
                    intent.putExtra("date", discussList.get(position).get("otime_date"));
                    intent.putExtra("timer", discussList.get(position).get("otime_time"));

                    intent.putExtra("id", id);
                    intent.putExtra("template_type", template_type);
                    startActivity(intent);
                }

            });
            discuss_layout.setVisibility(View.VISIBLE);
        }
    }

    private void getVideoView(JSONObject jsonObject) {

        videoPlayer = new VideoPlayer(this);
        videoPlayer.setUp("http://" + jsonObject.getString("src"), null);
        playerController = new TxVideoPlayerController(this);
        playerController.setTitle("");
        videoPlayer.setController(playerController);
        Picasso.with(this)
                .load(jsonObject.getString("snapshotUrl"))
                .into(playerController.imageView(), new Callback() {
                    @Override
                    public void onSuccess() {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lp.topMargin = 8;
                        lp.bottomMargin = 10;
                        content.addView(videoPlayer, 0, lp);
                        loadOver();
                        handler.sendMessageDelayed(handler.obtainMessage(2), 1000);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private View getTextView(String text) {
        View view = View.inflate(this, R.layout.template_text_view, null);
        TextView textView = view.findViewById(R.id.text_view);
        textView.setText(text);
        return view;
    }

    private View getImageView(String src) {
        View view = View.inflate(this, R.layout.template_image_view, null);
        ImageView imageView = view.findViewById(R.id.imgage_view);
        Picasso.with(this).load("http://" + src).into(imageView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(imageView.getLayoutParams());
        lp.setMargins(0, 10, 0, 10);
        imageView.setLayoutParams(lp);
        imageView.setAdjustViewBounds(true);
        return view;
    }

    private View getMp3View(String src) {
        audioSrc = src;
        LinearLayout view = (LinearLayout) View.inflate(this, R.layout.view_audio_controller, null);
        mRestartPaus = view.findViewById(R.id.restart_or_pause);
        position = view.findViewById(R.id.position);
        duration = view.findViewById(R.id.duration);
        mKuaijin = view.findViewById(R.id.kuaijin);
        mkuaitui = view.findViewById(R.id.kuaitui);
        upData = view.findViewById(R.id.updata);
        loadOver();
        upData.setOnClickListener(this);
        mRestartPaus.setOnClickListener(this);
        seek = (SeekBar) view.findViewById(R.id.seek);

        mKuaijin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ps = audioPlayer.getCurrentPosition() + 10000;
                if (ps < audioPlayer.getDuration()) {
                    audioPlayer.seekTo(ps);
                    position.setText(Util.formatTime(ps));
                }
            }
        });

        mkuaitui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ps = audioPlayer.getCurrentPosition() - 10000;
                if (ps > 0) {
                    audioPlayer.seekTo(ps);
                    position.setText(Util.formatTime(ps));
                }
            }
        });

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int ps = seekBar.getProgress();
                int seekto = durationNum * ps / 100;
                audioPlayer.seekTo(seekto);
                position.setText(Util.formatTime(seekto));
            }
        });

        audioPlayer = MediaPlayer.create(this, Uri.parse("http://" + src));
        audioPlayer.setLooping(true);
        audioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                AudioPlaySta.mCurrentState = AudioPlaySta.STATE_PREPARED;
            }
        });
        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                AudioPlaySta.mCurrentState = AudioPlaySta.STATE_COMPLETED;
                cancelUpdateProgressTimer();
                resetAudioPlayer();
            }
        });
        audioPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    // 播放器开始渲染
                    AudioPlaySta.mCurrentState = AudioPlaySta.STATE_PLAYING;
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    // MediaPlayer暂时不播放，以缓冲更多的数据
                    if (AudioPlaySta.mCurrentState == AudioPlaySta.STATE_PAUSED || AudioPlaySta.mCurrentState == AudioPlaySta.STATE_BUFFERING_PAUSED) {
                        AudioPlaySta.mCurrentState = AudioPlaySta.STATE_BUFFERING_PAUSED;
                    } else {
                        AudioPlaySta.mCurrentState = AudioPlaySta.STATE_BUFFERING_PLAYING;
                    }
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    // 填充缓冲区后，MediaPlayer恢复播放/暂停
                    if (AudioPlaySta.mCurrentState == AudioPlaySta.STATE_BUFFERING_PLAYING) {
                        AudioPlaySta.mCurrentState = AudioPlaySta.STATE_PLAYING;
                    }
                }
                return true;
            }
        });
        audioPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                AudioPlaySta.mBufferPercentage = i;
            }
        });
        durationNum = audioPlayer.getDuration();
        duration.setText(Util.formatTime(durationNum));

        return view;
    }

    private void resetAudioPlayer() {
        mRestartPaus.setImageResource(R.drawable.mediaplayer_start);
        audioPlayer.stop();
        audioPlayer.reset();
        seek.setProgress(0);
        audioIsPlay = false;//后加
        position.setText(Util.formatTime(0));//后加
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPlayer != null) {
            videoPlayer.stopPlayWithBackground();
            VideoPlayerManager.instance().releaseNiceVideoPlayer();
        }
        if (videoPlayer != null) {
            videoPlayer.releasePlayer();
            VideoPlayerManager.instance().releaseNiceVideoPlayer();
        }
        if (audioPlayer != null) {
            try {
                mUpdateProgressTimer = null;
                audioPlayer.reset();
                audioPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }

            audioPlayerIsRelease = true;


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(audioPlayer != null){
//            audioPlayer.pause();
//        }
    }

    @butterknife.OnClick({R.id.xiaolian, R.id.mEditEmojicon, R.id.ltzan, R.id.show_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.restart_or_pause:
                if (audioPlayer.isPlaying()) {
                    audioIsPlay = false;
                    mRestartPaus.setImageResource(R.drawable.mediaplayer_start);
                    audioPlayer.pause();
                } else {
                    audioIsPlay = true;
                    mRestartPaus.setImageResource(R.drawable.mediaplayer_pause);
                    audioPlayer.start();
                    startUpdateProgressTimer();
                }

                break;
            case R.id.updata:

                boolean isUpdata = false;

                if (MenModel.member_id.equals("0")) {
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("提示")
                            .setContentText("还没有登录，是否去登录")
                            .setConfirmText("确定")
                            .setCancelText("取消")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent = new Intent(TemplateActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else {
                    booksDatabaseHelper = new BooksDatabaseHelper(this, Consts.DATABASE_VERSION);
                    db = booksDatabaseHelper.getWritableDatabase();
                    Cursor c = db.query("chapters", null, null, null, null, null, null);//查询并获得游标
                    if (c.getCount() > 0) {
                        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                            String chapterId = c.getString(c.getColumnIndex("chapterId"));
                            String memberId = c.getString(c.getColumnIndex("member_id"));
                            if (id.equals(chapterId)&&MenModel.member_id.equals(memberId)) {
                                isUpdata = true;
                            }
                        }
                    }
                    if (c.getCount() == 0) {
                        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("提示")
                                .setContentText("是否下载当前音频")
                                .setConfirmText("确定")
                                .setCancelText("取消")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        //开启下载的service
                                        Intent serviceIntent = new Intent(TemplateActivity.this, DownloadService.class);
                                        serviceIntent.putExtra("book_id", books_id);
                                        serviceIntent.putExtra("chapter_id", id);
                                        serviceIntent.putExtra("duration", Util.formatTime(durationNum));
                                        serviceIntent.putExtra("audioSrc", audioSrc);
                                        serviceIntent.putExtra("audioName", audioName);
                                        startService(serviceIntent);

                                        Toast.makeText(TemplateActivity.this, "下载！！！", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                        c.close();
                        db.close();
                    } else if (isUpdata) {
                        Toast.makeText(TemplateActivity.this, "已下载，可在我的下载中查看", Toast.LENGTH_SHORT).show();
                        c.close();
                        db.close();
                    } else {
                        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("提示")
                                .setContentText("是否下载当前音频")
                                .setConfirmText("确定")
                                .setCancelText("取消")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        //开启下载的service
                                        Intent serviceIntent = new Intent(TemplateActivity.this, DownloadService.class);
                                        serviceIntent.putExtra("book_id", books_id);
                                        serviceIntent.putExtra("chapter_id", id);
                                        serviceIntent.putExtra("duration", Util.formatTime(durationNum));
                                        serviceIntent.putExtra("audioSrc", audioSrc);
                                        serviceIntent.putExtra("audioName", audioName);
                                        startService(serviceIntent);

                                        Toast.makeText(TemplateActivity.this, "下载！！！", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                        c.close();
                        db.close();
                    }
                }

                //开启下载的service
//                Intent serviceIntent = new Intent(this, DownloadService.class);
//                serviceIntent.putExtra("book_id", books_id);
//                serviceIntent.putExtra("chapter_id", id);
//                serviceIntent.putExtra("duration", Util.formatTime(durationNum));
//
//                Toast.makeText(TemplateActivity.this, "下载！！！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.xiaolian:
//                mEditEmojicon.clearFocus();
                emojicons.setVisibility(View.VISIBLE);
                break;
            case R.id.mEditEmojicon:

                emojicons.setVisibility(View.GONE);
                break;
            case R.id.ltzan:
                setZan();
                break;
            case R.id.show_all:
                String title = info_title.getText().toString();
                //String comment_id = discussList.get(position).get("comment_id");

                Intent intent = new Intent();
                intent.setClass(TemplateActivity.this, CommentListActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("id", id);
                intent.putExtra("template_type", template_type);
                startActivity(intent);

                break;
        }
    }

    private void setZan() {
        String member_id = MenModel.member_id;
        String sta = "-";
        String is_zan = zan_img.getTag(R.id.is_zan).toString();

        int number = Integer.parseInt(thumb.getText().toString());
        if (is_zan.equals("1")) {
            zan_img.setImageResource(R.drawable.bshow_03);
            number--;
            zan_img.setTag(R.id.is_zan, 0);
        } else {
            sta = "+";
            zan_img.setImageResource(R.drawable.bshow_04);
            number++;
            zan_img.setTag(R.id.is_zan, 1);
        }
        thumb.setText(String.valueOf(number));
        System.out.println("id:" + id);
        System.out.println("sta:" + sta);
        System.out.println("member_id:" + member_id);
        CustomerUtil.changeLTZan(id, sta, member_id);
    }


    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }

    /**
     * 取消更新进度的计时器。
     */
    private void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    private void updateProgress() {
        if (!audioPlayerIsRelease) {
            int tposition = audioPlayer.getCurrentPosition();
            int tduration = audioPlayer.getDuration();
//        int bufferPercentage = audioPlayer.getBufferPercentage();
//        seek.setSecondaryProgress(bufferPercentage);
            int progress = (int) (100f * tposition / tduration);
            int secondProgress = AudioPlaySta.mBufferPercentage;
            seek.setProgress(progress);
            seek.setSecondaryProgress(secondProgress);
            position.setText(Util.formatTime(tposition));
            duration.setText(Util.formatTime(tduration));
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


    static class AudioPlaySta {
        public static int mCurrentState = 0;
        public static int mBufferPercentage = 0;
        /**
         * 播放错误
         **/
        public static final int STATE_ERROR = -1;
        /**
         * 播放未开始
         **/
        public static final int STATE_IDLE = 0;
        /**
         * 播放准备中
         **/
        public static final int STATE_PREPARING = 1;
        /**
         * 播放准备就绪
         **/
        public static final int STATE_PREPARED = 2;
        /**
         * 正在播放
         **/
        public static final int STATE_PLAYING = 3;
        /**
         * 暂停播放
         **/
        public static final int STATE_PAUSED = 4;
        /**
         * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
         **/
        public static final int STATE_BUFFERING_PLAYING = 5;
        /**
         * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
         **/
        public static final int STATE_BUFFERING_PAUSED = 6;
        /**
         * 播放完成
         **/
        public static final int STATE_COMPLETED = 7;
    }


}
