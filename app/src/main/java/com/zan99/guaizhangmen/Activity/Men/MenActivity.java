package com.zan99.guaizhangmen.Activity.Men;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.zan99.guaizhangmen.Activity.MenuActivity;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.BaseModel;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CircleTransform;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class MenActivity extends BaseActivity implements View.OnClickListener {

    ImageView imageView;
    LinearLayout house;

    Button btnLogout;
    private static  String member_id;
    public static int REFRESH=1;
    @BindView(R.id.mobile)   TextView mobileText;
    @BindView(R.id.nick_name) TextView nick_nameText;
    @BindView(R.id.pay) TextView pay;
    @BindView(R.id.mycomment) LinearLayout mycomment;
    @BindView(R.id.wingift) LinearLayout wingift;
    @BindView(R.id.winguaidou) LinearLayout winguaidou;
    @BindView(R.id.rechargeguaidou) LinearLayout rechargeguaidou;
    @BindView(R.id.login) RelativeLayout login;
    @BindView(R.id.aboutus) LinearLayout aboutus;
    @BindView(R.id.nologin) TextView nologin;
    @BindView(R.id.myhuiyuan) LinearLayout myhuiyuan;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_men);

        imageView = (ImageView) findViewById(R.id.imagetouxiang);
        house= (LinearLayout) findViewById(R.id.house);

        btnLogout= (Button) findViewById(R.id.btnLogout);

        imageView.setOnClickListener(this);
        house.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        ButterKnife.bind(this);
        //头像显示部分
        judgeLogin();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        judgeLogin();
    }


    private void loadData(){

        final RequestParams params = new RequestParams();
        params.put("member_id",MenModel.member_id);
        params.put("id", MenuActivity.uploadid);
        L.d(params.toString());
        HttpUtil.post("/admin.php/Systeminterface/share_code",params, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    L.d(result);
                    JSONObject jsonObject=new JSONObject(result);
                    String dataList=jsonObject.getString("dataList");
                    String errcode=jsonObject.getString("errcode");

                    JSONObject jsonObj=new JSONObject(dataList);
                    String imgsurl=jsonObj.getString("imgsurl");

                    String id=jsonObj.getString("id");
                    SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
                    editor.putString("imgsurl", L.encrypt(imgsurl,MenModel.LKEY));
                    editor.putString("uploadid", L.encrypt(id,MenModel.LKEY));
                    editor.commit();
//                    setStringData("imgsurl",imgsurl);
//                    setStringData("uploadid",id);
                    MenuActivity.uploadid=id;



                } catch (UnsupportedEncodingException e) {
                    getTyChaterrmsg("/admin.php/Systeminterface/share_code",params.toString());
                    e.printStackTrace();
                } catch (JSONException e) {
                    getTyChaterrmsg("/admin.php/Systeminterface/share_code",params.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    //判断是否登录
    private void judgeLogin(){

        SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
        member_id= L.decrypt(pref.getString("member_id",""), MenModel.LKEY);
        String head_img= L.decrypt(pref.getString("head_img",""), MenModel.LKEY);
        String mobile= L.decrypt(pref.getString("mobile",""), MenModel.LKEY);
        String nick_name= L.decrypt(pref.getString("nick_name",""), MenModel.LKEY);
        String fee= L.decrypt(pref.getString("fee",""), MenModel.LKEY);
        String client= L.decrypt(pref.getString("client",""), MenModel.LKEY);

        if(!TextUtils.isEmpty(member_id)){//已登录
            TextView feeview= (TextView) findViewById(R.id.fee);
            feeview.setText(fee);
            findViewById(R.id.guaidou).setVisibility(View.VISIBLE);
            findViewById(R.id.nologin).setVisibility(View.GONE);
            findViewById(R.id.login).setVisibility(View.VISIBLE);
            mobileText.setVisibility(View.VISIBLE);
            findViewById(R.id.loginbtn1).setVisibility(View.VISIBLE);
            mobileText.setText(settingphone(mobile));

            nick_nameText.setText(nick_name);
            if(!TextUtils.isEmpty(head_img)){
                head_img="http://"+head_img+"?imageView2/2/w/300/h/800/interlace/0/q/100";
                System.out.println("head_img:"+head_img);
                Picasso.with(this).load(head_img).transform(new CircleTransform()).into(imageView);            }
            ImageView iconvip= (ImageView) findViewById(R.id.iconvip);
            if(client.equals("1")){
                iconvip.setImageResource(R.drawable.vip_03);
                pay.setVisibility(View.GONE);
                myhuiyuan.setVisibility(View.VISIBLE);
            }else if(client.equals("2")){
//                ViewGroup.LayoutParams lp = iconvip.getLayoutParams();
//                lp.width = 30;
//                lp.height = 20;
//                iconvip.setLayoutParams(lp);
                iconvip.setImageResource(R.drawable.vip_03_h);
                findViewById(R.id.loginbtn1).setVisibility(View.VISIBLE);
                pay.setVisibility(View.VISIBLE);
                myhuiyuan.setVisibility(View.GONE);
            }
            loadData();
        }else{//未登录
            findViewById(R.id.guaidou).setVisibility(View.GONE);
            findViewById(R.id.nologin).setVisibility(View.VISIBLE);
            findViewById(R.id.login).setVisibility(View.GONE);
            mobileText.setVisibility(View.GONE);
            findViewById(R.id.loginbtn1).setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.iconheader_03);
            MenModel.member_id="0";
        }

        if(BaseModel.showMenActivity && (!TextUtils.isEmpty(MenActivity.member_id)) && (!MenActivity.member_id.equals(""))){
            wingift.setVisibility(View.VISIBLE);
        }else{
            wingift.setVisibility(View.GONE);
        }

    }



    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        //bitmap.getHeight()
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());

        final RectF rectF = new RectF(rect);

        //final float roundPx = pixels;
        final float roundPx = bitmap.getWidth()/2;

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }

    @butterknife.OnClick({R.id.myhuiyuan,R.id.pay, R.id.mycomment, R.id.wingift, R.id.winguaidou, R.id.rechargeguaidou, R.id.login, R.id.mobile,R.id.aboutus,R.id.nologin})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.aboutus:
                Intent abi=new Intent(this,AboutusActivity.class);
                startActivity(abi);
                break;
            case R.id.mobile:
            case R.id.login:
            case R.id.imagetouxiang:
                if(!TextUtils.isEmpty(member_id)){
                    Intent intent = new Intent(this, PersonalSetActivity.class);
                    startActivity(intent);
                    break;
                }
            case R.id.rechargeguaidou:
                if(!TextUtils.isEmpty(member_id)) {
                    Intent rggd = new Intent(this, RecargeGuaidouActivity.class);
                    startActivity(rggd);
                    break;
                }
            case R.id.house:
                if(!TextUtils.isEmpty(member_id)) {
                    Intent collectintent = new Intent(this, CollectActivity.class);
                    startActivity(collectintent);
                    break;
                }
            case R.id.mycomment:
                if(!TextUtils.isEmpty(member_id)) {
                    Intent myc = new Intent(this, CommentActivity.class);
                    startActivity(myc);
                    break;
                }
            case R.id.wingift:
                if(!TextUtils.isEmpty(member_id)) {
                    Intent wgift = new Intent(this, WinGiftActivity.class);
                    startActivity(wgift);
                    break;
                }
            case R.id.winguaidou:
                if(!TextUtils.isEmpty(member_id)) {
                    Intent guaidou = new Intent(this, WinGuaidouActivity.class);
                    startActivity(guaidou);
                    break;
                }
            case R.id.myhuiyuan:
                if(!TextUtils.isEmpty(member_id)) {
                    Intent collectpay = new Intent(this, PayActivity.class);
                    collectpay.putExtra("type","myhuiyuan");
                    startActivity(collectpay);
                    break;
                }
            case R.id.pay:
                if(!TextUtils.isEmpty(member_id)) {
                    Intent collectpay = new Intent(this, PayActivity.class);
                    collectpay.putExtra("type","huiyuan");
                    startActivity(collectpay);
                    break;
                }
            case R.id.btnLogout:
                if(!TextUtils.isEmpty(member_id)) {
                    new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("提示")
                            .setContentText("是否退出？")
                            .setConfirmText("退出")
                            .setCancelText("取消")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
                                    MenModel.member_id="0";
                                    editor.remove("member_id");
                                    editor.clear();
                                    editor.commit();
                                    Toast.makeText(MenActivity.this, "退出成功", Toast.LENGTH_LONG).show();
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
                Intent i=new Intent(this,LoginActivity.class);
                startActivity(i);
                break;


        }
    }



}
