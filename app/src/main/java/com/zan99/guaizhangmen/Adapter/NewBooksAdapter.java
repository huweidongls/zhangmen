package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/11/27.
 */

public class NewBooksAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    public Context context;

    public NewBooksAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView != null){
            viewHolder = (ViewHolder) convertView.getTag();
        }else{
            convertView = View.inflate(context, R.layout.layout_guai_new_books_item, null);
            viewHolder = new ViewHolder();
            viewHolder.booksname = (TextView)  convertView.findViewById(R.id.booksname);
            viewHolder.bookssynopsis = (TextView)  convertView.findViewById(R.id.bookssynopsis);
            viewHolder.authorname = (TextView)  convertView.findViewById(R.id.authorname);
            viewHolder.create_time = (TextView)  convertView.findViewById(R.id.create_time);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.booksimg);
            convertView.setTag(viewHolder);
        }

        // 图片
        Picasso.with(context).load("http://"+list.get(position).get("books_img")).into(viewHolder.imageView);

        // 书名
        viewHolder.booksname.setText(list.get(position).get("books_name"));

        //文字简介
        viewHolder.bookssynopsis.setText(list.get(position).get("books_synopsis"));

        //作者
        viewHolder.authorname.setText(list.get(position).get("author_name")+"  著");

        //时间
        viewHolder.create_time.setText(list.get(position).get("create_time"));

        convertView.setTag(R.id.books_id, list.get(position).get("books_id"));
        return convertView;

    }



    static class ViewHolder {
        TextView booksname;
        TextView bookssynopsis;
        TextView authorname;
        TextView create_time;
        ImageView imageView;
    }




}
