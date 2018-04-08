package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zan99.guaizhangmen.Model.MyUpDataZhangjieEntity;
import com.zan99.guaizhangmen.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99zan on 2018/1/15.
 */

public class MyUpDataZhangjieAdapter extends BaseAdapter {

    private Context context;
    private List<MyUpDataZhangjieEntity> data = new ArrayList<>();

    public MyUpDataZhangjieAdapter(Context context, List<MyUpDataZhangjieEntity> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.layout_myupdata_zhangjie_item, null);
            holder.bookname = convertView.findViewById(R.id.myupdata_book_zhangjie_item_name);
            holder.booktime = convertView.findViewById(R.id.myupdata_book_zhangjie_item_time);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bookname.setText(data.get(position).getChapterName());
        holder.booktime.setText(data.get(position).getDuration());

        return convertView;
    }

    private class ViewHolder {
        TextView bookname;
        TextView booktime;
    }

}
