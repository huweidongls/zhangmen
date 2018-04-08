package com.zan99.guaizhangmen;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.Util.SPUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import io.github.rockerhieu.emojicon.EmojiconEditText;

/**
 * Created by Administrator on 2017/11/27.
 */

public class BaseActivity extends AppCompatActivity {
    public String BASE_URL = "http://192.168.0.227:8080";
    public static Boolean wxpay=false;
    private SPUtil spUtil;







    public static SweetAlertDialog loadingpDialog(Context context){
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#c30318"));
        pDialog.setTitleText("加载中...");
        pDialog.setCancelable(false);
        return pDialog;
    }


    public void setStringData(String key,String value){
        SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
        editor.putString(key, L.encrypt(value,MenModel.LKEY));
        editor.commit();
    }

    public String getStringData(String key){
        SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
        String member_id= L.decrypt(pref.getString(key,""), MenModel.LKEY);
        return member_id;
    }

    /**
     * 手机号用****号隐藏中间数字
     *
     * @param phone
     * @return
     */
    public static String settingphone(String phone) {
        String phone_s = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        return phone_s;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            View view = getCurrentFocus();
            if(isShouldHideInput(view, ev)){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    if(view instanceof io.github.rockerhieu.emojicon.EmojiconEditText){
                        ((EmojiconEditText) view).setHint("");
                        view.setTag(R.id.tag_comment,"0");
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }



    private boolean isShouldHideInput(View v, MotionEvent event) {

        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {

                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                v.clearFocus();//失去焦点
                return true;
            }
        }
        return false;
    }


    public static void getSysteminterface(String url,String errmsg){

        RequestParams param = new RequestParams();
        param.put("member_id", MenModel.member_id);
        param.put("url", URLEncoder.encode(url));
        param.put("errmsg_id", errmsg);
        L.d(param.toString());
        HttpUtil.post("admin.php/Systeminterface/misreport", param, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result=new String(responseBody,"utf-8");
                    System.out.println("======>result:"+result);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public static void getTyChaterrmsg(String url, String params){
        try {
            getSysteminterface(url, URLEncoder.encode(params,"utf-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }

    public static void showemploy(View view,View listView){
        view.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }


}
