package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CircleTransform;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Administrator on 2017/11/27.
 */

public class BookChapterAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    public Context context;
    private String id="";

    public BookChapterAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    public BookChapterAdapter(Context context, ArrayList<HashMap<String, String>> list,String id) {
        this.context = context;
        this.list = list;
        this.id=id;
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

            viewHolder = new ViewHolder();
            convertView = View.inflate(context,R.layout.layout_book_chapter_item, null);
            convertView.setTag(viewHolder);
        }
        String viewid=list.get(position).get("id");


        viewHolder.createTime=convertView.findViewById(R.id.createTime);
        viewHolder.chaptreName=convertView.findViewById(R.id.chaptreName);
        viewHolder.imageView=convertView.findViewById(R.id.chapterImg);
        viewHolder.durationMsec=convertView.findViewById(R.id.durationMsec);

        viewHolder.createTime.setText(list.get(position).get("createTime"));
        viewHolder.chaptreName.setText(list.get(position).get("chaptreName"));
        viewHolder.durationMsec.setText(list.get(position).get("durationMsec"));
        String imgUrl = list.get(position).get("chapterImg");
        viewHolder.imageView.setTag(imgUrl);
        if (!TextUtils.isEmpty(imgUrl)) {
            imgUrl="http://"+imgUrl;
            Picasso.with(context).load(imgUrl).into(viewHolder.imageView);
        }

        if(list.get(position).get("id").equals(id)){
            LinearLayout playing=convertView.findViewById(R.id.playing);
            playing.setVisibility(View.VISIBLE);
            viewHolder.chaptreName.setTextColor(Color.rgb(00, 154, 205));
        }




        return convertView;
    }



    static class ViewHolder {
        TextView createTime;
        ImageView imageView;
        TextView chaptreName;
        TextView date;

        TextView durationMsec;

    }




}
