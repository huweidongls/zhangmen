package com.zan99.guaizhangmen.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Adapter.MyUpdataMediaplayerAdapter;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.Model.MyUpdataMediaPlayerEntity;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.SqlUtil.BooksDatabaseHelper;
import com.zan99.guaizhangmen.Util.Consts;
import com.zan99.videoview.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SuopingMediaPlayerActivity extends SwipeBackActivity {

    SwipeBackLayout swipeBackLayout;

    private BooksDatabaseHelper booksDatabaseHelper;
    private SQLiteDatabase db;

    @BindView(R.id.myupdata_mediaplayer_rl)
    RelativeLayout rl;
    @BindView(R.id.myupdata_mediaplayer_book_pic)
    ImageView bookImg;
    @BindView(R.id.myupdata_mediaplayer_book_name)
    TextView bookName;
    @BindView(R.id.myupdata_mediaplayer_book_yanjiang)
    TextView authorName;
    @BindView(R.id.myupdata_mediaplayer_seek)
    SeekBar seek;
    @BindView(R.id.myupdata_mediaplayer_position)
    TextView position;
    @BindView(R.id.myupdata_mediaplayer_duration)
    TextView duration;
    @BindView(R.id.myupdata_mediaplayer_menu)
    ImageView menu;
    @BindView(R.id.myupdata_mediaplayer_pre)
    ImageView pre;
    @BindView(R.id.myupdata_mediaplayer_start_or_pause)
    ImageView mRestartPaus;
    @BindView(R.id.myupdata_mediaplayer_next)
    ImageView next;
    @BindView(R.id.mediaplayer_list_rl)
    RelativeLayout rlMenu;
    @BindView(R.id.mediaplayer_list)
    ListView menuList;
    @BindView(R.id.mediaplayer_close_list)
    TextView closeMenu;

    private List<MyUpdataMediaPlayerEntity> data = new ArrayList<>();
    /**
     * 书籍id
     */
    private String bookId;
    /**
     * 当前播放音频
     */
    private int currentPlay;
    /**
     * 当前是否播放
     */
    private boolean audioIsPlay = true;
    private String booksName;
    private String authorsName;
    private String booksImg;
    private int durationNum;

    private MyUpdataMediaplayerAdapter adapter;

    private Animation translate, translatet;

    private SuopingBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_suoping_media_player);

        ButterKnife.bind(this);

        swipeBackLayout = getSwipeBackLayout();

        // 可以调用该方法，设置是否允许滑动退出
        setSwipeBackEnable(true);
        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
        // 滑动退出的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
        swipeBackLayout.setEdgeSize(400);

        bookId = getIntent().getStringExtra("bookid");
        currentPlay = getIntent().getIntExtra("currentPlay", 0);
        
        init();

    }

    private class SuopingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Consts.ACTION_UPDATA_SUOPING:
                    int tposition = intent.getIntExtra("position", -1);
                    int tduration = intent.getIntExtra("duration", -1);
                    int progress = intent.getIntExtra("progress", -1);
                    seek.setProgress(progress);
                    position.setText(Util.formatTime(tposition));
                    duration.setText(Util.formatTime(tduration));
                    durationNum = tduration;
                    break;
                case Consts.ACTION_MEDIA_PLAY_COMPLETE:
                    currentPlay = intent.getIntExtra("complete", 0);
                    adapter.setIndex(currentPlay);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private void init() {

        receiver = new SuopingBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Consts.ACTION_UPDATA_SUOPING);
        intentFilter.addAction(Consts.ACTION_MEDIA_PLAY_COMPLETE);
        registerReceiver(receiver, intentFilter);

        translate = AnimationUtils.loadAnimation(this, R.anim.translate);
        translatet = AnimationUtils.loadAnimation(this, R.anim.translatet);

        booksName = getIntent().getStringExtra("booksName");
        authorsName = getIntent().getStringExtra("authorsName");
        booksImg = getIntent().getStringExtra("booksImg");

        bookName.setText(booksName);
        authorName.setText(authorsName);
        Picasso.with(this).load("http://" + booksImg).into(bookImg);

        data = getData();
        adapter = new MyUpdataMediaplayerAdapter(this, data);
        adapter.setIndex(currentPlay);

        menuList.setAdapter(adapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Consts.ACTION_PLAY_TO);
                    intent.putExtra("currentPosition", position);
                    sendBroadcast(intent);
                    currentPlay = position;
                    adapter.setIndex(currentPlay);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int ps = seekBar.getProgress();
                int seekto = durationNum * ps / 100;
                position.setText(Util.formatTime(seekto));
                Intent intent = new Intent();
                intent.setAction(Consts.ACTION_SEEK_TO);
                intent.putExtra("seekto", seekto);
                sendBroadcast(intent);
            }
        });

    }

    private List<MyUpdataMediaPlayerEntity> getData() {

        booksDatabaseHelper = new BooksDatabaseHelper(this, Consts.DATABASE_VERSION);
        db = booksDatabaseHelper.getWritableDatabase();

        String sql = "select * from chapters where member_id=? and bookId=?";
        Cursor c = db.rawQuery(sql, new String[]{MenModel.member_id, bookId});

        List<MyUpdataMediaPlayerEntity> data = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String member_id = c.getString(c.getColumnIndex("member_id"));
            String booksId = c.getString(c.getColumnIndex("bookId"));
            String chaptreName = c.getString(c.getColumnIndex("chaptreName"));
            String chapterImg = c.getString(c.getColumnIndex("chapterImg"));
            String createTime = c.getString(c.getColumnIndex("createTime"));
            String chapterId = c.getString(c.getColumnIndex("chapterId"));
            String duration = c.getString(c.getColumnIndex("duration"));
            String zhangjiePath = c.getString(c.getColumnIndex("zhangjiePath"));
            data.add(new MyUpdataMediaPlayerEntity(member_id, booksId, chaptreName, chapterImg, createTime, chapterId, duration, zhangjiePath));
        }
        c.close();
        db.close();

        return data;
    }

    @OnClick({R.id.mediaplayer_close_list, R.id.myupdata_mediaplayer_menu, R.id.myupdata_mediaplayer_start_or_pause, R.id.myupdata_mediaplayer_next,
            R.id.myupdata_mediaplayer_pre, R.id.myupdata_mediaplayer_book_pic, R.id.myupdata_mediaplayer_rl})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.mediaplayer_close_list:
                rlMenu.setVisibility(View.GONE);
                rlMenu.startAnimation(translate);
                break;
            case R.id.myupdata_mediaplayer_menu:
                rlMenu.setVisibility(View.VISIBLE);
                rlMenu.startAnimation(translatet);
                break;
            case R.id.myupdata_mediaplayer_start_or_pause:
                if (audioIsPlay) {
                    audioIsPlay = false;
                    mRestartPaus.setImageResource(R.drawable.myupdata_mediaplayer_start);
//                    audioPlayer.pause();
                    intent.setAction(Consts.ACTION_PAUSE);
                    sendBroadcast(intent);
                } else {
                    audioIsPlay = true;
                    mRestartPaus.setImageResource(R.drawable.myupdata_mediaplayer_pause);
//                    audioPlayer.start();
//                    startUpdateProgressTimer();
                    intent.setAction(Consts.ACTION_PLAY);
                    sendBroadcast(intent);
                }
                break;
            case R.id.myupdata_mediaplayer_next:
                intent.setAction(Consts.ACTION_NEXT);
                sendBroadcast(intent);
                if (currentPlay < data.size() - 1) {
                    currentPlay = currentPlay + 1;
                } else {
                    currentPlay = 0;
                }
                adapter.setIndex(currentPlay);
                adapter.notifyDataSetChanged();
                break;
            case R.id.myupdata_mediaplayer_pre:
                intent.setAction(Consts.ACTION_PRE);
                sendBroadcast(intent);
                if (currentPlay > 0) {
                    currentPlay = currentPlay - 1;
                } else {
                    currentPlay = data.size() - 1;
                }
                adapter.setIndex(currentPlay);
                adapter.notifyDataSetChanged();
                break;
            case R.id.myupdata_mediaplayer_book_pic:
                if (rlMenu.getVisibility() == View.VISIBLE) {
                    rlMenu.setVisibility(View.GONE);
                    rlMenu.startAnimation(translate);
                }
                break;
            case R.id.myupdata_mediaplayer_rl:
                if (rlMenu.getVisibility() == View.VISIBLE) {
                    rlMenu.setVisibility(View.GONE);
                    rlMenu.startAnimation(translate);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
