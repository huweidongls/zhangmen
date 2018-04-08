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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/11/28.
 */

public class ZhangFangtanAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    public Context context;

    public ZhangFangtanAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        FangtanViewHolder holder = null;
        if(convertView != null){
            holder = (FangtanViewHolder) convertView.getTag();
        }else{
            convertView = LayoutInflater.from(context).inflate( R.layout.layout_zhang_fangtan_item , null);
            holder = new FangtanViewHolder();

            holder.tvtitle = (TextView) convertView.findViewById(R.id.tvtitle);
            holder.ctime = (TextView) convertView.findViewById(R.id.ctime);
            holder.profile = (TextView) convertView.findViewById(R.id.profile);
            holder.zan_num = (TextView) convertView.findViewById(R.id.zan_num);
            holder.ping_num = (TextView) convertView.findViewById(R.id.ping_num);
            holder.ivimage = (ImageView) convertView.findViewById(R.id.ivimage);

            convertView.setTag(holder);
        }
        Picasso.with(context).load(list.get(position).get("cover")).into(holder.ivimage);
        holder.tvtitle.setText(list.get(position).get("name"));
        holder.ctime.setText(list.get(position).get("time"));
        holder.profile.setText( "\u3000\u3000"+list.get(position).get("profile"));
        holder.zan_num.setText(list.get(position).get("givelike"));
        holder.ping_num.setText(list.get(position).get("discuss"));
        convertView.setTag(R.id.fangtan_id,list.get(position).get("id"));
        return convertView;
    }

    static class FangtanViewHolder{

        TextView tvtitle;
        TextView ctime;
        TextView profile;
        TextView zan_num;
        TextView ping_num;
        ImageView ivimage;
    }

}
