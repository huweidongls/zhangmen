package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CircleTransform;
import com.zan99.guaizhangmen.Widget.ListViewLinearLayoutAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Administrator on 2017/12/21.
 */

public class LinearLayoutListViewBookDiscussAdapter extends ListViewLinearLayoutAdapter {

    public ArrayList<HashMap<String, String>> list;
    public Context context;

    public LinearLayoutListViewBookDiscussAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        super(context, list);
        this.context = context;
        this.list = list;
    }


    @Override
    public View getView(int position) {
        View convertView = View.inflate(context,R.layout.layout_book_discuss_item, null);
        System.out.println(list.get(position).get("content"));
        EmojiconTextView content = (EmojiconTextView) convertView.findViewById(R.id.content);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView timer = (TextView) convertView.findViewById(R.id.timer);
        ImageView headimg = (ImageView) convertView.findViewById(R.id.headimg);
        TextView nick_name = (TextView) convertView.findViewById(R.id.nick_name);
        TextView number = (TextView) convertView.findViewById(R.id.number);
        TextView durationMsec = convertView.findViewById(R.id.durationMsec);

        nick_name.setText(list.get(position).get("nick_name"));
        content.setText(list.get(position).get("content"));
        date.setText(list.get(position).get("otime_date"));
        timer.setText(list.get(position).get("otime_time"));
        if(Integer.parseInt(list.get(position).get("number").toString()) != 0){
            number.setText( "查看"+list.get(position).get("number").toString()+"条回复>");
        }else{
            number.setVisibility(View.GONE);
        }
        Picasso.with(context).load(list.get(position).get("head_img")).transform(new CircleTransform()).into(headimg);
        convertView.setTag(R.id.comment_id,list.get(position).get("comment_id"));
        convertView.setTag(R.id.to_user_id, list.get(position).get("to_user_id"));
        convertView.setTag(R.id.nick_name, list.get(position).get("nick_name"));
        return convertView;
    }
}
