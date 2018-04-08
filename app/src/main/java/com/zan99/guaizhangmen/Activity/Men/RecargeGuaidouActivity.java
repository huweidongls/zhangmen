package com.zan99.guaizhangmen.Activity.Men;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zan99.guaizhangmen.Adapter.RecargeGuaidouAdapter;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.Widget.CustomerGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class RecargeGuaidouActivity extends BaseActivity {



    private ArrayList<HashMap<String, String>> recargelist;

    @BindView(R.id.rechargegd)
    CustomerGridView rechargegd;
    @BindView(R.id.balance) TextView balance;
    @BindView(R.id.button2) LinearLayout button2;
    @BindView(R.id.tradelog) Button tradelog;
    private String member_id;
    private SweetAlertDialog pDialog;
    @BindView(R.id.back_left) Button back_left;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_guaidou);
        ButterKnife.bind(this);

        member_id= MenModel.member_id;

        recargelist(member_id);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recargelist(member_id);
    }

    @butterknife.OnClick({R.id.button2, R.id.tradelog,R.id.back_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_left:
            case R.id.button2:
                finish();
                break;
            case R.id.tradelog:
                Intent i=new Intent(this,TradeLogActivity.class);
                i.putExtra("member_id",member_id);
                startActivity(i);
                break;
        }
    }


    private void recargelist(String member_id){
        pDialog=loadingpDialog(this);
        pDialog.show();
        recargelist = new ArrayList<HashMap<String, String>>();

        final String path = "/member.php/Interfaces/ProporTion";
        final RequestParams params = new RequestParams();
        params.put("member_id",member_id);
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    initData(new JSONObject(result));
                    pDialog.hide();
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

    private void initData(JSONObject jsonObject){
        try {
            settingInfo(jsonObject.getString("setting"));
            balance.setText(jsonObject.getString("balance"));
            SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
            editor.putString("fee",  L.encrypt(jsonObject.getString("balance"), MenModel.LKEY));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void settingInfo(String setting){
        try {

            JSONArray setJSONArray=new JSONArray(setting);
            for (int i=0;i<setJSONArray.length();i++){
                HashMap<String, String> map=new HashMap<String, String>();
                JSONObject jsonObject=setJSONArray.getJSONObject(i);
                map.put("setting_id",jsonObject.getString("setting_id"));
                map.put("bean_num",jsonObject.getString("bean_num"));
                map.put("bean_money",jsonObject.getString("bean_money"));
                map.put("deliverNum",jsonObject.getString("deliverNum"));
                recargelist.add(map);
            }
            RecargeGuaidouAdapter adapte = new RecargeGuaidouAdapter(this, recargelist);
            rechargegd.setAdapter(adapte);
            rechargegd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> stringStringHashMap = recargelist.get(position);
                    Intent i = new Intent(RecargeGuaidouActivity.this,GuaidouPayActivity.class);
                    i.putExtra("setting_id",stringStringHashMap.get("setting_id"));
                    i.putExtra("member_id",member_id);
                    i.putExtra("bean_money",stringStringHashMap.get("bean_money"));
                    i.putExtra("bean_num",stringStringHashMap.get("bean_num"));
                    startActivity(i);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
