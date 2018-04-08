package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CircleTransform;
import com.zan99.guaizhangmen.Util.L;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Administrator on 2017/11/27.
 */

public class BookDiscussAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    public Context context;
    public LayoutInflater layoutInflater;

    public BookDiscussAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
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
        ViewHolder viewHolder;
        if(convertView!=null){
            viewHolder = (ViewHolder) convertView.getTag();
        }else{
            convertView = View.inflate(context,R.layout.layout_book_discuss_item, null);
            viewHolder = new ViewHolder();
            viewHolder.content = (EmojiconTextView) convertView.findViewById(R.id.content);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.timer = (TextView) convertView.findViewById(R.id.timer);
            viewHolder.headimg = (ImageView) convertView.findViewById(R.id.headimg);
            viewHolder.nick_name = (TextView) convertView.findViewById(R.id.nick_name);
            viewHolder.number = (TextView) convertView.findViewById(R.id.number);
            convertView.setTag(viewHolder);
        }

        viewHolder.nick_name.setText(list.get(position).get("nick_name"));
        viewHolder.content.setText(list.get(position).get("content"));
        viewHolder.date.setText(list.get(position).get("otime_date"));
        viewHolder.timer.setText(list.get(position).get("otime_time"));

        if(Integer.parseInt(list.get(position).get("number").toString()) != 0){
            viewHolder.number.setText( "查看"+list.get(position).get("number").toString()+"条回复>");
        }else{
            viewHolder.number.setVisibility(View.GONE);
        }

        Picasso.with(context).load(list.get(position).get("head_img")).placeholder(R.drawable.iconheader_03).transform(new CircleTransform()).into(viewHolder.headimg);
        convertView.setTag(R.id.comment_id,list.get(position).get("comment_id"));
        convertView.setTag(R.id.to_user_id, list.get(position).get("to_user_id"));
        convertView.setTag(R.id.nick_name, list.get(position).get("nick_name"));
        return convertView;
    }



    static class ViewHolder {
        TextView nick_name;
        ImageView headimg;
        TextView timer;
        TextView date;
        EmojiconTextView content;
        TextView number;

    }




}
