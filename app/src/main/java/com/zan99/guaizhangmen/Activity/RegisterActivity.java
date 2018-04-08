package com.zan99.guaizhangmen.Activity;


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
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CountDownTimerUtils;
import com.zan99.guaizhangmen.Util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;


public class RegisterActivity extends BaseActivity {


    @BindView(R.id.getcode)
    Button getcode;
    @BindView(R.id.back_left)
    Button back_left;
    @BindView(R.id.button)
    Button button;

    @BindView(R.id.uname)
    EditText uname;

    @BindView(R.id.rpwd)
    EditText rpwd;
    @BindView(R.id.pwd)
    EditText pwd;
    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.mobile)
    EditText mobile;

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.button2)
    LinearLayout button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @butterknife.OnClick({R.id.getcode,R.id.button2,R.id.button,R.id.back_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getcode:
                String mobileString=mobile.getText().toString();
                if(TextUtils.isEmpty(mobileString)){
                    Toast.makeText(this,"手机号不能为空", Toast.LENGTH_SHORT).show();
                    break;
                }

                if(!isMobile(mobileString)){
                    Toast.makeText(this,"请填写正确格式手机号", Toast.LENGTH_SHORT).show();
                    break;
                }

                getHttpCode(mobileString);
                break;
            case R.id.back_left:
            case R.id.button2:
                finish();
                break;
            case R.id.button:
                setCompareCode();

                break;
        }
    }



    private void getHttpCode(String mobileString){
        CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(getcode, 60000, 1000);
        mCountDownTimerUtils.start();
        final String path = "member.php/Interfaces/SendVerfiyCode";

        final RequestParams params = new RequestParams();

        params.put("mobile",mobileString);

        //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result;
                try {
                    result = new String(responseBody, "utf-8");
                    JSONObject myJsonObject = new JSONObject(result);
                    int code=myJsonObject.getInt("code");
                    if(code!=0){
                        String errmsg=myJsonObject.getString("err");
                        Toast.makeText(RegisterActivity.this,errmsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg(path,params.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    BaseActivity.getTyChaterrmsg(path,params.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg(path,params.toString());
            }
        });
    }




    //注册
    private void setCompareCode(){
        String unmaeText= uname.getText().toString();
        String rpwdText=rpwd.getText().toString();
        String pwdText=pwd.getText().toString();
        String codeText=code.getText().toString();
        String mobileText=mobile.getText().toString();
        String nameText=name.getText().toString();

        if(TextUtils.isEmpty(unmaeText)){
            Toast.makeText(this,"请填写企业名称", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(pwdText)){
            Toast.makeText(this,"请填写密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(pwdText==rpwdText){
            Toast.makeText(this,"两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }


        if(TextUtils.isEmpty(codeText)){
            Toast.makeText(this,"请填写验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mobileText)){
            Toast.makeText(this,"请填写手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(nameText)){
            Toast.makeText(this,"请填写姓名", Toast.LENGTH_SHORT).show();
            return;
        }


        String path = "member.php/Interfaces/CompareCode";
        RequestParams params = new RequestParams();
        params.put("code",codeText);
        params.put("mobile",mobileText);
        params.put("name",nameText);
        params.put("password",pwdText);
        params.put("uname",unmaeText);
        initData(path,params);
    }



    /**
     * 手机号验证
     *
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][0-9][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    private void initData(final String path,final RequestParams params){
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    JSONObject myJsonObject = new JSONObject(result);
                    initview(myJsonObject);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                BaseActivity.getTyChaterrmsg(path,params.toString());
            }
        });
    }



    private void initview(JSONObject myJsonObject){
        try {
            int code=myJsonObject.getInt("code");

            if(code!=0){
                String errmsg=myJsonObject.getString("errmsg");
                Toast.makeText(RegisterActivity.this,errmsg, Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(RegisterActivity.this,"注册成功！", Toast.LENGTH_SHORT).show();
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("提示")
                        .setContentText("注册成功！").setConfirmText("马上登录")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                finish();
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}


