package com.zan99.guaizhangmen.Util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Administrator on 2017/11/27.
 */

public class HttpUtil {
    public static String BASE_URL = "http://www.guaizhangmen.com/";
//    public static String BASE_URL = "http://192.168.0.223:8089/";
    private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象

    static {
        client.setTimeout(11000); // 设置链接超时，如果不设置，默认为10s
    }

    public static AsyncHttpClient getClient() {
        return client;
    }

    public static void get(String urlString, AsyncHttpResponseHandler res) // 用一个完整url获取一个string对象
    {
        client.get(BASE_URL + urlString, res);
    }

    public static void get(String urlString, RequestParams params,
                           AsyncHttpResponseHandler res) // url里面带参数
    {
        client.get(BASE_URL + urlString, params, res);
    }

    public static void get(String urlString, JsonHttpResponseHandler res) // 不带参数，获取json对象或者数组
    {
        client.get(BASE_URL + urlString, res);
    }

    public static void get(String urlString, RequestParams params,
                           JsonHttpResponseHandler res) // 带参数，获取json对象或者数组
    {
        client.get(BASE_URL + urlString, params, res);
    }

    public static void post (String urlString , RequestParams params, AsyncHttpResponseHandler res){
        client.post(BASE_URL + urlString, params, res);
    }

    public static void get(String uString, BinaryHttpResponseHandler bHandler) // 下载数据使用，会返回byte数据
    {
        client.get(BASE_URL + uString, bHandler);
    }

}