package com.zan99.guaizhangmen.Activity.Men;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ldoublem.loadingviewlib.view.LVEatBeans;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.MenuActivity;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cz.msebera.android.httpclient.Header;


/**
 * Created by Administrator on 2017/12/2.
 */

public class WinGuaidouActivity extends BaseActivity {

    @BindView(R.id.guaidou)
    LinearLayout guaidou;

    @BindView(R.id.share)
    Button share;
    @BindView(R.id.button2)
    LinearLayout button2;
    @BindView(R.id.back_left) Button back_left;
    @BindView(R.id.share1) LinearLayout share1;

//    @BindView(R.id.loadingAni)
//    LVEatBeans loadingAni;

    private static String imageurl="";
    private static String title="";
    private static String text="";
    private static String member_id;
    private SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_guaidou);
        ButterKnife.bind(this);
        member_id = MenModel.member_id;
        downloadimg();
    }

    private void downloadimg(){

        String imgsurl=getStringData("imgsurl");
        if(imgsurl!=""){
            createWinGuaidouimg(imgsurl);
        }
    }


    private void createWinGuaidouimg(String imgsurl){
        pDialog=loadingpDialog(this);
        pDialog.show();
//        loadingAni.startAnim();
        ImageView imageView = new ImageView(WinGuaidouActivity.this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        guaidou.addView(imageView);

        String image=WinGuaidouActivity.imageurl=HttpUtil.BASE_URL+imgsurl;
        L.d("---->image:"+image);
        Picasso.with(WinGuaidouActivity.this).load(image).into(imageView,new com.squareup.picasso.Callback(){

            @Override
            public void onSuccess() {
                pDialog.hide();
            }

            @Override
            public void onError() {
                pDialog.hide();

            }
        });
    }



    private void setShareimgtext(){
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题：微信、QQ（新浪微博不需要标题）
//        oks.setTitle(title);  //最多30个字符

        // text是分享文本：所有平台都需要这个字段
//        oks.setText(text);  //最多40个字符

        //网络图片的url：所有平台
        oks.setImageUrl(WinGuaidouActivity.imageurl);//网络图片rul
        // url：仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl("http://sharesdk.cn");   //网友点进链接后，可以看到分享的详情

        // Url：仅在QQ空间使用
        //oks.setTitleUrl("http://www.baidu.com");  //网友点进链接后，可以看到分享的详情

        // 启动分享GUI
        oks.show(WinGuaidouActivity.this);
    }





    @butterknife.OnClick({R.id.share1,R.id.share, R.id.button2,R.id.back_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share1:
            case R.id.share:
                setShareimgtext();
                break;
            case R.id.back_left:
            case R.id.button2:
                finish();
                break;
        }
    }



}


