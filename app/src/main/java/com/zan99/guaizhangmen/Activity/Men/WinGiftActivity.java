package com.zan99.guaizhangmen.Activity.Men;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2017/12/2.
 */

public class WinGiftActivity extends BaseActivity {

    @BindView(R.id.button2)
    LinearLayout button2;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_gift);
        ButterKnife.bind(this);

        String member_id= MenModel.member_id;
        pDialog=loadingpDialog(this);
        pDialog.show();

        WebView webview = (WebView) findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                pDialog.hide();
            }
        });
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl(HttpUtil.BASE_URL+"/member.php/Interfaces/goodl/member_id/"+member_id);


    }


    @butterknife.OnClick({R.id.button2,R.id.back_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_left:
            case R.id.button2:
                finish();
                break;
        }
    }
}
