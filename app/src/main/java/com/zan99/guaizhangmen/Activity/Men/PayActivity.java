package com.zan99.guaizhangmen.Activity.Men;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class PayActivity extends BaseActivity {

    @BindView(R.id.py_pay_zfb)  RadioButton py_pay_zfb;
    @BindView(R.id.py_wx)  RadioButton py_wx;
    @BindView(R.id.py_jhm)  RadioButton py_jhm;
    @BindView(R.id.et_jhm)  EditText et_jhm;
    @BindView(R.id.ok_pay)  Button ok_pay;
    @BindView(R.id.money)  TextView money;
    @BindView(R.id.name)  TextView name;
    @BindView(R.id.equity)  LinearLayout equityll;
    @BindView(R.id.button2)  LinearLayout button2;
    @BindView(R.id.back_left) Button back_left;
    @BindView(R.id.editText) TextView editText;
    @BindView(R.id.sub) TextView sub;


    private static String money_text;
    private static final int SDK_PAY_FLAG = 1;
    private String member_id;
    private SweetAlertDialog pDialog;
    private String out_trade_no;
    private String type;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        changeRadio();
    }


    @Override
    protected void onStart() {
        super.onStart();
        pDialog=loadingpDialog(this);
        member_id= MenModel.member_id;
        initPay(member_id);
        type=getIntent().getStringExtra("type");
        if(type.equals("myhuiyuan")){
            SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
            String ostime= L.decrypt(pref.getString("ostime",""), MenModel.LKEY);
            editText.setText("我的会员");
            ok_pay.setText("续费");

        }
    }

    private void initPay(String member_id){


//        pDialog.show();
        final String path = "/admin.php/Systeminterface/member_equity";
        final RequestParams params = new RequestParams();
        params.put("id",member_id);

        //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    initData(new JSONObject(result),path,params.toString());
                    pDialog.hide();
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

            }
        });
    }


    private void initData(JSONObject myJsonObject,String path,String params){
        String dataList= null;
        try {
            dataList = myJsonObject.getString("dataList");
            JSONObject jsondataList=new JSONObject(dataList);
            money_text=jsondataList.getString("money");
            String name_text=jsondataList.getString("name");
            String equity_text=jsondataList.getString("equity");
            setEquity(equity_text);
            name.setText(name_text);
            money.setText(money_text+"元");
            if(type.equals("myhuiyuan")){
                sub.setText("会员截止日期"+jsondataList.getString("ostime"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            getTyChaterrmsg(path,params);
        }
    }

    private void setEquity(String equity){
        try {
            JSONArray jsonArray=new JSONArray(equity);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String title=jsonObject.getString("title");
                String content=jsonObject.getString("content");
                //标题
                TextView title_tv = new TextView(this);
                TextPaint tp = title_tv.getPaint();
                tp.setFakeBoldText(true);
                title_tv.setText(title);
                title_tv.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                equityll.addView(title_tv);
                //内容
                TextView content_tv = new TextView(this);
                content_tv.setText(content);
                content_tv.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                equityll.addView (content_tv);
                LinearLayout.LayoutParams layoutParamscontent = (LinearLayout.LayoutParams)content_tv.getLayoutParams();
                layoutParamscontent.setMargins(0,0,0,10);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }




    @butterknife.OnClick({R.id.ok_pay,R.id.button2,R.id.back_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok_pay:
                if(py_pay_zfb.isChecked()){
                    setAlitype("Alipay");
                }
                if(py_wx.isChecked()){
                    setAlitype("WXpay");
                }
                if(py_jhm.isChecked()){
                    Log.e("111", "btnclick");
                    setAlitype(et_jhm.getText().toString());
                }
                break;
            case R.id.back_left:
            case R.id.button2:
                finish();
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
                            JSONObject jsonObject=new JSONObject(resultInfo);
                            JSONObject alipay_trade_app_pay_response=jsonObject.getJSONObject("alipay_trade_app_pay_response");
                            out_trade_no=alipay_trade_app_pay_response.getString("out_trade_no");

                            SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
                            editor.putString("client", L.encrypt("1",MenModel.LKEY));
                            editor.commit();

                            Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            //Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
    private void setWebReturn(int type,String aw){

        final String path = "/author.php/Nexts/webReturn";
        final RequestParams params = new RequestParams();
        params.put("member_id",member_id);
        params.put("setting",money_text);
        params.put("type",type);
        params.put("order",out_trade_no);
        params.put("aw",aw);

        //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getTyChaterrmsg(path,params.toString());
            }
        });
    }




    private void setAlitype(final String funal){

        Log.e("111", "setAlitype");

        final String path = "/author.php/Nexts/alitype";

        final RequestParams params = new RequestParams();
        params.put("funal",funal);
        params.put("type",2);
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
                    }else if(funal=="Alipay"){//支付宝支付
                        setAlipay(myJsonObject,path,params.toString());
                    }else{
                        Log.e("111", "jhmpay"+myJsonObject.getString("errcode"));
                        Toast.makeText(PayActivity.this, myJsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
                    }


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
                Log.e("111", "jhmpay.............onFailure"+funal);
                try {
                    String result = new String(responseBody, "utf-8");
                    Log.e("111",result);
                    Toast.makeText(PayActivity.this, "开通失败，请检查网络！", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                getTyChaterrmsg(path,params.toString());
            }
        });
    }

    //输入激活码激活
    private void setJhmpay() {

    }

    //微信支付
    private void setWXpay(JSONObject myJsonObject,String path,String param){

        try {
            JSONObject dataListObject = myJsonObject.getJSONObject("dataList");
            IWXAPI api = WXAPIFactory.createWXAPI(PayActivity.this, WXEntryActivity.WEIXIN_APP_ID);
            PayReq request = new PayReq();
            System.out.println("appid:"+dataListObject.getString("appid"));
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
            getTyChaterrmsg(path,param);
        }
    }

    //支付宝支付
    private void setAlipay(JSONObject myJsonObject,String path,String param){
        try {
            final String orderinfo=myJsonObject.getString("dataList");
            System.out.println("orderinfo:"+orderinfo);
            Runnable payRunnable = new Runnable() {

                @Override
                public void run() {
                    //新建任务
                    PayTask alipay = new PayTask(PayActivity.this);
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
            getTyChaterrmsg(path,param);
        }
    }



    private void changeRadio(){
        py_pay_zfb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(py_pay_zfb.isChecked()){
                    py_wx.setChecked(false);
                    py_jhm.setChecked(false);
                    Drawable right = getResources().getDrawable(R.drawable.radio_checked);
                    py_pay_zfb.setCompoundDrawablesWithIntrinsicBounds(null, null , right, null);

                    Drawable right1 = getResources().getDrawable(R.drawable.radio_unchecked);
                    py_wx.setCompoundDrawablesWithIntrinsicBounds(null, null , right1, null);
                    py_jhm.setCompoundDrawablesWithIntrinsicBounds(null, null , right1, null);

                    et_jhm.setVisibility(View.GONE);

                }
            }
        });

        py_wx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(py_wx.isChecked()){
                    py_pay_zfb.setChecked(false);
                    py_jhm.setChecked(false);
                    Drawable right = getResources().getDrawable(R.drawable.radio_unchecked);
                    py_pay_zfb.setCompoundDrawablesWithIntrinsicBounds(null, null , right, null);
                    py_jhm.setCompoundDrawablesWithIntrinsicBounds(null, null , right, null);

                    Drawable right1 = getResources().getDrawable(R.drawable.radio_checked);
                    py_wx.setCompoundDrawablesWithIntrinsicBounds(null, null , right1, null);
                    et_jhm.setVisibility(View.GONE);
                }
            }
        });

        py_jhm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(py_jhm.isChecked()){
                    py_pay_zfb.setChecked(false);
                    py_wx.setChecked(false);
                    Drawable right = getResources().getDrawable(R.drawable.radio_unchecked);
                    py_pay_zfb.setCompoundDrawablesWithIntrinsicBounds(null, null , right, null);
                    py_wx.setCompoundDrawablesWithIntrinsicBounds(null, null , right, null);

                    Drawable right1 = getResources().getDrawable(R.drawable.radio_checked);
                    py_jhm.setCompoundDrawablesWithIntrinsicBounds(null, null , right1, null);
                    et_jhm.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}
