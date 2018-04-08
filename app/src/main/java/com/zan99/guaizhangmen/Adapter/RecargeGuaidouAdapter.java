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

public class RecargeGuaidouAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    public Context context;
    public LayoutInflater layoutInflater;

    public RecargeGuaidouAdapter(Context context, ArrayList<HashMap<String, String>> list) {
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
            convertView = View.inflate(context,R.layout.layout_recharge_guaidou_item, null);
            viewHolder = new ViewHolder();
            viewHolder.bean_num = (TextView) convertView.findViewById(R.id.bean_num);
            viewHolder.bean_money = (TextView) convertView.findViewById(R.id.bean_money);
            viewHolder.deliverNum = (TextView) convertView.findViewById(R.id.deliverNum);
            convertView.setTag(viewHolder);
        }
        int deliverNum= Integer.parseInt(list.get(position).get("deliverNum"));
        if(deliverNum>0){
            viewHolder.deliverNum.setText("赠 "+list.get(position).get("deliverNum"));
        }else{
            viewHolder.deliverNum.setText("");
        }
        viewHolder.bean_num.setText(list.get(position).get("bean_num"));
        viewHolder.bean_money.setText("¥ "+list.get(position).get("bean_money"));
        return convertView;
    }



    static class ViewHolder {
        TextView deliverNum;
        TextView bean_num;
        TextView bean_money;

    }




}
