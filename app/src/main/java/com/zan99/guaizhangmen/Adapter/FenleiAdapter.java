package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.Guai.CategoryActivity;
import com.zan99.guaizhangmen.Model.GuaiFenleiEntity;
import com.zan99.guaizhangmen.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99zan on 2018/1/15.
 */

public class FenleiAdapter extends BaseAdapter {

    private List<GuaiFenleiEntity> listData;
    private LayoutInflater inflater;
    private Context context;
    private int mIndex;//页数下标，表示第几页，从0开始
    private int mPagerSize;//每页显示的最大数量

    public FenleiAdapter(Context context,List<GuaiFenleiEntity> listData,int mIndex,int mPagerSize) {
        this.context = context;
        this.listData = listData;
        this.mIndex = mIndex;
        this.mPagerSize = mPagerSize;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 先判断数据集的大小是否足够显示满本页？listData.size() > (mIndex + 1)*mPagerSize
     * 如果满足，则此页就显示最大数量mPagerSize的个数
     * 如果不够显示每页的最大数量，那么剩下几个就显示几个 (listData.size() - mIndex*mPagerSize)
     */
    @Override
    public int getCount() {
        return listData.size() > (mIndex + 1)*mPagerSize ? mPagerSize : (listData.size() - mIndex*mPagerSize);
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position + mIndex * mPagerSize);
    }

    @Override
    public long getItemId(int position) {
        return position + mIndex * mPagerSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.layout_guai_fenlei_item,parent,false);
            holder = new ViewHolder();
            holder.proName = (TextView) convertView.findViewById(R.id.guai_fenlei_item_text);
            holder.imgUrl = (ImageView) convertView.findViewById(R.id.guai_fenlei_item_pic);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        //重新确定position（因为拿到的是总的数据源，数据源是分页加载到每页的GridView上的，为了确保能正确的点对不同页上的item）
        final int pos = position + mIndex*mPagerSize;//假设mPagerSize=8，假如点击的是第二页（即mIndex=1）上的第二个位置item(position=1),那么这个item的实际位置就是pos=9
        GuaiFenleiEntity bean = listData.get(pos);
        holder.proName.setText(bean.getName());
        Picasso.with(context).load("http://"+bean.getImg()).into(holder.imgUrl);
        //添加item监听
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(context,"你点击了 "+listData.get(pos).getName(),Toast.LENGTH_SHORT).show();
//            }
//        });
        return convertView;
    }

    class ViewHolder{
        private TextView proName;
        private ImageView imgUrl;
    }

}
