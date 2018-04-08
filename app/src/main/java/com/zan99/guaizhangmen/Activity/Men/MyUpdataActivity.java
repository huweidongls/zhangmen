package com.zan99.guaizhangmen.Activity.Men;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Adapter.MyUpDataZhangjieAdapter;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.Model.MyUpDataZhangjieEntity;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.SqlUtil.BooksDatabaseHelper;
import com.zan99.guaizhangmen.Util.Consts;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyUpdataActivity extends BaseActivity {

    @BindView(R.id.button2)
    LinearLayout button2;
    @BindView(R.id.myupdata_book_play)
    ImageView myupdataBookPlay;
    private BooksDatabaseHelper booksDatabaseHelper;
    private SQLiteDatabase db;
    /**
     * 书籍名称
     */
    private String booksName;
    /**
     * 书籍图片URL
     */
    private String booksImg;
    /**
     * 书籍作者
     */
    private String authorname;
    /**
     * 书籍创建时间
     */
    private String creattime;
    /**
     * 书籍id
     */
    private String bookId;

    @BindView(R.id.myupdata_book_date)
    TextView createTime;
    @BindView(R.id.myupdata_book_yanjiang)
    TextView authorName;
    @BindView(R.id.myupdata_book)
    ImageView bookImg;
    @BindView(R.id.myupdata_book_name)
    TextView bookName;
    @BindView(R.id.back_left)
    Button back;
    /**
     * 章节列表
     */
    @BindView(R.id.myupdata_book_zhangjie)
    ListView listView;
    /**
     * 章节数据
     */
    private List<MyUpDataZhangjieEntity> data = new ArrayList<>();
    /**
     * 章节列表适配器
     */
    private MyUpDataZhangjieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_updata);

        ButterKnife.bind(this);

        init();

    }

    private void init() {

        Intent intent = getIntent();
        bookId = intent.getStringExtra("books_id");
        booksName = intent.getStringExtra("books_name");
        booksImg = intent.getStringExtra("books_img");
        authorname = intent.getStringExtra("author_name");
        creattime = intent.getStringExtra("create_time");

        Picasso.with(this).load("http://" + booksImg).into(bookImg);
        bookName.setText(booksName);
        authorName.setText(authorname + " 著");
        createTime.setText(creattime);

        data = getData();
        adapter = new MyUpDataZhangjieAdapter(MyUpdataActivity.this, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("currentPlay", position);
                intent.putExtra("bookId", bookId);
                intent.putExtra("booksName", booksName);
                intent.putExtra("booksImg", booksImg);
                intent.putExtra("authorname", authorname);
                intent.setClass(MyUpdataActivity.this, MyUpDataMediaPlayerActivity.class);
                startActivity(intent);
            }
        });

    }

    private List<MyUpDataZhangjieEntity> getData() {

        booksDatabaseHelper = new BooksDatabaseHelper(this, Consts.DATABASE_VERSION);
        db = booksDatabaseHelper.getWritableDatabase();

        String sql = "select * from chapters where member_id=? and bookId=?";
        Cursor c = db.rawQuery(sql, new String[]{MenModel.member_id, bookId});

        List<MyUpDataZhangjieEntity> data = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            data.add(new MyUpDataZhangjieEntity(c.getString(c.getColumnIndex("chaptreName")), c.getString(c.getColumnIndex("duration"))));
        }
        c.close();
        db.close();

        return data;
    }

    @OnClick({R.id.button2, R.id.back_left, R.id.myupdata_book_play})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.myupdata_book_play:
                intent.setClass(MyUpdataActivity.this, MyUpDataMediaPlayerActivity.class);
                intent.putExtra("currentPlay", 0);
                intent.putExtra("bookId", bookId);
                intent.putExtra("booksName", booksName);
                intent.putExtra("booksImg", booksImg);
                intent.putExtra("authorname", authorname);
                startActivity(intent);
                break;
            case R.id.back_left:
                MyUpdataActivity.this.finish();
                break;
            case R.id.button2:
                MyUpdataActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
