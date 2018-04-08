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
import com.zan99.guaizhangmen.Util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2017/11/28.
 */

public class ConfirmPasswordActivity extends BaseActivity {

    @BindView(R.id.newpwd)
    EditText newpwd;
    @BindView(R.id.redenewpwd)
    EditText redenewpwd;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.button2)
    LinearLayout button2;
    @BindView(R.id.back_left) Button back_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmpwd);
        ButterKnife.bind(this);
    }

    @butterknife.OnClick({R.id.button,R.id.button2,R.id.back_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                savepassword();
                break;
            case R.id.back_left:
            case R.id.button2:
                finish();
                break;
        }
    }

    private void savepassword(){
        String newpwdtext=newpwd.getText().toString();
        String redenewpwdtext=redenewpwd.getText().toString();
        if(TextUtils.isEmpty(newpwdtext)){
            Toast.makeText(this,"新密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!newpwdtext.equals(redenewpwdtext)){
            Toast.makeText(this,"两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        loadDate(newpwdtext);
    }


    private void loadDate(String newpwdtext){
        final String path ="member.php/Interfaces/SavePassword";
        final RequestParams params = new RequestParams();
        params.put("mobile",getIntent().getStringExtra("mobile"));
        params.put("password",newpwdtext);


        //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result=null;
                try {
                    result = new String(responseBody, "utf-8");
                    JSONObject myJsonObject = new JSONObject(result);
                    initview(myJsonObject);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    getTyChaterrmsg(path,params.toString());
                }catch (JSONException e) {
                    e.printStackTrace();
                    getTyChaterrmsg(path,params.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getTyChaterrmsg(path,params.toString());
            }
        });
    }


    private void initview(JSONObject myJsonObject){
        try {
            int code=myJsonObject.getInt("code");
            if(code!=0){
                String err=myJsonObject.getString("errmsg"); //errmsg
                Toast.makeText(ConfirmPasswordActivity.this,err, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ConfirmPasswordActivity.this,"修改成功", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
