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

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Administrator on 2017/11/27.
 */

public class TradeLogAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    public Context context;

    public TradeLogAdapter(Context context, ArrayList<HashMap<String, String>> list) {
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
        ViewHolder viewHolder;
        if(convertView!=null){
            viewHolder = (ViewHolder) convertView.getTag();
        }else{
            convertView = View.inflate(context,R.layout.layout_trade_log_item, null);
            viewHolder = new ViewHolder();

            viewHolder.tradeimg = (ImageView) convertView.findViewById(R.id.tradeimg);
            viewHolder.content = (TextView) convertView.findViewById(R.id.content);
            viewHolder.otime = (TextView) convertView.findViewById(R.id.otime);
            viewHolder.money = (TextView) convertView.findViewById(R.id.money);
            convertView.setTag(viewHolder);
        }


        viewHolder.content.setText(list.get(position).get("content"));
        viewHolder.otime.setText(list.get(position).get("otime"));
        String status=list.get(position).get("status");
        switch (status){
            case "1":
                viewHolder.tradeimg.setImageResource(R.drawable.chong);
                viewHolder.money.setVisibility(View.VISIBLE);
                viewHolder.money.setText("+"+list.get(position).get("money"));
                break;
            case "2":
                viewHolder.money.setVisibility(View.GONE);
                viewHolder.tradeimg.setImageResource(R.drawable.xiao);
                break;
            case "3":
                viewHolder.money.setVisibility(View.GONE);
                viewHolder.tradeimg.setImageResource(R.drawable.tui);
                break;
            case "4":
                viewHolder.money.setVisibility(View.VISIBLE);
                viewHolder.tradeimg.setImageResource(R.drawable.vip);
                viewHolder.money.setText("+"+list.get(position).get("money"));
                break;
            case "5":
                viewHolder.money.setVisibility(View.GONE);
                viewHolder.tradeimg.setImageResource(R.drawable.jiang);
                break;
            case "6":
                viewHolder.money.setVisibility(View.GONE);
                viewHolder.tradeimg.setImageResource(R.drawable.tui);
                break;
            case "7":
                viewHolder.money.setVisibility(View.GONE);
                viewHolder.tradeimg.setImageResource(R.drawable.xiao);
                break;
        }

        return convertView;
    }



    static class ViewHolder {
        TextView money;
        TextView otime;
        ImageView tradeimg;
        TextView content;


    }




}
