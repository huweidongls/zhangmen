package com.zan99.guaizhangmen.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by 99zan on 2018/1/22.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d("TAG", "[MyReceiver] 用户点击打开了通知");
//            //打开自定义的Activity
//            Intent i = new Intent(context, MainActivity.class);
////            i.putExtras(bundle);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
        }
    }
}
