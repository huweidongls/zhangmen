package com.zan99.guaizhangmen.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Fragment.MenFragment;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.ok)
    Button ok;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.register)
    TextView register;
    @BindView(R.id.forgetpwd)
    TextView forgetpwd;
    @BindView(R.id.button2)
    LinearLayout button2;
    @BindView(R.id.back_left)
    Button back_left;


    private final static String STUTAS="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
        String mobile=pref.getString("mobile","");
        if(mobile!=""){
            Intent i=new Intent(LoginActivity.this,ConfirmPasswordActivity.class);
            i.putExtra("mobile",mobile);
            startActivity(i);
        }

    }

    @butterknife.OnClick({R.id.ok,R.id.mobile,R.id.password,R.id.register,R.id.forgetpwd,R.id.back_left,R.id.button2})
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.forgetpwd:
                Intent intent1 = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent1);
                break;
            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.ok:
                loginentry();
                break;
            case R.id.button2:
            case R.id.back_left:
                finish();
                break;
        }
    }



    private void loginentry(){
        String modileString=mobile.getText().toString();
        String passwordString=password.getText().toString();
        if("".equals(modileString)){
            Toast.makeText(this,"请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(passwordString)){
            Toast.makeText(this,"请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        loadDate(modileString,passwordString);

    }

    private void loadDate(String modileString,String passwordString){
        final String path ="/member.php/Interfaces/login";
        final RequestParams params = new RequestParams();
        params.put("mobile", modileString);
        params.put("password", passwordString);

        //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                //判断状态码
                if (statusCode == 200) {
                    //获取结果
                    initview(responseBody);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LoginActivity.this,"网络错误，请稍后重试", Toast.LENGTH_LONG).show();
                BaseActivity.getTyChaterrmsg(path,params.toString());
            }
        });
    }

    private void initview(byte[] responseBody){
        try {
            String result = new String(responseBody, "utf-8");
            JSONObject myJsonObject = new JSONObject(result);
            String status=myJsonObject.getString("status");
            if(STUTAS.equals(status)){
                String member=myJsonObject.getString("member");
                JSONObject membernObject = new JSONObject(member);
                savemember(membernObject);
            }else{
                String err=myJsonObject.getString("err");
                Toast.makeText(LoginActivity.this,err, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void savemember(JSONObject membernObject){
        try {
            String member_id = membernObject.getString("member_id");
            MenModel.member_id = member_id;
            String head_img=membernObject.getString("head_img");
            String mobile=membernObject.getString("mobile");
            String nick_name=membernObject.getString("nick_name");
            String fee=membernObject.getString("fee");
            String client=membernObject.getString("client");
            String banned=membernObject.getString("banned");
            String ostime=membernObject.getString("ostime");
            String parentid = membernObject.getString("parent_id");
            String uname = membernObject.getString("incode");
            SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
            editor.putString("member_id", L.encrypt(member_id,MenModel.LKEY));
            editor.putString("head_img", L.encrypt(head_img,MenModel.LKEY));
            editor.putString("mobile", L.encrypt(mobile,MenModel.LKEY));
            editor.putString("nick_name", L.encrypt(nick_name,MenModel.LKEY));
            editor.putString("fee", L.encrypt(fee,MenModel.LKEY));
            editor.putString("client", L.encrypt(client,MenModel.LKEY));
            editor.putString("banned", L.encrypt(banned,MenModel.LKEY));
            editor.putString("ostime", L.encrypt(ostime,MenModel.LKEY));
            editor.putString("parentid", L.encrypt(parentid, MenModel.LKEY));
            editor.putString("uname", L.encrypt(uname, MenModel.LKEY));
            editor.commit();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }








}
