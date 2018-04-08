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
import java.util.List;

/**
 * Created by Administrator on 2017/11/27.
 */

public class CategoryViewPagerListAdapther extends BaseAdapter {
    private List<HashMap<String,String>> mDataList;
    private Context mContext;
    private boolean end = false;

    public CategoryViewPagerListAdapther(Context context, ArrayList<HashMap<String, String>> arrayList, boolean isEnd) {
        mDataList = arrayList;
        this.mContext = context;
        end = isEnd;
    }

    @Override
    public int getCount() {
        return mDataList.size();
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(convertView != null){
            viewHolder = (ViewHolder) convertView.getTag();
        }else{
            convertView = View.inflate(mContext,R.layout.layout_category_view_pager_books_item,null);
            viewHolder = new ViewHolder();
            viewHolder.books_name = (TextView) convertView.findViewById(R.id.books_name);
            viewHolder.books_img = (ImageView) convertView.findViewById(R.id.books_img);
            viewHolder.auther_name = (TextView) convertView.findViewById(R.id.auther_name);
            viewHolder.books_time = (TextView) convertView.findViewById(R.id.books_time);
            viewHolder.books_synopsis = (TextView) convertView.findViewById(R.id.books_synopsis);
            convertView.setTag(viewHolder);
        }

        viewHolder.books_name.setText(mDataList.get(i).get("books_name"));
        viewHolder.books_synopsis.setText(mDataList.get(i).get("books_synopsis"));
        Picasso.with(mContext).load(mDataList.get(i).get("books_img")).into(viewHolder.books_img);
        if(end){
            viewHolder.auther_name.setText(mDataList.get(i).get("auther_name") + "参与项目");
        }else{
            viewHolder.auther_name.setText(mDataList.get(i).get("auther_name") + "  著");
        }
        viewHolder.books_time.setText(mDataList.get(i).get("books_time"));
        convertView.setTag(R.id.books_id,mDataList.get(i).get("books_id"));
        return convertView;
    }

    static class ViewHolder{
        TextView books_name;
        ImageView books_img;
        TextView books_synopsis;
        TextView auther_name;
        TextView books_time;
    }
}
