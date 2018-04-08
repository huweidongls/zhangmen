package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.VectorEnabledTintResources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.Zhang.ZhangActivity;
import com.zan99.guaizhangmen.Fragment.ZhangFragment;
import com.zan99.guaizhangmen.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/12/2.
 */

public class ZhangDakaAdapter extends RecyclerView.Adapter<ZhangDakaAdapter.DakaViewHolder> {

    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener = null;

    public ZhangDakaAdapter(Context context, ArrayList<HashMap<String, String>> dakaList) {
        this.list = dakaList;
        this.mContext = context;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String author_id);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public DakaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DakaViewHolder dakaViewHolder = new DakaViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_zhang_daka_item, parent, false));
        return dakaViewHolder;
    }

    @Override
    public void onBindViewHolder(final DakaViewHolder holder, final int position) {
        Picasso.with(mContext).load(list.get(position).get("daka_img")).into(holder.ivimage);
        holder.itemView.setTag(R.id.author_id,list.get(position).get("author_id"));
        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(holder.itemView, list.get(position).get("author_id"));
                }
            });
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ZhangFragment.dip2px(mContext,ZhangFragment.widthone),LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(20,0,0,0);
        holder.ivimage.setLayoutParams(layoutParams);

        //holder.ivimage.setLayoutParams(new LinearLayout.LayoutParams(ZhangActivity.dip2px(mContext,ZhangActivity.widthone),LinearLayout.LayoutParams.WRAP_CONTENT));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DakaViewHolder extends RecyclerView.ViewHolder {

        ImageView ivimage;

        public DakaViewHolder(View convertView){
            super(convertView);
            ivimage = (ImageView) convertView.findViewById(R.id.daka_img);

        }
    }

    private Resources mResources;


}
