package com.zan99.guaizhangmen.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.Model.MyUpDataZhangjieEntity;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.SqlUtil.BooksDatabaseHelper;
import com.zan99.guaizhangmen.Util.DownloadUtil;
import com.zan99.guaizhangmen.Util.HttpUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 99zan on 2018/1/17.
 */

public class DownloadService extends Service {

    String book_id;
    String chapter_id;
    String duration;
    JSONObject bookResult;
    JSONObject zhangjieResult;
    String audioSrc;
    String audioName;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    qingqiuBook();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 在此方法中，可以执行相关逻辑，如耗时操作
     *
     * @param intent  :由Activity传递给service的信息，存在intent中
     * @param flags   ：规定的额外信息
     * @param startId ：开启服务时，如果有规定id，则传入startid
     * @return 返回值规定此startservice是哪种类型，粘性的还是非粘性的
     * START_STICKY:粘性的，遇到异常停止后重新启动，并且intent=null
     * START_NOT_STICKY:非粘性，遇到异常停止不会重启
     * START_REDELIVER_INTENT:粘性的，重新启动，并且将Context传递的信息intent传递
     * 此方法是唯一的可以执行很多次的方法
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        book_id = intent.getStringExtra("book_id");
        chapter_id = intent.getStringExtra("chapter_id");
        duration = intent.getStringExtra("duration");
        audioSrc = intent.getStringExtra("audioSrc");
        audioName = intent.getStringExtra("audioName");

        download();

        return START_NOT_STICKY;
    }

    private void download() {

        DownloadUtil.get().download("http://"+audioSrc, "/com.99zan/download/yp", audioName+".mp3", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
                Log.e("111", "下载成功！onDownloadSuccess");
            }

            @Override
            public void onDownloading(int progress) {
                Log.e("111", "下载中！"+progress);
            }

            @Override
            public void onDownloadFailed() {
                Log.e("111", "下载失败！onDownloadFailed");
            }
        });

    }

    private void qingqiuBook() {

        String url = "author.php/Nexts/listarray";
        RequestParams param = new RequestParams();
        param.put("member_id", MenModel.member_id);
        param.put("books_id", book_id);
        HttpUtil.post(url, param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject json  = null;
                try {
                    Log.e("111", "listarray");
                    json = JSON.parseObject(new String(responseBody, "utf-8"));
                    bookResult = json.getJSONObject("dataList");
                    qingqiuZhangjie();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    private void qingqiuZhangjie() {

        String url = "author.php/Nexts/commentContent";
        RequestParams params = new RequestParams();
        params.add("member_id", MenModel.member_id);
        params.add("chapter_id",chapter_id);
        params.add("books_id",book_id);
        HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.e("111", "commentContent");
                    JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf8"));
                    zhangjieResult = jsonObject.getJSONObject("dataList").getJSONObject("chapterId");

                    putIntoSqlite(bookResult, zhangjieResult);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    private void putIntoSqlite(JSONObject jsonObject, JSONObject zhangjie) {

        JSONObject book = jsonObject.getJSONObject("books");

        BooksDatabaseHelper booksDatabaseHelper = new BooksDatabaseHelper(getApplicationContext(), 1);
        SQLiteDatabase db = booksDatabaseHelper.getWritableDatabase();

        boolean isBooks = false;
        String sql = "select * from books where member_id=?";
        Cursor c = db.rawQuery(sql, new String[]{MenModel.member_id});
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if(c.getString(c.getColumnIndex("bookId")).equals(book_id)){
                isBooks = true;
            }
        }
        c.close();

        if(!isBooks){
            ContentValues cv = new ContentValues();
            cv.put("member_id", MenModel.member_id);
            cv.put("bookId", book_id);
            cv.put("booksName", book.getString("booksName"));
            cv.put("booksImg", book.getString("booksImg"));
            cv.put("booksSynopsis", book.getString("booksSynopsis"));
            cv.put("bookDate", book.getString("bookDate"));
            cv.put("authorName", jsonObject.getJSONObject("author").getString("name"));
            db.insert("books", null, cv);//执行插入操作
        }

        ContentValues cv1 = new ContentValues();
        cv1.put("member_id", MenModel.member_id);
        cv1.put("bookId", book_id);
        cv1.put("chaptreName", zhangjie.getString("chaptreName"));
        cv1.put("chapterImg", zhangjie.getString("chapterImg"));
        cv1.put("createTime", zhangjie.getString("createTime"));
        cv1.put("duration", duration);
        cv1.put("chapterId", zhangjie.getString("chapterId"));
        cv1.put("zhangjiePath", Environment.getExternalStorageDirectory()+"/com.99zan/download/yp/"+audioName+".mp3");
        db.insert("chapters", null, cv1);

        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this);
        builder1.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("通知")
                .setContentText("下载完成！");
        Notification notification = builder1.build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(3, notification);

        db.close();

        stopSelf();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
