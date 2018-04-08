package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Widget.ListViewLinearLayoutAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/12/21.
 */

public class GuaiBooksAdapter extends ListViewLinearLayoutAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> list;
    public GuaiBooksAdapter (Context context , ArrayList<HashMap<String, String>> list){
        super(context, list);
        this.context = context;
        this.list = list;
    }
    @Override
    public View getView(int position) {
        View convertView = View.inflate(context, R.layout.layout_guai_new_books_item, null);
        TextView booksname = (TextView)  convertView.findViewById(R.id.booksname);
        TextView bookssynopsis = (TextView)  convertView.findViewById(R.id.bookssynopsis);
        TextView authorname = (TextView)  convertView.findViewById(R.id.authorname);
        TextView create_time = (TextView)  convertView.findViewById(R.id.create_time);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.booksimg);

        // 图片
        Picasso.with(context).load("http://"+list.get(position).get("books_img")).resize(320, 440).into(imageView);

        // 书名
        booksname.setText(list.get(position).get("books_name"));

        //文字简介
        bookssynopsis.setText(list.get(position).get("books_synopsis"));

        //作者
        authorname.setText(list.get(position).get("author_name")+"  著");

        //时间
        create_time.setText(list.get(position).get("create_time"));

        convertView.setTag(R.id.books_id, list.get(position).get("books_id"));
        return convertView;
    }
}
