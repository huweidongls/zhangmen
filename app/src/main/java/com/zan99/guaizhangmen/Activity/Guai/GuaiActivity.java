package com.zan99.guaizhangmen.Activity.Guai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.donkingliang.banner.CustomBanner;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zan99.guaizhangmen.Activity.TemplateActivity;
import com.zan99.guaizhangmen.Adapter.GuaiBooksAdapter;
import com.zan99.guaizhangmen.Adapter.NewBooksAdapter;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Loader.BannerImageLoder;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.Util.ToastUtil;
import com.zan99.guaizhangmen.Widget.CustomerGridView;
import com.zan99.guaizhangmen.Widget.ListViewLinearlayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/*
 * 全局常量考虑删除
 */
public class GuaiActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> bannerlist = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> booksList = new ArrayList<HashMap<String, String>>();
    public static JSONArray categoryList;
    CustomBanner banner;
    ListViewLinearlayout booksListView;
    LayoutInflater mInflater;
    LinearLayout gallery;
    @BindView(R.id.search_btn)
    RelativeLayout search_btn;
    private SmartRefreshLayout refreshLayout;
    private GuaiBooksAdapter booksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guai);
        ButterKnife.bind(this);
        mInflater = LayoutInflater.from(this);
        initView();
    }

    private void initView() {
        loadData();
        banner = (CustomBanner) findViewById(R.id.banner);
        booksListView = (ListViewLinearlayout) findViewById(R.id.glist);
//        gallery = (LinearLayout) findViewById(R.id.gallery);

        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadData();
                refreshlayout.finishRefresh();
            }
        });

        booksAdapter = new GuaiBooksAdapter(GuaiActivity.this, booksList);

        booksListView.setAdapter(booksAdapter);

        // 书籍点击事件
        booksListView.setOnItemClickListener(new ListViewLinearlayout.OnItemClickListener() {

            @Override
            public void onItemClicked(View v, Object obj, int position) {

                Intent intent = new Intent(GuaiActivity.this, BookActivity.class);
                intent.putExtra("books_id",v.getTag(R.id.books_id).toString());
                startActivity(intent);

            }
        });
    }

    private void loadData(){
        loadBanner();
        loadCategory();
        loadBooks();
    }

    // 搜索按钮
    public void search(View view){
        startActivity(new Intent(GuaiActivity.this, SearchActivity.class));
    }

    private void loadBooks() {
        HttpUtil.get("/author.php/Nexts/NewBookslist", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf-8"));
                        loadBooksView(jsonObject.getJSONArray("dataList"));
                    } catch (JSONException e) {
                        BaseActivity.getTyChaterrmsg("/author.php/Nexts/NewBookslist","");
                        e.printStackTrace();
                    }catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        BaseActivity.getTyChaterrmsg("/author.php/Nexts/NewBookslist","");
                    }


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg("author.php/Nexts/NewBookslist","");
            }
        });
    }

    private void loadBooksView(JSONArray dataList) {
        booksList.clear();
        for (int i = 0; i < dataList.size(); i++) {
            JSONObject myjObject = dataList.getJSONObject(i);
            HashMap<String, String> map = new HashMap<String, String>();
            L.d("book:"+myjObject.getString("booksImg"));
            map.put("books_id", myjObject.getString("booksId"));
            map.put("books_name", myjObject.getString("booksName"));
            map.put("books_img", myjObject.getString("booksImg"));
            map.put("books_synopsis", myjObject.getString("booksSynopsis"));
            map.put("create_time", myjObject.getString("createTime"));
            map.put("author_name", myjObject.getString("authorName"));
            booksList.add(map);
        }
        booksAdapter.notifyDataSetChanged();
    }

    private void loadBanner() {
        HttpUtil.get("admin.php/Systeminterface/banner_show",new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try {
                        JSONObject jsonObject  =  JSON.parseObject(new String(responseBody, "utf-8"));
                        initBannerView(jsonObject.getJSONArray("dataList"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/banner_show","");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/banner_show","");
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/banner_show","");
            }
        });

    }

    private void initBannerView(JSONArray dataList) {
        bannerlist = new ArrayList<HashMap<String, String>>();

        try {
            for(int i=0;i<dataList.size();i++){
                JSONObject temj = dataList.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("choose_id",temj.getString("chooseId"));
                map.put("type",temj.getString("type"));
                map.put("banner_img","http://"+temj.getString("bannerImg"));
                bannerlist.add(map);
            }
            banner.setPages(new CustomBanner.ViewCreator<HashMap<String, String>>() {
                @Override
                public View createView(Context context, int i) {
                    ImageView imageView = new ImageView(GuaiActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    return imageView;
                }

                @Override
                public void updateUI(Context context, View view, int i, HashMap<String,String> map) {
                    ImageView imageView = (ImageView) view;
                    Picasso.with(GuaiActivity.this).load(map.get("banner_img")).into(imageView);
                }
            },bannerlist)
                    .setIndicatorInterval(5)
                    .setOnPageClickListener(new CustomBanner.OnPageClickListener() {
                        @Override
                        public void onPageClick(int position, Object o) {
                            HashMap<String, String> map = bannerlist.get(position);

                            Intent intent = new Intent();
                            if(map.get("type").equals("2")){
                                intent.setClass(GuaiActivity.this, BookActivity.class);
                                intent.putExtra("books_id", map.get("choose_id"));

                            }else{
                                intent.setClass(GuaiActivity.this, TemplateActivity.class);
                                intent.putExtra("id",map.get("choose_id"));
                                if(map.get("type").equals("1")){
                                    intent.putExtra("type","fangtan");
                                }else if(map.get("type").equals("3")){
                                    intent.putExtra("type","jiangshi");
                                }else{
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

    private void loadCategory() {
        HttpUtil.get("admin.php/Systeminterface/all_fication",new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try {
                        JSONObject jsonObject  = JSON.parseObject(new String(responseBody, "utf-8"));
//                        initCategoryView(jsonObject.getJSONArray("dataList"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/all_fication","");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/all_fication","");
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg("admin.php/Systeminterface/all_fication","");
            }
        });
    }

//    private void initCategoryView(JSONArray dataList) {
//        gallery.removeAllViews();
//        categoryList = dataList;
//        try {
//            for(int i = 0; i<dataList.size(); i++){
//                JSONObject temj = dataList.getJSONObject(i);
//                View view = mInflater.inflate(R.layout.layout_gallery_item,
//                        gallery, false);
//
//                ImageView img = (ImageView) view
//                        .findViewById(R.id.ItemImage);
//
//                TextView ItemName = (TextView) view.findViewById(R.id.ItemName);
//                ItemName.setTextSize(20);
//                ItemName.setText(temj.getString("name"));
//                if (!TextUtils.isEmpty(temj.getString("img"))) {
//                    Picasso.with(GuaiActivity.this).load("http://"+temj.getString("img")).into(img);
//                }
//                img.setScaleType(ImageView.ScaleType.FIT_XY);
//                view.setTag(R.id.category_id, temj.getString("ficationId"));
//                view.setTag(R.id.position, i);
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(GuaiActivity.this, CategoryActivity.class);
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
