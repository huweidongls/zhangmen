package com.zan99.guaizhangmen.Activity.Men;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.SuopingMediaPlayerActivity;
import com.zan99.guaizhangmen.Activity.TemplateActivity;
import com.zan99.guaizhangmen.Adapter.MyUpdataMediaplayerAdapter;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.Model.MyUpDataZhangjieEntity;
import com.zan99.guaizhangmen.Model.MyUpdataMediaPlayerEntity;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.SqlUtil.BooksDatabaseHelper;
import com.zan99.guaizhangmen.Util.Consts;
import com.zan99.videoview.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyUpDataMediaPlayerActivity extends AppCompatActivity {

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

    private MyUpdataMediaplayerAdapter adapter;

    MediaPlayer audioPlayer;
    private boolean audioIsPlay = false;
    private int durationNum;
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;
    private Timer mUpdataSuopingProgressTimer;
    private TimerTask mUpdataSuopingProgressTimerTask;
    private boolean audioPlayerIsRelease = false;

    /**
     * 接收锁屏广播
     */
    MediaPlayerBroadcastReceiver receiver;

    private List<MyUpdataMediaPlayerEntity> data = new ArrayList<>();
    /**
     * 书籍id
     */
    private String bookId;
    /**
     * 当前播放音频
     */
    private int currentPlay;
    private String booksName;
    private String authorsName;
    private String booksImg;

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    updateProgress();
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private Animation translate, translatet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_up_data_media_player);

        ButterKnife.bind(this);

        init();

    }

    private class MediaPlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF:
                    Log.e("111", "收到锁屏广播");
                    Intent lockscreen = new Intent(MyUpDataMediaPlayerActivity.this, SuopingMediaPlayerActivity.class);
                    lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    lockscreen.putExtra("bookid", bookId);
                    lockscreen.putExtra("currentPlay", currentPlay);
                    lockscreen.putExtra("booksName", booksName);
                    lockscreen.putExtra("authorsName", authorsName);
                    lockscreen.putExtra("booksImg", booksImg);
                    startActivity(lockscreen);
                    startUpdateSuopingProgressTimer();
                    break;
                case Consts.ACTION_PAUSE:
                    audioIsPlay = false;
                    mRestartPaus.setImageResource(R.drawable.myupdata_mediaplayer_start);
                    audioPlayer.pause();
                    break;
                case Consts.ACTION_PLAY:
                    audioIsPlay = true;
                    mRestartPaus.setImageResource(R.drawable.myupdata_mediaplayer_pause);
                    audioPlayer.start();
                    startUpdateProgressTimer();
                    break;
                case Consts.ACTION_NEXT:
                    next();
                    break;
                case Consts.ACTION_PRE:
                    pre();
                    break;
                case Consts.ACTION_PLAY_TO:
                    int position1 = intent.getIntExtra("currentPosition", 0);
                    playTo(position1);
                    break;
                case Consts.ACTION_SEEK_TO:
                    int seekto = intent.getIntExtra("seekto", 0);
                    audioPlayer.seekTo(seekto);
                    position.setText(Util.formatTime(seekto));
                    break;
            }
        }
    }

    private void init() {

        receiver = new MediaPlayerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Consts.ACTION_PLAY);
        intentFilter.addAction(Consts.ACTION_PAUSE);
        intentFilter.addAction(Consts.ACTION_NEXT);
        intentFilter.addAction(Consts.ACTION_PRE);
        intentFilter.addAction(Consts.ACTION_PLAY_TO);
        intentFilter.addAction(Consts.ACTION_SEEK_TO);
        registerReceiver(receiver, intentFilter);

        bookId = getIntent().getStringExtra("bookId");
        currentPlay = getIntent().getIntExtra("currentPlay", -1);

        booksName = getIntent().getStringExtra("booksName");
        authorsName = getIntent().getStringExtra("authorname");
        booksImg = getIntent().getStringExtra("booksImg");

        bookName.setText(booksName);
        authorName.setText(authorsName);
        Picasso.with(this).load("http://" + booksImg).into(bookImg);

        translate = AnimationUtils.loadAnimation(this, R.anim.translate);
        translatet = AnimationUtils.loadAnimation(this, R.anim.translatet);

        data = getData();
        adapter = new MyUpdataMediaplayerAdapter(this, data);
        adapter.setIndex(currentPlay);

        menuList.setAdapter(adapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    playTo(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        getMp3View(data.get(currentPlay).getZhangjiePath());

    }

    private void playTo(int position) {
        try {
            currentPlay = position;
            audioPlayer.stop();
            audioPlayer.reset();
            audioPlayer.setDataSource(MyUpDataMediaPlayerActivity.this, Uri.parse(data.get(currentPlay).getZhangjiePath()));
            audioPlayer.prepare();
            audioPlayer.start();
            adapter.setIndex(currentPlay);
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void next() {
        try {
            if (currentPlay < data.size() - 1) {
                currentPlay = currentPlay + 1;
            } else {
                currentPlay = 0;
            }
            audioPlayer.reset();
            audioPlayer.setDataSource(this, Uri.parse(data.get(currentPlay).getZhangjiePath()));
            audioPlayer.prepare();
            audioPlayer.start();
            adapter.setIndex(currentPlay);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pre() {
        try {
            if (currentPlay > 0) {
                currentPlay = currentPlay - 1;
            } else {
                currentPlay = data.size() - 1;
            }
            audioPlayer.reset();
            audioPlayer.setDataSource(this, Uri.parse(data.get(currentPlay).getZhangjiePath()));
            audioPlayer.prepare();
            audioPlayer.start();
            adapter.setIndex(currentPlay);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void getMp3View(String src) {
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

        audioPlayer = MediaPlayer.create(this, Uri.parse(src));
        audioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                MyUpDataMediaPlayerActivity.AudioPlaySta.mCurrentState = MyUpDataMediaPlayerActivity.AudioPlaySta.STATE_PREPARED;
            }
        });
        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                MyUpDataMediaPlayerActivity.AudioPlaySta.mCurrentState = MyUpDataMediaPlayerActivity.AudioPlaySta.STATE_COMPLETED;
//                cancelUpdateProgressTimer();
//                resetAudioPlayer();
                next();
                Intent intent = new Intent();
                intent.setAction(Consts.ACTION_MEDIA_PLAY_COMPLETE);
                intent.putExtra("complete", currentPlay);
                sendBroadcast(intent);
            }
        });
        audioPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    // 播放器开始渲染
                    MyUpDataMediaPlayerActivity.AudioPlaySta.mCurrentState = MyUpDataMediaPlayerActivity.AudioPlaySta.STATE_PLAYING;
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    // MediaPlayer暂时不播放，以缓冲更多的数据
                    if (MyUpDataMediaPlayerActivity.AudioPlaySta.mCurrentState == MyUpDataMediaPlayerActivity.AudioPlaySta.STATE_PAUSED || MyUpDataMediaPlayerActivity.AudioPlaySta.mCurrentState == MyUpDataMediaPlayerActivity.AudioPlaySta.STATE_BUFFERING_PAUSED) {
                        MyUpDataMediaPlayerActivity.AudioPlaySta.mCurrentState = MyUpDataMediaPlayerActivity.AudioPlaySta.STATE_BUFFERING_PAUSED;
                    } else {
                        MyUpDataMediaPlayerActivity.AudioPlaySta.mCurrentState = MyUpDataMediaPlayerActivity.AudioPlaySta.STATE_BUFFERING_PLAYING;
                    }
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    // 填充缓冲区后，MediaPlayer恢复播放/暂停
                    if (MyUpDataMediaPlayerActivity.AudioPlaySta.mCurrentState == MyUpDataMediaPlayerActivity.AudioPlaySta.STATE_BUFFERING_PLAYING) {
                        MyUpDataMediaPlayerActivity.AudioPlaySta.mCurrentState = MyUpDataMediaPlayerActivity.AudioPlaySta.STATE_PLAYING;
                    }
                }
                return true;
            }
        });
        audioPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                MyUpDataMediaPlayerActivity.AudioPlaySta.mBufferPercentage = i;
            }
        });
        durationNum = audioPlayer.getDuration();
        duration.setText(Util.formatTime(durationNum));
        audioIsPlay = true;
        mRestartPaus.setImageResource(R.drawable.myupdata_mediaplayer_pause);
        audioPlayer.start();
        startUpdateProgressTimer();
    }

    private void resetAudioPlayer() {
        mRestartPaus.setImageResource(R.drawable.mediaplayer_start);
        audioPlayer.stop();
        audioPlayer.reset();
        seek.setProgress(0);
        audioIsPlay = false;//后加
        position.setText(Util.formatTime(0));//后加
    }

    /**
     * 开始更新进度
     */
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
            int secondProgress = MyUpDataMediaPlayerActivity.AudioPlaySta.mBufferPercentage;
            seek.setProgress(progress);
            seek.setSecondaryProgress(secondProgress);
            position.setText(Util.formatTime(tposition));
            duration.setText(Util.formatTime(tduration));
        }
    }

    /**
     * 更新锁屏进度
     */
    protected void startUpdateSuopingProgressTimer() {
        cancelUpdateSuopingProgressTimer();
        if (mUpdataSuopingProgressTimer == null) {
            mUpdataSuopingProgressTimer = new Timer();
        }
        if (mUpdataSuopingProgressTimerTask == null) {
            mUpdataSuopingProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (!audioPlayerIsRelease) {
                        int tposition = audioPlayer.getCurrentPosition();
                        int tduration = audioPlayer.getDuration();
                        int progress = (int) (100f * tposition / tduration);
                        Intent intent = new Intent();
                        intent.setAction(Consts.ACTION_UPDATA_SUOPING);
                        intent.putExtra("position", tposition);
                        intent.putExtra("duration", tduration);
                        intent.putExtra("progress", progress);
                        sendBroadcast(intent);
                    }
                }
            };
        }
        mUpdataSuopingProgressTimer.schedule(mUpdataSuopingProgressTimerTask, 0, 1000);
    }

    /**
     * 取消更新锁屏进度
     */
    private void cancelUpdateSuopingProgressTimer() {
        if (mUpdataSuopingProgressTimer != null) {
            mUpdataSuopingProgressTimer.cancel();
            mUpdataSuopingProgressTimer = null;
        }
        if (mUpdataSuopingProgressTimerTask != null) {
            mUpdataSuopingProgressTimerTask.cancel();
            mUpdataSuopingProgressTimerTask = null;
        }
    }

    @OnClick({R.id.mediaplayer_close_list, R.id.myupdata_mediaplayer_menu, R.id.myupdata_mediaplayer_start_or_pause, R.id.myupdata_mediaplayer_next,
            R.id.myupdata_mediaplayer_pre, R.id.myupdata_mediaplayer_book_pic, R.id.myupdata_mediaplayer_rl})
    public void onClick(View view) {
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
                if (audioPlayer.isPlaying()) {
                    audioIsPlay = false;
                    mRestartPaus.setImageResource(R.drawable.myupdata_mediaplayer_start);
                    audioPlayer.pause();
                } else {
                    audioIsPlay = true;
                    mRestartPaus.setImageResource(R.drawable.myupdata_mediaplayer_pause);
                    audioPlayer.start();
                    startUpdateProgressTimer();
                }
                break;
            case R.id.myupdata_mediaplayer_next:
                next();
                break;
            case R.id.myupdata_mediaplayer_pre:
                pre();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        unregisterReceiver(receiver);
        cancelUpdateSuopingProgressTimer();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("111", "onrestart()");
//        cancelUpdateSuopingProgressTimer();
    }
}
