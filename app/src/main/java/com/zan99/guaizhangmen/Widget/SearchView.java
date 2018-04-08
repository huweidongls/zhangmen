package com.zan99.guaizhangmen.Widget;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zan99.guaizhangmen.R;

/**
 * Created by Administrator on 2017/12/4.
 */

public class SearchView extends LinearLayout implements TextWatcher, View.OnClickListener {
    private final TextView submit_search;
    private final ImageView back_mg;
    /**
     * 输入框
     */
    private EditText et_search;
    /**
     * 输入框后面的那个清除按钮
     */
    private Button bt_clear;
    private LinearLayout back;

    private OnSearchAction listener = null;

    public interface OnSearchAction{
        void onSearchAction(String keyword);
    }

    public void setOnSearchAction(OnSearchAction listener){
        this.listener = listener;
    }


    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**加载布局文件*/
        LayoutInflater.from(context).inflate(R.layout.layout_search_view, this, true);
        /***找出控件*/
        et_search = (EditText) findViewById(R.id.et_search);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        back = (LinearLayout) findViewById(R.id.back_left);
        back_mg = (ImageView) findViewById(R.id.back_left_img);
        submit_search = (TextView) findViewById(R.id.submit_search);
        bt_clear.setVisibility(GONE);

        et_search.addTextChangedListener(this);
        bt_clear.setOnClickListener(this);
        back.setOnClickListener(this);
        back_mg.setOnClickListener(this);
        submit_search.setOnClickListener(this);


    }

    public String getText(){
        return et_search.getText().toString().trim();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    /****
     * 对用户输入文字的监听
     *
     * @param editable
     */
    @Override
    public void afterTextChanged(Editable editable) {
        /**获取输入文字**/
        String input = et_search.getText().toString().trim();
        if (input.isEmpty()) {
            bt_clear.setVisibility(GONE);
        } else {
            bt_clear.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_left:
                ((Activity) getContext()).finish();
                break;
            case R.id.bt_clear:
                et_search.setText("");
                break;
            case R.id.submit_search:
                if(listener != null){
                    listener.onSearchAction(et_search.getText().toString().trim());
                }
                break;
            case R.id.back_left_img:
                ((Activity) getContext()).finish();
                break;
        }
    }
}