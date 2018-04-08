package com.zan99.guaizhangmen.Activity.Zhang;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zan99.guaizhangmen.Util.ToastUtil;

/**
 * Created by Administrator on 2017/11/29.
 */

public class DakaInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String author_id = getIntent().getStringExtra("author_id");
    }
}
