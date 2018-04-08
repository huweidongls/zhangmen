package com.zan99.guaizhangmen.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zan99.guaizhangmen.Activity.Men.GuaidouPayActivity;
import com.zan99.guaizhangmen.Activity.Men.PayActivity;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.Util.L;

/**
 * Created by Administrator on 2017/5/11.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WXEntryActivity.WEIXIN_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        //...
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Intent intent = new Intent("com.zan99.guaizhangmen.Activity.Men.GuaidouPayActivity");
        intent.putExtra("type", "wx");
        if(baseResp.errCode==0){
            BaseActivity.wxpay=true;
            intent.putExtra("result","ok");
            SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
            editor.putString("client", L.encrypt("1",MenModel.LKEY));
            editor.commit();
        }else{
            intent.putExtra("result","false");
        }
        sendBroadcast(intent);
        finish();
    }
}