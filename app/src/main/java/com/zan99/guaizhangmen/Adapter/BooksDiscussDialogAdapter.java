package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CircleTransform;
import com.zan99.guaizhangmen.Util.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/11/27.
 */

public class BooksDiscussDialogAdapter extends BaseAdapter {

        public ArrayList<HashMap<String, String>> list;
        public Context context;
        public LayoutInflater layoutInflater;

        public BooksDiscussDialogAdapter(Context context, ArrayList<HashMap<String, String>> list) {
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
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHolder viewHolder;

            if(convertView != null){
                viewHolder = (viewHolder) convertView.getTag();
            }else{
                convertView = View.inflate(context, R.layout.layout_book_discuss_dialog_item, null);
                viewHolder = new viewHolder();
                viewHolder.content = (TextView) convertView.findViewById(R.id.content);
                viewHolder.timer = (TextView) convertView.findViewById(R.id.timer);
                viewHolder.headimg = (ImageView) convertView.findViewById(R.id.headimg);
                viewHolder.nick_name = (TextView) convertView.findViewById(R.id.nick_name);
                viewHolder.date = (TextView) convertView.findViewById(R.id.date);
                convertView.setTag(viewHolder);
            }

            viewHolder.nick_name.setText(list.get(position).get("nick_name"));
            viewHolder.timer.setText(list.get(position).get("timer"));
            viewHolder.date.setText(list.get(position).get("date"));
            Picasso.with(context).load(list.get(position).get("head_img")).placeholder(R.drawable.iconheader_03).transform(new CircleTransform()).into(viewHolder.headimg);
            SpannableString spannableString = new SpannableString("  @"+ list.get(position).get("to_name") + "  " + list.get(position).get("content"));

            String to_name="";
            if(list.get(position).get("to_name")!=null){
                to_name=list.get(position).get("to_name");
            }
            int startSpan = to_name.length()+3;

            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#4cb8c4")), 0, startSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.content.setText(spannableString);
            convertView.setTag(R.id.nick_name,list.get(position).get("nick_name"));
            convertView.setTag(R.id.comment_id,list.get(position).get("comment_id"));
            convertView.setTag(R.id.to_user_id,list.get(position).get("to_user_id"));
            return convertView;
        }

        static class viewHolder{
            ImageView headimg;
            TextView nick_name;
            TextView content;
            TextView timer;
            TextView date;
        }
    }
