package com.zan99.guaizhangmen.Activity.Men;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.pay.PayResult;
import com.zan99.guaizhangmen.wxapi.WXEntryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class GuaidouPayActivity extends BaseActivity {

    @BindView(R.id.py_pay_zfb)  RadioButton py_pay_zfb;
    @BindView(R.id.py_wx)  RadioButton py_wx;
    @BindView(R.id.bean_num) TextView bean_num;
    @BindView(R.id.bean_money) TextView bean_money;
    @BindView(R.id.button2) LinearLayout button2;
    @BindView(R.id.tel) TextView tel;
    @BindView(R.id.passmobile) LinearLayout passmobile;
    @BindView(R.id.back_left) Button back_left;

    private static final int SDK_PAY_FLAG = 1;
    private String member_id;

    private String setting;
    private String out_trade_no;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guaidou_pay);
        ButterKnife.bind(this);
        changeRadio();

        bean_num.setText(getIntent().getStringExtra("bean_num"));
        bean_money.setText(getIntent().getStringExtra("bean_money")+"元");
        member_id=getIntent().getStringExtra("member_id");
        setting=getIntent().getStringExtra("setting_id");
        SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
        String mobile= L.decrypt(pref.getString("mobile",""), MenModel.LKEY);
        tel.setText(settingphone(mobile));

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
        String mobile= L.decrypt(pref.getString("mobile",""), MenModel.LKEY);
        tel.setText(settingphone(mobile));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //处理微信支付成功的回调

        if(wxpay){
            //setWebReturn("weChat");
            wxpay=false;
        }
    }


    @butterknife.OnClick({R.id.ok_pay, R.id.button2, R.id.passmobile,R.id.back_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok_pay:
                if(py_pay_zfb.isChecked()){
                    setAlitype("Alipay");

                }
                if(py_wx.isChecked()){
                    setAlitype("WXpay");
                }
                break;
            case R.id.back_left:
            case R.id.button2:
                finish();
                break;
            case R.id.passmobile:
                Intent savetel=new Intent(this,UnbundlingActivity.class);
                startActivity(savetel);
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息


                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {

                        try {
                            System.out.println("--->resultInfo:"+resultInfo);
                            JSONObject jsonObject=new JSONObject(resultInfo);
                            JSONObject alipay_trade_app_pay_response=jsonObject.getJSONObject("alipay_trade_app_pay_response");
                            out_trade_no=alipay_trade_app_pay_response.getString("out_trade_no");
                            //System.out.println("out_trade_no:"+out_trade_no);

                            //setWebReturn("alipay");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(GuaidouPayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(GuaidouPayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            //Toast.makeText(GuaidouPayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };



    //支付成功的回调
    private void setWebReturn(String aw){

        final String path = "/author/Nexts/webReturn";
        final RequestParams params = new RequestParams();
        params.put("member_id",member_id);
        params.put("setting",setting);
        params.put("type",1);
        params.put("order",out_trade_no);
        params.put("aw",aw);
        //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    JSONObject jsonObject =new JSONObject(result);
                    int errcode = jsonObject.getInt("errcode");
                    if(errcode!=0){
                        String errcodestr = jsonObject.getString("errmsg");
                        Toast.makeText(GuaidouPayActivity.this,errcodestr,Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    getTyChaterrmsg(path,params.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    getTyChaterrmsg(path,params.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String result=new String(responseBody,"utf-8");
                    System.out.println("result:"+result);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                getTyChaterrmsg(path,params.toString());
            }
        });
    }

    private void setAlitype(final String funal){

        final String path = "/author/Nexts/alitype";

        final RequestParams params = new RequestParams();
        params.put("funal",funal);
        params.put("type",1);
        params.put("setting_id",setting);
        params.put("member_id",member_id);

        //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    JSONObject myJsonObject = new JSONObject(result);

                    //微信支付
                    if(funal=="WXpay"){
                        setWXpay(myJsonObject,path,params.toString());
                    }
                    //支付宝支付
                    if(funal=="Alipay"){
                        setAlipay(myJsonObject,path,params.toString());
                    }

                } catch (UnsupportedEncodingException e) {
                    getTyChaterrmsg(path,params.toString());
                    e.printStackTrace();
                } catch (JSONException e) {
                    getTyChaterrmsg(path,params.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getTyChaterrmsg(path,params.toString());
            }
        });
    }

    //微信支付
    private void setWXpay(JSONObject myJsonObject,String path,String params){

        try {
            JSONObject dataListObject = myJsonObject.getJSONObject("dataList");
            IWXAPI api = WXAPIFactory.createWXAPI(GuaidouPayActivity.this, WXEntryActivity.WEIXIN_APP_ID);
            PayReq request = new PayReq();
            out_trade_no=dataListObject.getString("out_trade_no");
            request.appId = dataListObject.getString("appid");
            request.partnerId = dataListObject.getString("partnerid");
            request.prepayId = dataListObject.getString("prepayid");
            request.packageValue = dataListObject.getString("package"); //packageValue
            request.nonceStr = dataListObject.getString("noncestr");
            request.timeStamp = dataListObject.getString("timestamp");
            request.sign =dataListObject.getString("sign");
            api.sendReq(request);
        } catch (JSONException e) {
            e.printStackTrace();
            getTyChaterrmsg(path,params);
        }
    }

    //支付宝支付
    private void setAlipay(JSONObject myJsonObject,String path,String params){
        try {
            final String orderinfo=myJsonObject.getString("dataList");
            System.out.println("orderinfo:"+orderinfo);
            Runnable payRunnable = new Runnable() {

                @Override
                public void run() {
                    //新建任务
                    PayTask alipay = new PayTask(GuaidouPayActivity.this);
                    Map<String, String> resultMAP =alipay.payV2(orderinfo, true);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = resultMAP;
                    mHandler.sendMessage(msg);
                }
            };
            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        } catch (JSONException e) {
            e.printStackTrace();
            getTyChaterrmsg(path,params);
        }
    }



    private void changeRadio(){
        py_pay_zfb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(py_pay_zfb.isChecked()){
                    py_wx.setChecked(false);
                    Drawable right = getResources().getDrawable(R.drawable.radio_checked);
                    py_pay_zfb.setCompoundDrawablesWithIntrinsicBounds(null, null , right, null);

                    Drawable right1 = getResources().getDrawable(R.drawable.radio_unchecked);
                    py_wx.setCompoundDrawablesWithIntrinsicBounds(null, null , right1, null);

                }
            }
        });

        py_wx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(py_wx.isChecked()){
                    py_pay_zfb.setChecked(false);
                    Drawable right = getResources().getDrawable(R.drawable.radio_unchecked);
                    py_pay_zfb.setCompoundDrawablesWithIntrinsicBounds(null, null , right, null);

                    Drawable right1 = getResources().getDrawable(R.drawable.radio_checked);
                    py_wx.setCompoundDrawablesWithIntrinsicBounds(null, null , right1, null);
                }
            }
        });
    }

}
