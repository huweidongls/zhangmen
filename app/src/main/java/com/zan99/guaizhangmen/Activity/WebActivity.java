package com.zan99.guaizhangmen.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zan99.guaizhangmen.R;

/**
 * Created by Administrator on 2017/12/4.
 */

public class WebActivity extends AppCompatActivity {
    private String type;

//    @BindView(R.id.back_left)
//    Button back_left;
//    @BindView(R.id.back_layout)
//    Button back_layout;

    View public_header;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        //ButterKnife.bind(this);
        public_header=findViewById(R.id.public_header);
        LinearLayout back_layout =public_header.findViewById(R.id.back_layout);
        Button back_left =public_header.findViewById(R.id.back_left);

        type = getIntent().getStringExtra("type");
        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        final WebView webView = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        final String finalUrl = url;
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                webView.loadUrl(finalUrl);
                return true;
            }
        });
        webView.loadUrl(url);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }}
        );
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);

        back_layout.setVisibility(View.VISIBLE);
        back_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if(type.equals("tuiguang")){
                    intent.setClass(WebActivity.this, MenuActivity.class);
                }
                startActivity(intent);

            }
        });

        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if(type.equals("tuiguang")){
                    intent.setClass(WebActivity.this, MenuActivity.class);
                }
                startActivity(intent);
            }
        });
    }
}
