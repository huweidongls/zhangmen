package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.text.TextUtils;
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


public class CollectAdapte extends BaseAdapter {
	public ArrayList<HashMap<String, String>> list;
	public Context context;
	public LayoutInflater layoutInflater;

	public CollectAdapte(Context context, ArrayList<HashMap<String, String>> list) {
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


        TextView textView;
		TextView bookssynopsis;
		TextView authorname;
		TextView create_time;
		ViewHolder viewHolder = null;


		if(convertView!=null){
			viewHolder = (ViewHolder) convertView.getTag();
		}else{
			viewHolder = new ViewHolder();
			convertView = View.inflate(context, R.layout.layout_collect_item, null);
			convertView.setTag(viewHolder);
		}

		viewHolder.imageView = (ImageView) convertView.findViewById(R.id.booksimg);

		//图片
		String imgUrl = list.get(position).get("books_img");
		viewHolder.imageView.setTag(imgUrl);
        if (!TextUtils.isEmpty(imgUrl)) {
			imgUrl="http://"+imgUrl;
			Picasso.with(context).load(imgUrl).into(viewHolder.imageView);
        }


        //书名
		textView= (TextView) convertView.findViewById(R.id.booksname);
		textView.setText(list.get(position).get("books_name"));

        //文字简介
		bookssynopsis= (TextView) convertView.findViewById(R.id.bookssynopsis);
		bookssynopsis.setText(list.get(position).get("books_synopsis"));

        //时间
		create_time= (TextView) convertView.findViewById(R.id.create_time);
		create_time.setText(list.get(position).get("ctime"));
		return convertView;

	}


	static class ViewHolder {
		TextView txt;
		public ImageView imageView;
	}


}


