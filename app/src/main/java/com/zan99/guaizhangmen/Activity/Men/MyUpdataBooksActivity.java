package com.zan99.guaizhangmen.Activity.Men;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zan99.guaizhangmen.Adapter.GuaiBooksAdapter;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.SqlUtil.BooksDatabaseHelper;
import com.zan99.guaizhangmen.Util.Consts;
import com.zan99.guaizhangmen.Widget.ListViewLinearlayout;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyUpdataBooksActivity extends AppCompatActivity {

    @BindView(R.id.button2)
    LinearLayout button2;
    private BooksDatabaseHelper booksDatabaseHelper;
    private SQLiteDatabase db;

    @BindView(R.id.myupdata_book_list)
    ListViewLinearlayout booksListView;
    @BindView(R.id.back_left)
    Button back;

    private GuaiBooksAdapter booksAdapter;
    private ArrayList<HashMap<String, String>> booksList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_updata_books);

        ButterKnife.bind(this);

        init();

    }

    /**
     * 初始化书籍列表
     */
    private void init() {
        booksList = loadBooksView();
        booksAdapter = new GuaiBooksAdapter(this, booksList);
        booksListView.setAdapter(booksAdapter);

        // 书籍点击事件
        booksListView.setOnItemClickListener(new ListViewLinearlayout.OnItemClickListener() {
            @Override
            public void onItemClicked(View v, Object obj, int position) {

                Intent intent = new Intent(MyUpdataBooksActivity.this, MyUpdataActivity.class);
                String bookId = booksList.get(position).get("books_id");
                String bookName = booksList.get(position).get("books_name");
                String bookImg = booksList.get(position).get("books_img");
                String createTime = booksList.get(position).get("create_time");
                String authorName = booksList.get(position).get("author_name");
                intent.putExtra("books_id", bookId);
                intent.putExtra("books_name", bookName);
                intent.putExtra("books_img", bookImg);
                intent.putExtra("create_time", createTime);
                intent.putExtra("author_name", authorName);
                startActivity(intent);

            }
        });
    }

    @OnClick({R.id.button2, R.id.back_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_left:
                MyUpdataBooksActivity.this.finish();
                break;
            case R.id.button2:
                MyUpdataBooksActivity.this.finish();
                break;
        }
    }

    /**
     * 从数据库中取出该member_id下的所有书籍
     *
     * @return
     */
    private ArrayList<HashMap<String, String>> loadBooksView() {
        booksDatabaseHelper = new BooksDatabaseHelper(this, Consts.DATABASE_VERSION);
        db = booksDatabaseHelper.getWritableDatabase();

        String sql = "select * from books where member_id=?";
        Cursor c = db.rawQuery(sql, new String[]{MenModel.member_id});

        if (c.getCount() > 0) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("books_id", c.getString(c.getColumnIndex("bookId")));
                map.put("books_name", c.getString(c.getColumnIndex("booksName")));
                map.put("books_img", c.getString(c.getColumnIndex("booksImg")));
                map.put("books_synopsis", c.getString(c.getColumnIndex("booksSynopsis")));
                map.put("create_time", c.getString(c.getColumnIndex("bookDate")));
                map.put("author_name", c.getString(c.getColumnIndex("authorName")));
                booksList.add(map);
            }
            c.close();
            db.close();
        }

        return booksList;
    }

}
