package com.zan99.guaizhangmen.Activity.Men;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class AboutusActivity extends BaseActivity {

    @BindView(R.id.button2)
    LinearLayout button2;
    @BindView(R.id.back_left)
    Button back_left;
    @BindView(R.id.aboutcontent)
    TextView aboutcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        ButterKnife.bind(this);
        String content="      掌门课堂团队的工程师们从2011年就一直走" +
                "在营销工具的探索之路上，并且开发的拓客" +
                "168微信营销软件在中国的大地上帮助了10万" +
                "人走向成功。\n" +
                "      但我们深知要不断的自我创新，掌门课程的小" +
                "伙伴们一直在寻找更好的解决方案，最终达成" +
                "让掌门课程成为无人看管的自动化生态系统。\n" +
                "      历经两年的研发最终获得了成功，在用户" +
                "建立好任务后手机到时间就自动工作，自动执" +
                "行各种复杂的营销任务，可以做到微信、快手" +
                "微博、QQ等多个平台的全自动营销推广，掌门" +
                "课堂自动化营销平台就是一个任劳任怨的员工，" +
                "掌门课堂自动营销平台也为创业者打造的自动化" +
                "赚钱机器。\n";
        content+="客服电话：400-800-4800";
        aboutcontent.setText(content);


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
