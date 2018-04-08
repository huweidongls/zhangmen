package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CircleTransform;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class CommentAdapte extends BaseAdapter {
	public ArrayList<HashMap<String, String>> list;
	public Context context;
	public LayoutInflater layoutInflater;

	public CommentAdapte(Context context, ArrayList<HashMap<String, String>> list) {
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

		ViewHolder viewHolder = null;
		LinearLayout particularsll;
		LinearLayout revertll;

		if(convertView!=null){
			viewHolder = (ViewHolder) convertView.getTag();
		}else{
			viewHolder = new ViewHolder();
			convertView = View.inflate(context, R.layout.layout_comment_item, null);
			convertView.setTag(viewHolder);
		}

		viewHolder.imageView = (ImageView) convertView.findViewById(R.id.head_img);
		particularsll=convertView.findViewById(R.id.particulars);
		revertll=convertView.findViewById(R.id.revert);
		//一级头像
		String imgUrl = list.get(position).get("head_img");
		viewHolder.imageView.setTag(imgUrl);
        if (!TextUtils.isEmpty(imgUrl)) {
			imgUrl="http://"+imgUrl;
			Picasso.with(context).load(imgUrl).transform(new CircleTransform()).into(viewHolder.imageView);
        }

        //我的姓名
		setdataText(convertView,list.get(position).get("nick_name"),R.id.nick_name_one);

        //我的评论
		setdataText(convertView,list.get(position).get("content"),R.id.content);

        //时间
		setdataText(convertView,list.get(position).get("otime"),R.id.timer_one);

        //日期
		setdataText(convertView,list.get(position).get("otimes"),R.id.dater_one);

		String types=list.get(position).get("types");
		//书籍信息
		if(types=="1") {
			//书籍信息设置
			String particulars = list.get(position).get("particulars");
			if (particulars == "null") {
				particularsll.setVisibility(View.GONE);
			} else {
				particularsll.setVisibility(View.VISIBLE);
				setparticulars(convertView, particulars);
			}

			//上级回复
			String revert = list.get(position).get("revert");
			if (revert == "null") {
				revertll.setVisibility(View.GONE);
			} else {
				revertll.setVisibility(View.VISIBLE);
				setRevert(convertView, revert);
			}
		}
		//论坛信息
		if(types=="2") {

			String particureply = list.get(position).get("particureply");
			if (particureply == "null") {
				particularsll.setVisibility(View.GONE);
			} else {
				particularsll.setVisibility(View.VISIBLE);
				setparticureply(convertView, particureply);
			}

			//论坛二级回复

			String reply = list.get(position).get("reply");
			if (reply == "null") {
				revertll.setVisibility(View.GONE);
			} else {
				revertll.setVisibility(View.VISIBLE);
				setReply(convertView, reply);
			}
		}
		return convertView;

	}


	//数据设置
	private void setdataText(View convertView,String data,int dataid){
		TextView textView= convertView.findViewById(dataid);
		textView.setText(data);
	}



    //上级回复数据
	private void setRevert(View convertView,String revert){
		JSONObject partJsonObject = null;
		try {
			partJsonObject = new JSONObject(revert);

			String nick_name=partJsonObject.getString("nick_name");
			String head_img=partJsonObject.getString("head_img");
			String contentwo=partJsonObject.getString("contentwo");
			String otime=partJsonObject.getString("otime");
			String otimes=partJsonObject.getString("otimes");

			String books_name=partJsonObject.getString("books_name");
			String books_img=partJsonObject.getString("books_img");
			String books_synopsis=partJsonObject.getString("books_synopsis");
			String zname=partJsonObject.getString("zname");
			String create_time=partJsonObject.getString("create_time");

			//上级回复头像
			ImageView topheadimg_image=convertView.findViewById(R.id.topheadimg);
			if (!TextUtils.isEmpty(head_img)) {
				head_img="http://"+head_img;
				Picasso.with(context).load(head_img).transform(new CircleTransform()).into(topheadimg_image);
			}

			//上级回复的书名
			ImageView top_book_name_image=convertView.findViewById(R.id.top_book_img);
			if (!TextUtils.isEmpty(books_img)) {
				books_img="http://"+books_img;
				Picasso.with(context).load(books_img).into(top_book_name_image);
			}



            //顶级姓名
			setdataText(convertView,nick_name,R.id.top_nick_name);
			//上级姓名
			setdataText(convertView,nick_name,R.id.top_nick_name);
            //上级时间
			setdataText(convertView,otime,R.id.top_timer);
			//上级回复日期
			setdataText(convertView,otimes,R.id.top_dater);
            //上级回复内容
			setdataText(convertView,contentwo,R.id.top_content);

			//书名
			setdataText(convertView,books_name,R.id.top_book_name);
            //简介
			setdataText(convertView,books_synopsis,R.id.top_book_synopsis);
			//作者
			setdataText(convertView,zname+" 著",R.id.top_book_author);
            //书的时间
			setdataText(convertView,create_time,R.id.top_book_time);



		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


    //书籍数据
	private void setparticureply(View convertView,String particulars){
		TextView books_nameText=convertView.findViewById(R.id.books_name);
		ImageView books_imgImage=convertView.findViewById(R.id.books_img);
		TextView books_synopsisText=convertView.findViewById(R.id.books_synopsis);
		TextView create_timeText=convertView.findViewById(R.id.create_time);
		TextView auther_nameText=convertView.findViewById(R.id.auther_name);

		JSONObject partJsonObject = null;
		try {
			partJsonObject = new JSONObject(particulars);

			String books_name=partJsonObject.getString("name");
			String books_img=partJsonObject.getString("cover");
			String books_synopsis=partJsonObject.getString("profile");
			String create_time=partJsonObject.getString("time");
			books_nameText.setText(books_name);
			books_synopsisText.setText(books_synopsis);
			create_timeText.setText(create_time);
			auther_nameText.setText("");
			if (!TextUtils.isEmpty(books_img)) {
				books_img="http://"+books_img;
				Picasso.with(context).load(books_img).into(books_imgImage);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}


	//论坛的数据
	private void setparticulars(View convertView,String particulars){
		TextView books_nameText=convertView.findViewById(R.id.books_name);
		ImageView books_imgImage=convertView.findViewById(R.id.books_img);
		TextView books_synopsisText=convertView.findViewById(R.id.books_synopsis);
		TextView create_timeText=convertView.findViewById(R.id.create_time);
		TextView auther_nameText=convertView.findViewById(R.id.auther_name);

		JSONObject partJsonObject = null;
		try {
			partJsonObject = new JSONObject(particulars);

			String books_name=partJsonObject.getString("books_name");
			String books_img=partJsonObject.getString("books_img");
			String books_synopsis=partJsonObject.getString("books_synopsis");
			String create_time=partJsonObject.getString("create_time");
			String name=partJsonObject.getString("name");

			books_nameText.setText(books_name);
			books_synopsisText.setText(books_synopsis);
			create_timeText.setText(create_time);
			auther_nameText.setText(name+" 著");
			if (!TextUtils.isEmpty(books_img)) {
				books_img="http://"+books_img;
				Picasso.with(context).load(books_img).into(books_imgImage);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}


	//上级回复数据
	private void setReply(View convertView,String revert){
		JSONObject partJsonObject = null;
		try {
			partJsonObject = new JSONObject(revert);

			String nick_name=partJsonObject.getString("nick_name");
			String head_img=partJsonObject.getString("head_img");
			String contentwo=partJsonObject.getString("content");
			String otime=partJsonObject.getString("otime");
			String otimes=partJsonObject.getString("otimes");

			String books_name=partJsonObject.getString("name"); //
			String books_img=partJsonObject.getString("cover"); //
			String books_synopsis=partJsonObject.getString("profile"); //
			String create_time=partJsonObject.getString("time");

			//上级回复头像
			ImageView topheadimg_image=convertView.findViewById(R.id.topheadimg);
			if (!TextUtils.isEmpty(head_img)) {
				head_img="http://"+head_img;
				Picasso.with(context).load(head_img).transform(new CircleTransform()).into(topheadimg_image);
			}

			//上级回复的书名
			ImageView top_book_name_image=convertView.findViewById(R.id.top_book_img);
			if (!TextUtils.isEmpty(books_img)) {
				books_img="http://"+books_img;
				Picasso.with(context).load(books_img).into(top_book_name_image);
			}



			//顶级姓名
			setdataText(convertView,nick_name,R.id.top_nick_name);
			//上级姓名
			setdataText(convertView,nick_name,R.id.top_nick_name);
			//上级时间
			setdataText(convertView,otime,R.id.top_timer);
			//上级回复日期
			setdataText(convertView,otimes,R.id.top_dater);
			//上级回复内容
			setdataText(convertView,contentwo,R.id.top_content);

			//书名
			setdataText(convertView,books_name,R.id.top_book_name);
			//简介
			setdataText(convertView,books_synopsis,R.id.top_book_synopsis);
			//作者
			setdataText(convertView,"",R.id.top_book_author);
			//书的时间
			setdataText(convertView,create_time,R.id.top_book_time);



		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	static class ViewHolder {
		TextView txt;
		public ImageView imageView;
	}


}


