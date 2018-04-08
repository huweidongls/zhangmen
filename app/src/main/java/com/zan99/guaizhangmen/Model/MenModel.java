package com.zan99.guaizhangmen.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zan99.guaizhangmen.Util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2017/9/27.
 */

public class MenModel {
    public static String member_id = "0";
    private boolean isAdPlayed = false;
    public final static String FILENAME="lock";
    public final static int LKEY=123;
    private static String PAY_TYPE = "";    // 支付类型   购买会员  member   充值积分   guaidou

    private Map<String, String> adinfo = new HashMap<String,String>();
    public void MainModel(){

    }

    public static void setPayType(String type){
        PAY_TYPE = type;
    }

    public static String getPayType(){
        return PAY_TYPE;
    }

    public void setAdPlayed(Boolean bool){
        isAdPlayed = bool;
    }
    public boolean showAdPlayed(){
        return isAdPlayed;
    }
    public void getAd(){
        HttpUtil.get("http://192.168.0.43/getAd.php",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if(statusCode != 200){
                    return;
                }
                try {
                    String res = response.getString("res");
                    if(res.equals("1")){
                        adinfo.put("adpic",response.getString("adpic"));
                        adinfo.put("adtime",response.getString("adtime"));
                        adinfo.put("adid",response.getString("adid"));
                        setAdPlayed(true);
                    }

                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ShowAdPic(final ImageView imageView){
        if(!adinfo.get("adpic").equals("")){
            HttpUtil.get(adinfo.get("adpic"), new BinaryHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                    imageView.setImageBitmap(bmp);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {

                }
            });
        }
    }
}
