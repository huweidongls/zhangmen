package com.zan99.guaizhangmen.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zan99.guaizhangmen.Bean.WalletMxBean;
import com.zan99.guaizhangmen.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99zan on 2018/2/7.
 */

public class MyWalletMxAdapter extends BaseAdapter {

    private Context context;
    private List<WalletMxBean.DataListBean> data = new ArrayList<>();
    private LayoutInflater inflater;

    public MyWalletMxAdapter(Context context, List<WalletMxBean.DataListBean> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.layout_my_wallet_mx_item, null);
            holder.name = convertView.findViewById(R.id.name);
            holder.createTime = convertView.findViewById(R.id.create_time);
            holder.fee = convertView.findViewById(R.id.fee);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        WalletMxBean.DataListBean listBean = data.get(position);

        holder.createTime.setText(listBean.getTime());
        if(listBean.getType() == 0){
            holder.name.setText("提现");
            holder.fee.setText("- "+listBean.getFee());
        }else if(listBean.getType() == 1){
            holder.name.setText("所在"+listBean.getMember_name()+listBean.getNowlevel()+"级分销");
            holder.fee.setText("+ "+listBean.getFee());
        }

        return convertView;
    }

    private class ViewHolder{
        TextView name;
        TextView createTime;
        TextView fee;
    }

}
