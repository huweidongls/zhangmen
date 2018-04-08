package com.zan99.guaizhangmen.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.donkingliang.banner.CustomBanner;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.Guai.BookActivity;
import com.zan99.guaizhangmen.Activity.Guai.CategoryActivity;
import com.zan99.guaizhangmen.Activity.Guai.SearchActivity;
import com.zan99.guaizhangmen.Activity.TemplateActivity;
import com.zan99.guaizhangmen.Adapter.FenleiAdapter;
import com.zan99.guaizhangmen.Adapter.GuaiBooksAdapter;
import com.zan99.guaizhangmen.Adapter.MyViewPagerAdapter;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.GuaiFenleiEntity;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.ACache;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.Widget.ListViewLinearlayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by 99zan on 2018/1/3.
 */

public class GuaiFragment extends Fragment implements ViewPager.OnPageChangeListener {

    /**
     * 分类列表的实体类数组
     */
    private List<GuaiFenleiEntity> fenleiList = new ArrayList<>();
    /**
     * 分类列表的适配器
     */
    private FenleiAdapter fenleiAdapter;
    private ArrayList<HashMap<String, String>> bannerlist = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> booksList = new ArrayList<HashMap<String, String>>();
    public static JSONArray categoryList;
    @BindView(R.id.banner)
    CustomBanner banner;
    @BindView(R.id.glist)
    ListViewLinearlayout booksListView;
    //    @BindView(R.id.gallery)
//    LinearLayout gallery;
    @BindView(R.id.search_btn)
    RelativeLayout search_btn;
    @BindView(R.id.search_btn_img)
    ImageButton search_btn_img;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.guai_viewPager)
    ViewPager viewPager;
    @BindView(R.id.points)
    ViewGroup points;//小圆点指示器
    private int totalPage;//总的页数
    private int mPageSize = 8;//每页显示的最大数量
    private ImageView[] ivPoints;//小圆点图片集合
    private List<View> viewPagerList;//GridView作为一个View对象添加到ViewPager集合中
    private int currentPage;//当前页
    /**
     * 判断是否是第一次加载
     */
    private boolean isPage = false;
    private GuaiBooksAdapter booksAdapter;
    private boolean isRefresh = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_guai, null);
        ButterKnife.bind(this, view);

        initView(view, inflater);

        return view;
    }

    private void initView(View view, final LayoutInflater inflater) {
        loadData(inflater);
        refreshLayout.setRefreshHeader(new MaterialHeader(getContext()).setShowBezierWave(true));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                isRefresh = true;
                loadData(inflater);
                refreshlayout.finishRefresh();
            }
        });

        booksAdapter = new GuaiBooksAdapter(getContext(), booksList);

        booksListView.setAdapter(booksAdapter);

        // 书籍点击事件
        booksListView.setOnItemClickListener(new ListViewLinearlayout.OnItemClickListener() {

            @Override
            public void onItemClicked(View v, Object obj, int position) {

                Intent intent = new Intent(getContext(), BookActivity.class);
                intent.putExtra("books_id", v.getTag(R.id.books_id).toString());
                startActivity(intent);

            }
        });
    }

    @OnClick({R.id.search_btn, R.id.search_btn_img})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.search_btn:
                intent.setClass(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.search_btn_img:
                intent.setClass(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void loadData(LayoutInflater inflater) {
        loadBanner();
        loadCategory(inflater);
        loadBooks();
    }

    private void loadBooks() {
        ACache aCache = ACache.get(getContext());
        byte[] value = aCache.getAsBinary("books");
        if (value != null && !isRefresh) {
            try {
                JSONObject jsonObject = JSON.parseObject(new String(value, "utf-8"));
                loadBooksView(jsonObject.getJSONArray("dataList"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            HttpUtil.get("/author.php/Nexts/NewBookslist", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        try {
                            JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf-8"));
                            ACache aCache = ACache.get(getContext());
                            aCache.put("books", responseBody, 2 * ACache.TIME_DAY);
                            loadBooksView(jsonObject.getJSONArray("dataList"));
                        } catch (JSONException e) {
                            BaseActivity.getTyChaterrmsg("/author.php/Nexts/NewBookslist", "");
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            BaseActivity.getTyChaterrmsg("/author.php/Nexts/NewBookslist", "");
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    BaseActivity.getTyChaterrmsg("author.php/Nexts/NewBookslist", "");
                }
            });
        }
    }

    private void loadBooksView(JSONArray dataList) {
        booksList.clear();
        for (int i = 0; i < dataList.size(); i++) {
            JSONObject myjObject = dataList.getJSONObject(i);
            HashMap<String, String> map = new HashMap<String, String>();
            L.d("book:" + myjObject.getString("booksImg"));
            map.put("books_id", myjObject.getString("booksId"));
            map.put("books_name", myjObject.getString("booksName"));
            map.put("books_img", myjObject.getString("booksImg"));
            map.put("books_synopsis", myjObject.getString("booksSynopsis"));
            map.put("create_time", myjObject.getString("createTime"));
            map.put("author_name", myjObject.getString("authorName"));
            booksList.add(map);
        }
        if (booksAdapter == null) {
            booksAdapter = new GuaiBooksAdapter(getContext(), booksList);
        }
        booksAdapter.notifyDataSetChanged();
    }

    private void loadBanner() {
        ACache aCache = ACache.get(getContext());
        byte[] value = aCache.getAsBinary("bannerList");
        if (value != null && !isRefresh) {
            try {
                JSONObject jsonObject = JSON.parseObject(new String(value, "utf-8"));
                initBannerView(jsonObject.getJSONArray("dataList"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            HttpUtil.get("admin.php/Systeminterface/banner_show", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        try {
                            JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf-8"));
                            ACache aCache = ACache.get(getContext());
                            aCache.put("bannerList", responseBody, 2 * ACache.TIME_DAY);
                            initBannerView(jsonObject.getJSONArray("dataList"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/banner_show", "");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/banner_show", "");
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/banner_show", "");
                }
            });
        }

    }

    private void initBannerView(JSONArray dataList) {
        bannerlist = new ArrayList<HashMap<String, String>>();

        try {
            for (int i = 0; i < dataList.size(); i++) {
                JSONObject temj = dataList.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("choose_id", temj.getString("chooseId"));
                map.put("type", temj.getString("type"));
                map.put("banner_img", "http://" + temj.getString("bannerImg"));
                bannerlist.add(map);
            }
            banner.setPages(new CustomBanner.ViewCreator<HashMap<String, String>>() {
                @Override
                public View createView(Context context, int i) {
                    ImageView imageView = new ImageView(context);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    return imageView;
                }

                @Override
                public void updateUI(Context context, View view, int i, HashMap<String, String> map) {
                    ImageView imageView = (ImageView) view;
//                    Log.e("111", map.get("banner_img")+111111);
                    Picasso.with(context).load(map.get("banner_img")).into(imageView);
                }
            }, bannerlist)
                    .setIndicatorInterval(5)
                    .setOnPageClickListener(new CustomBanner.OnPageClickListener() {
                        @Override
                        public void onPageClick(int position, Object o) {
                            HashMap<String, String> map = bannerlist.get(position);

                            Intent intent = new Intent();
                            if (map.get("type").equals("2")) {
                                intent.setClass(getContext(), BookActivity.class);
                                intent.putExtra("books_id", map.get("choose_id"));

                            } else {
                                intent.setClass(getContext(), TemplateActivity.class);
                                intent.putExtra("id", map.get("choose_id"));
                                if (map.get("type").equals("1")) {
                                    intent.putExtra("type", "fangtan");
                                } else if (map.get("type").equals("3")) {
                                    intent.putExtra("type", "jiangshi");
                                } else {
                                    intent.putExtra("type", "zhangjie");
                                }

                            }
                            startActivity(intent);
                        }
                    })
                    .startTurning(5000);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadCategory(final LayoutInflater inflater) {
        ACache aCache = ACache.get(getContext());
        byte[] value = aCache.getAsBinary("category");
        if (value != null && !isRefresh) {
            try {
                JSONObject jsonObject = JSON.parseObject(new String(value, "utf-8"));
//                initCategoryView(jsonObject.getJSONArray("dataList"), inflater);
                initCategoryView(jsonObject.getJSONArray("dataList"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            HttpUtil.get("admin.php/Systeminterface/all_fication", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        try {
                            JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf-8"));
                            ACache aCache = ACache.get(getContext());
                            aCache.put("category", responseBody, 2 * ACache.TIME_DAY);
//                            initCategoryView(jsonObject.getJSONArray("dataList"), inflater);
                            initCategoryView(jsonObject.getJSONArray("dataList"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/all_fication", "");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/all_fication", "");
                        }

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/all_fication", "");
                }
            });
        }
    }

    /**
     * 初始化viewpager
     */
    private void initPage() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        //总的页数，取整（这里有三种类型：Math.ceil(3.5)=4:向上取整，只要有小数都+1  Math.floor(3.5)=3：向下取整  Math.round(3.5)=4:四舍五入）
        totalPage = (int) Math.ceil(fenleiList.size() * 1.0 / mPageSize);
        viewPagerList = new ArrayList<>();
        for (int i = 0; i < totalPage; i++) {
            //每个页面都是inflate出一个新实例
            GridView gridView = (GridView) inflater.inflate(R.layout.layout_fenlei_gridview, viewPager, false);
            fenleiAdapter = new FenleiAdapter(getContext(), fenleiList, i, mPageSize);
            gridView.setAdapter(fenleiAdapter);
            //添加item点击监听
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int pos = position + currentPage * mPageSize;
                    Log.i("TAG", "position的值为：" + position + "-->pos的值为：" + pos);
                    Intent intent = new Intent(getContext(), CategoryActivity.class);
                    intent.putExtra("cid", fenleiList.get(position).getFicationId());
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
            //每一个GridView作为一个View对象添加到ViewPager集合中
            viewPagerList.add(gridView);
        }
        //设置ViewPager适配器
        viewPager.setAdapter(new MyViewPagerAdapter(viewPagerList));
        if (totalPage > 1 && !isPage) {
            //小圆点指示器
            ivPoints = new ImageView[totalPage];
            for (int i = 0; i < ivPoints.length; i++) {
                ImageView imageView = new ImageView(getContext());
                //设置图片的宽高
                imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
                if (i == 0) {
                    imageView.setBackgroundResource(R.drawable.enable);
                } else {
                    imageView.setBackgroundResource(R.drawable.disable);
                }
                ivPoints[i] = imageView;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutParams.leftMargin = 20;//设置点点点view的左边距
                layoutParams.rightMargin = 20;//设置点点点view的右边距
                points.addView(imageView, layoutParams);
            }
            isPage = true;
        }
        //设置ViewPager滑动监听
        viewPager.addOnPageChangeListener(this);
    }

    private void initCategoryView(JSONArray dataList) {
        categoryList = dataList;
        if (fenleiAdapter == null) {
            try {
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject temj = dataList.getJSONObject(i);
                    fenleiList.add(new GuaiFenleiEntity(temj.getString("img"), temj.getString("name"), temj.getString("ficationId")));
                }
                initPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                fenleiList.clear();
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject temj = dataList.getJSONObject(i);
                    fenleiList.add(new GuaiFenleiEntity(temj.getString("img"), temj.getString("name"), temj.getString("ficationId")));
                }
                initPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //改变小圆圈指示器的切换效果
        setImageBackground(position);
        currentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 改变点点点的切换效果
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < ivPoints.length; i++) {
            if (i == selectItems) {
                ivPoints[i].setBackgroundResource(R.drawable.enable);
            } else {
                ivPoints[i].setBackgroundResource(R.drawable.disable);
            }
        }
    }

//    private void initCategoryView(JSONArray dataList, LayoutInflater inflater) {
//        gallery.removeAllViews();
//        categoryList = dataList;
//        try {
//            for(int i = 0; i<dataList.size(); i++){
//                JSONObject temj = dataList.getJSONObject(i);
//                View view = inflater.inflate(R.layout.layout_gallery_item,
//                        gallery, false);
//
//                ImageView img = (ImageView) view
//                        .findViewById(R.id.ItemImage);
//
//                TextView ItemName = (TextView) view.findViewById(R.id.ItemName);
//                ItemName.setTextSize(20);
//                ItemName.setText(temj.getString("name"));
//                if (!TextUtils.isEmpty(temj.getString("img"))) {
//                    Picasso.with(getContext()).load("http://"+temj.getString("img")).into(img);
//                }
//                img.setScaleType(ImageView.ScaleType.FIT_XY);
//                view.setTag(R.id.category_id, temj.getString("ficationId"));
//                view.setTag(R.id.position, i);
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(getContext(), CategoryActivity.class);
//                        intent.putExtra("cid",view.getTag(R.id.category_id).toString());
//                        intent.putExtra("position", Integer.parseInt(view.getTag(R.id.position).toString()));
//                        startActivity(intent);
//                    }
//                });
//                gallery.addView(view);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

}
