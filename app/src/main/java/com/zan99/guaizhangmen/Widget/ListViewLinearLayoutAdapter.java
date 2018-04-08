package com.zan99.guaizhangmen.Widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.zan99.guaizhangmen.Widget.ListViewLinearlayout.MNotifyDataSetChangedIF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/12/21.
 */

public abstract class ListViewLinearLayoutAdapter {
    private ArrayList<HashMap<String, String>> list;
    private Context context;
    private MNotifyDataSetChangedIF changedIF;
    public ListViewLinearLayoutAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    public LayoutInflater getLayoutInflater() {
        if (context != null) {
            return LayoutInflater.from(context);
        }

        return null;
    }

    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    };

    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        }
        return null;
    };

    /**
     * 绑定adapter中的监听
     * @param changedIF
     */
    public void setNotifyDataSetChangedIF(MNotifyDataSetChangedIF changedIF){
        this.changedIF = changedIF;
    }
    /**
     * 数据刷新
     */
    public void notifyDataSetChanged(){
        if (changedIF != null) {
            changedIF.changed();
        }
    }
    /**
     * 供子类复写
     *
     * @param position
     * @return
     */
    public abstract View getView(int position);
}