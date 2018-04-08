package com.zan99.guaizhangmen.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.Activity.LoginActivity;
import com.zan99.guaizhangmen.Activity.Men.AboutusActivity;
import com.zan99.guaizhangmen.Activity.Men.CollectActivity;
import com.zan99.guaizhangmen.Activity.Men.CommentActivity;
import com.zan99.guaizhangmen.Activity.Men.MyUpdataActivity;
import com.zan99.guaizhangmen.Activity.Men.MyUpdataBooksActivity;
import com.zan99.guaizhangmen.Activity.Men.MyWalletActivity;
import com.zan99.guaizhangmen.Activity.Men.PayActivity;
import com.zan99.guaizhangmen.Activity.Men.PersonalSetActivity;
import com.zan99.guaizhangmen.Activity.Men.RecargeGuaidouActivity;
import com.zan99.guaizhangmen.Activity.Men.WinGiftActivity;
import com.zan99.guaizhangmen.Activity.Men.WinGuaidouActivity;
import com.zan99.guaizhangmen.Activity.MenuActivity;
import com.zan99.guaizhangmen.Model.BaseModel;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CircleTransform;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

/**
 * Created by 99zan on 2018/1/3.
 */

public class MenFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.imagetouxiang)
    ImageView imageView;
    @BindView(R.id.house)
    LinearLayout house;
    @BindView(R.id.btnLogout)
    Button btnLogout;

    private static String member_id;
    public static int REFRESH = 1;
    @BindView(R.id.mobile)
    TextView mobileText;
    @BindView(R.id.nick_name)
    TextView nick_nameText;
    @BindView(R.id.pay)
    TextView pay;
    @BindView(R.id.mycomment)
    LinearLayout mycomment;
    @BindView(R.id.wingift)
    LinearLayout wingift;
    @BindView(R.id.winguaidou)
    LinearLayout winguaidou;
    @BindView(R.id.rechargeguaidou)
    LinearLayout rechargeguaidou;
    @BindView(R.id.login)
    RelativeLayout login;
    @BindView(R.id.aboutus)
    LinearLayout aboutus;
    @BindView(R.id.nologin)
    TextView nologin;
    @BindView(R.id.myhuiyuan)
    LinearLayout myhuiyuan;
    @BindView(R.id.guaidou)
    LinearLayout guaidou;
    @BindView(R.id.loginbtn1)
    LinearLayout loginbtn1;
    @BindView(R.id.iconvip)
    ImageView iconvip;
    @BindView(R.id.fee)
    TextView feeview;
    @BindView(R.id.my_updata)
    LinearLayout myUpdata;
    @BindView(R.id.mywallet)
    LinearLayout myWallet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_men, null);
        ButterKnife.bind(this, view);

        //头像显示部分
        judgeLogin();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        judgeLogin();
    }

    private void loadData() {

        final RequestParams params = new RequestParams();
        params.put("member_id", MenModel.member_id);
        params.put("id", MenuActivity.uploadid);
        L.d(params.toString());
        HttpUtil.post("/admin.php/Systeminterface/share_code", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    L.d(result);
                    JSONObject jsonObject = new JSONObject(result);
                    String dataList = jsonObject.getString("dataList");
                    String errcode = jsonObject.getString("errcode");

                    JSONObject jsonObj = new JSONObject(dataList);
                    String imgsurl = jsonObj.getString("imgsurl");

                    String id = jsonObj.getString("id");
                    SharedPreferences.Editor editor = getContext().getSharedPreferences(MenModel.FILENAME, getContext().MODE_PRIVATE).edit();
                    editor.putString("imgsurl", L.encrypt(imgsurl, MenModel.LKEY));
                    editor.putString("uploadid", L.encrypt(id, MenModel.LKEY));
                    editor.commit();
//                    setStringData("imgsurl",imgsurl);
//                    setStringData("uploadid",id);
                    MenuActivity.uploadid = id;


                } catch (UnsupportedEncodingException e) {
                    getTyChaterrmsg("/admin.php/Systeminterface/share_code", params.toString());
                    e.printStackTrace();
                } catch (JSONException e) {
                    getTyChaterrmsg("/admin.php/Systeminterface/share_code", params.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    //判断是否登录
    private void judgeLogin() {

        SharedPreferences pref = getContext().getSharedPreferences(MenModel.FILENAME, getContext().MODE_PRIVATE);
        member_id = L.decrypt(pref.getString("member_id", ""), MenModel.LKEY);
        String head_img = L.decrypt(pref.getString("head_img", ""), MenModel.LKEY);
        String mobile = L.decrypt(pref.getString("mobile", ""), MenModel.LKEY);
        String nick_name = L.decrypt(pref.getString("nick_name", ""), MenModel.LKEY);
        String fee = L.decrypt(pref.getString("fee", ""), MenModel.LKEY);
        String client = L.decrypt(pref.getString("client", ""), MenModel.LKEY);

        if (!TextUtils.isEmpty(member_id)) {//已登录
            feeview.setText(fee);
            guaidou.setVisibility(View.VISIBLE);
            nologin.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            mobileText.setVisibility(View.VISIBLE);
            loginbtn1.setVisibility(View.VISIBLE);
            mobileText.setText(settingphone(mobile));

            nick_nameText.setText(nick_name);
            if (!TextUtils.isEmpty(head_img)) {
                head_img = "http://" + head_img + "?imageView2/2/w/300/h/800/interlace/0/q/100";
                System.out.println("head_img:" + head_img);
                Picasso.with(getContext()).load(head_img).transform(new CircleTransform()).into(imageView);
            }
            if (client.equals("1")) {
                iconvip.setImageResource(R.drawable.vip_03);
                pay.setVisibility(View.GONE);
                myhuiyuan.setVisibility(View.VISIBLE);
            } else if (client.equals("2")) {
//                ViewGroup.LayoutParams lp = iconvip.getLayoutParams();
//                lp.width = 30;
//                lp.height = 20;
//                iconvip.setLayoutParams(lp);
                iconvip.setImageResource(R.drawable.vip_03_h);
                loginbtn1.setVisibility(View.VISIBLE);
                pay.setVisibility(View.VISIBLE);
                myhuiyuan.setVisibility(View.GONE);
            }
            loadData();
        } else {//未登录
            guaidou.setVisibility(View.GONE);
            nologin.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            mobileText.setVisibility(View.GONE);
            loginbtn1.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.iconheader_03);
            MenModel.member_id = "0";
        }

        if (BaseModel.showMenActivity && (!TextUtils.isEmpty(MenFragment.member_id)) && (!MenFragment.member_id.equals(""))) {
            wingift.setVisibility(View.VISIBLE);
        } else {
            wingift.setVisibility(View.GONE);
        }

    }


    @butterknife.OnClick({R.id.mywallet, R.id.my_updata, R.id.btnLogout, R.id.imagetouxiang, R.id.myhuiyuan, R.id.pay, R.id.mycomment, R.id.wingift, R.id.winguaidou, R.id.rechargeguaidou, R.id.login, R.id.mobile, R.id.aboutus, R.id.nologin, R.id.house})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutus:
                Intent abi = new Intent(getContext(), AboutusActivity.class);
                startActivity(abi);
                break;
            case R.id.mobile:
            case R.id.login:
            case R.id.imagetouxiang:
                if (!TextUtils.isEmpty(member_id)) {
                    Intent intent = new Intent(getContext(), PersonalSetActivity.class);
                    startActivity(intent);
                    break;
                }
            case R.id.rechargeguaidou:
                if (!TextUtils.isEmpty(member_id)) {
                    Intent rggd = new Intent(getContext(), RecargeGuaidouActivity.class);
                    startActivity(rggd);
                    break;
                }
            case R.id.house:
                if (!TextUtils.isEmpty(member_id)) {
                    Intent collectintent = new Intent(getContext(), CollectActivity.class);
                    startActivity(collectintent);
                    break;
                }
            case R.id.mycomment:
                if (!TextUtils.isEmpty(member_id)) {
                    Intent myc = new Intent(getContext(), CommentActivity.class);
                    startActivity(myc);
                    break;
                }
            case R.id.mywallet:
                if (!TextUtils.isEmpty(member_id)) {
                    Intent intent2 = new Intent();
                    intent2.setClass(getContext(), MyWalletActivity.class);
                    startActivity(intent2);
                    break;
                }
            case R.id.my_updata:
                if (!TextUtils.isEmpty(member_id)) {
                    Intent intent1 = new Intent();
                    intent1.setClass(getContext(), MyUpdataBooksActivity.class);
                    startActivity(intent1);
                    break;
                }
            case R.id.wingift:
                if (!TextUtils.isEmpty(member_id)) {
                    Intent wgift = new Intent(getContext(), WinGiftActivity.class);
                    startActivity(wgift);
                    break;
                }
            case R.id.winguaidou:
                if (!TextUtils.isEmpty(member_id)) {
                    Intent guaidou = new Intent(getContext(), WinGuaidouActivity.class);
                    startActivity(guaidou);
                    break;
                }
            case R.id.myhuiyuan:
                if (!TextUtils.isEmpty(member_id)) {
                    Intent collectpay = new Intent(getContext(), PayActivity.class);
                    collectpay.putExtra("type", "myhuiyuan");
                    startActivity(collectpay);
                    break;
                }
            case R.id.pay:
                if (!TextUtils.isEmpty(member_id)) {
                    Intent collectpay = new Intent(getContext(), PayActivity.class);
                    collectpay.putExtra("type", "huiyuan");
                    startActivity(collectpay);
                    break;
                }
            case R.id.btnLogout:
                if (!TextUtils.isEmpty(member_id)) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("提示")
                            .setContentText("是否退出？")
                            .setConfirmText("退出")
                            .setCancelText("取消")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    SharedPreferences.Editor editor = getContext().getSharedPreferences(MenModel.FILENAME, getContext().MODE_PRIVATE).edit();
                                    MenModel.member_id = "0";
                                    editor.remove("member_id");
                                    editor.clear();
                                    editor.commit();
                                    Toast.makeText(getContext(), "退出成功", Toast.LENGTH_LONG).show();
                                    judgeLogin();

                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            }).show();

                    break;
                }
            case R.id.nologin:
            default:
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                break;


        }
    }

    public void getSysteminterface(String url, String errmsg) {

        RequestParams param = new RequestParams();
        param.put("member_id", MenModel.member_id);
        param.put("url", URLEncoder.encode(url));
        param.put("errmsg_id", errmsg);
        L.d(param.toString());
        HttpUtil.post("admin.php/Systeminterface/misreport", param, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    System.out.println("======>result:" + result);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void getTyChaterrmsg(String url, String params) {
        try {
            getSysteminterface(url, URLEncoder.encode(params, "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 手机号用****号隐藏中间数字
     *
     * @param phone
     * @return
     */
    public String settingphone(String phone) {
        String phone_s = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        return phone_s;
    }

}
