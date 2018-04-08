package com.zan99.guaizhangmen.Activity.Guai;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zan99.guaizhangmen.Adapter.NewBooksAdapter;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.ToastUtil;
import com.zan99.guaizhangmen.Widget.SearchView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2017/12/3.
 */

public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.list_view)
    ListView list_view;

    private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private SearchView searchView;
    private NewBooksAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        list_view.addHeaderView(getHeaderView());
        adapter = new NewBooksAdapter(SearchActivity.this,list);
        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(SearchActivity.this, BookActivity.class);
                intent.putExtra("books_id", list.get(i-1).get("books_id"));
                startActivity(intent);
            }
        });
    }

    private View getHeaderView() {
        searchView = new SearchView(SearchActivity.this, null);
        searchView.setOnSearchAction(new SearchView.OnSearchAction() {
            @Override
            public void onSearchAction(String keyword) {
                if(!TextUtils.isEmpty(keyword)){
                    searchAction(keyword);
                }
            }
        });
        return searchView;
    }

    private void searchAction(String keyword) {
        final RequestParams params = new RequestParams();
        params.put("content", keyword);
        HttpUtil.post("author.php/Nexts/BooksSearch", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = JSON.parseObject(new String(responseBody, "utf8"));
                    JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                    if(jsonArray.size() > 0){
                        showDatas(jsonArray);
                    }else {
                        showEmpty();
                    }
                } catch (UnsupportedEncodingException e) {
                    showEmpty();
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg("author.php/Nexts/BooksSearch",params.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showEmpty();
                BaseActivity.getTyChaterrmsg("author.php/Nexts/BooksSearch",params.toString());
            }
        });
//        ToastUtil.show(SearchActivity.this,keyword, Toast.LENGTH_SHORT);
    }

    private void showDatas(JSONArray dataList) {
        list_view.setVisibility(View.VISIBLE);
        findViewById(R.id.emptytext).setVisibility(View.GONE);
        list.clear();
        for(int i=0;i<dataList.size();i++){
            JSONObject myjObject = dataList.getJSONObject(i);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("books_id", myjObject.getString("booksId"));
            map.put("books_name", myjObject.getString("booksName"));
            map.put("books_img", myjObject.getString("booksImg"));
            map.put("books_synopsis", myjObject.getString("booksSynopsis"));
            map.put("create_time", myjObject.getString("createTime"));
            map.put("author_name", myjObject.getString("authorName"));
            map.put("thumb", myjObject.getString("thumb"));
            map.put("collection", myjObject.getString("collection"));
            list.add(map);
        }
        adapter.notifyDataSetChanged();
    }



    private void showEmpty() {
        list.clear();
        findViewById(R.id.emptytext).setVisibility(View.VISIBLE);
        //list_view.addHeaderView(getHeaderView());
        adapter.notifyDataSetChanged();
        //list_view.setVisibility(View.GONE);

    }

}
