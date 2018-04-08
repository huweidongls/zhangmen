package com.zan99.guaizhangmen.Activity.Men;

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

/**
 *
 * Created by Administrator on 2017/11/29.
 */

public class UnbundlingActivity extends BaseActivity {


    @BindView(R.id.button2)
    LinearLayout button2;
    @BindView(R.id.getcode)
    Button getcode;
    @BindView(R.id.old_mobile)
    EditText old_mobile;
    @BindView(R.id.new_mobile)
    EditText new_mobile;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.back_left) Button back_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbund);
        ButterKnife.bind(this);

    }

    @butterknife.OnClick({R.id.button2, R.id.getcode, R.id.button,R.id.back_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_left:
            case R.id.button2:
                finish();
                break;
            case R.id.getcode:
                if(TextUtils.isEmpty(old_mobile.getText().toString())){
                    Toast.makeText(this,"老手机号不能为空", Toast.LENGTH_SHORT).show();
                    break;
                }

                if(TextUtils.isEmpty(new_mobile.getText().toString())){
                    Toast.makeText(this,"新手机号不能为空", Toast.LENGTH_SHORT).show();
                    break;
                }

                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(getcode, 60000, 1000);
                mCountDownTimerUtils.start();
                String mobileString=new_mobile.getText().toString();
                getHttpCode(mobileString);
                break;
            case R.id.button:
                SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
                String member_id= MenModel.member_id;
                String oldmobile=old_mobile.getText().toString();
                String newmobile=new_mobile.getText().toString();
                String codeTEXT=code.getText().toString();
                setReplacethephone(member_id,oldmobile,newmobile,codeTEXT);
                break;
        }
    }


    private void setReplacethephone(String member_id,String oldmobile,final String newmobile,String code){
        final String path = "/member.php/Interfaces/Replacethephone";
        final RequestParams params = new RequestParams();
        params.put("member_id",member_id);
        params.put("oldagemobile",oldmobile);
        params.put("newmobile",newmobile);
        params.put("code",code);
        //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    System.out.println("==========>result:"+result);
                    JSONObject jsonObject=new JSONObject(result);
                    int code=jsonObject.getInt("code");

                    if(code!=0){
                        String errmsg=jsonObject.getString("errmsg");
                        Toast.makeText(UnbundlingActivity.this,errmsg,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(UnbundlingActivity.this,"手机号更换成功！",Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
                        editor.putString("mobile", L.encrypt(newmobile, MenModel.LKEY));
                        editor.commit();
                        finish();
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
                getTyChaterrmsg(path,params.toString());
            }
        });
    }


    private void getHttpCode(String mobileString){

        final String path = "/index.php/admin/Interfaces/SendVerfiyCode";

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
                        String err=myJsonObject.getString("err");
                        Toast.makeText(UnbundlingActivity.this,err,Toast.LENGTH_SHORT).show();
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
                getTyChaterrmsg(path,params.toString());
            }
        });
    }









}
