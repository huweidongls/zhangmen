package com.zan99.guaizhangmen.Activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CountDownTimerUtils;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ForgotPasswordActivity extends BaseActivity {

    @BindView(R.id.getcode)
    Button getcode;
    @BindView(R.id.back_left)
    Button back_left;
    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.button2)
    LinearLayout button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpwd);
        ButterKnife.bind(this);
        SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
        String mobiles= L.decrypt(pref.getString("mobile",""), MenModel.LKEY);
        mobile.setHint(settingphone(mobiles));
    }

    @butterknife.OnClick({R.id.getcode,R.id.button2,R.id.button})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getcode:
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(getcode, 60000, 1000);
                mCountDownTimerUtils.start();
                getHttpCode();
                break;
            case R.id.back_left:
            case R.id.button2:
                finish();
                break;
            case R.id.button:
                setCompareverify();
                break;

        }
    }



    //发送验证码
    private void getHttpCode(){

        String mobileString=mobile.getText().toString();
        final String path ="member.php/Interfaces/SendVerfiyCode";
        final RequestParams params = new RequestParams();
        params.put("mobile",mobileString);

        //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                 initview(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("失败："+statusCode);
                getTyChaterrmsg(path,params.toString());
            }
        });
    }

    private void initview(byte[] responseBody){
        try {
            String result = new String(responseBody, "utf-8");
            JSONObject myJsonObject = new JSONObject(result);
            int code = myJsonObject.getInt("code");
            if(code!=0){
                String err=myJsonObject.getString("err");
                Toast.makeText(ForgotPasswordActivity.this,err, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    //验证
    private void setCompareverify(){

        String codeText=code.getText().toString();
        final String mobileText=mobile.getText().toString();


        if(TextUtils.isEmpty(codeText)){
            Toast.makeText(this,"请填写验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mobileText)){
            Toast.makeText(this,"请填写手机号", Toast.LENGTH_SHORT).show();
            return;
        }


        final String path ="member.php//Interfaces/RetrievePassword";

        final RequestParams params = new RequestParams();
        params.put("mobile",mobileText);
        params.put("code",codeText);


        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                initRetrieveview(responseBody,mobileText);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg(path,params.toString());
            }
        });

    }



    private void initRetrieveview(byte[] responseBody,String mobileText){
        try {
            String result = new String(responseBody, "utf-8");
            JSONObject myJsonObject = new JSONObject(result);
            int code=myJsonObject.getInt("code");
            if(code!=0){
                String errmsg=myJsonObject.getString("errmsg");
                Toast.makeText(ForgotPasswordActivity.this,errmsg, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ForgotPasswordActivity.this,"验证成功！", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
                editor.putString("mobile", mobileText);
                editor.commit();
                finish();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}


