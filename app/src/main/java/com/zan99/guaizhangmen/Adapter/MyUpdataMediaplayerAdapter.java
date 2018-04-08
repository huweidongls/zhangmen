package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zan99.guaizhangmen.Model.MyUpdataMediaPlayerEntity;
import com.zan99.guaizhangmen.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99zan on 2018/1/19.
 */

public class MyUpdataMediaplayerAdapter extends BaseAdapter {

    private Context context;
    private List<MyUpdataMediaPlayerEntity> data = new ArrayList<>();
    private int currentIndex;

    public MyUpdataMediaplayerAdapter(Context context, List<MyUpdataMediaPlayerEntity> data) {
        this.context = context;
        this.data = data;
    }

    public void setIndex (int i){
        currentIndex = i;
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
            convertView = View.inflate(context, R.layout.layout_my_updata_mediaplayer_menu_list_item, null);
            holder.imageView = convertView.findViewById(R.id.myupdata_mediaplayer_menu_play);
            holder.textView = convertView.findViewById(R.id.myupdata_mediaplayer_menu_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(position == currentIndex){
            holder.imageView.setVisibility(View.VISIBLE);
        }else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.textView.setText(data.get(position).getChaptreName());

        return convertView;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

}
