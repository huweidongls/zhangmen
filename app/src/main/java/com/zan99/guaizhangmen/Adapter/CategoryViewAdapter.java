package com.zan99.guaizhangmen.Adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/11/27.
 */

public class CategoryViewAdapter extends PagerAdapter {

    private List<View> mDataList;

    public CategoryViewAdapter (List<View> dataList){
        mDataList = dataList;
    }
    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mDataList.get(position));
        return mDataList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mDataList.get(position));
    }
}
