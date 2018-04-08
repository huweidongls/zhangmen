package com.zan99.guaizhangmen.Util;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2017/11/27.
 */

public class CustomerUtil {
    public static void showSoftInputFromWindow(Activity activity , EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }
    public static void hideSoftInputFromWindow(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public static void changeSaveOrZan(String bookid, String type, String action,String uid){
        String url = "author.php/Nexts/";
        if(type.equals("zan")){
            if(action.equals("+")){
                url += "giveThumbsUp";
            }else{
                url += "BackgiveThumbsUp";
            }
        }else{
            if(action.equals("+")){
                url += "CollectionUp";
            }else{
                url += "BackCollectionUp";
            }
        }
        RequestParams param = new RequestParams();
        param.add("books_id", bookid);
        param.add("member_id", uid);

        HttpUtil.post(url, param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }



    public static void changeLTZan(String give_id,String action,String uid){
        String url = "admin.php/Systeminterface/";

        if(action.equals("+")){
            url += "give_like";
        }else{
            url += "cancel_give_like";
        }

        RequestParams param = new RequestParams();
        param.add("give_id", give_id);
        param.add("id", uid);
        HttpUtil.post(url, param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result=new String(responseBody,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }







}
