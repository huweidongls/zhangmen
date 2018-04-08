package com.zan99.guaizhangmen.Activity.Men;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.DownloadUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyWalletActivity extends AppCompatActivity {

    @BindView(R.id.back_left)
    Button back;
    @BindView(R.id.tixian_zfb)
    RadioButton zfb;
    @BindView(R.id.tixian_yhk)
    RadioButton yhk;
    @BindView(R.id.my_wallet_tixian)
    Button btnTixian;
    @BindView(R.id.my_wallet_mingxi)
    TextView tvMingxi;
    @BindView(R.id.my_wallet_yu_e)
    TextView myWalletYuE;
    @BindView(R.id.my_wallet_insert_card)
    Button btnInsertCard;
    @BindView(R.id.button2)
    LinearLayout button2;
    @BindView(R.id.rg_pay)
    RadioGroup rgPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        ButterKnife.bind(this);
        init();
        changeRadio();

    }

    private void init() {

        getData();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getData();
    }

    private void getData() {

        final String url = "/admin.php/Systeminterface_getBalance";//获取钱包余额接口
        final RequestBody requestBody = new FormBody.Builder()
                .add("memberId", MenModel.member_id)
                .build();
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                DownloadUtil.get().post(url, requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("111", "请求失败！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Log.e("111", result + "asdsfdsafdasf");
                        e.onNext(result);
                        e.onComplete();
                    }
                });
            }
        });
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                changeYue(value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    private void changeYue(String value) {

        try {
            JSONObject jsonObject = new JSONObject(value);
            String yue = jsonObject.getString("wallet");
            myWalletYuE.setText(yue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @OnClick({R.id.button2, R.id.my_wallet_insert_card, R.id.my_wallet_tixian, R.id.my_wallet_mingxi, R.id.back_left})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.my_wallet_tixian:
//                ToastUtil.showShort(MyWalletActivity.this, "提现！！！");
                if (zfb.isChecked()) {
                    intent.setClass(MyWalletActivity.this, MyWalletTixianActivity.class);
                    intent.putExtra("tixianType", "zfb");
                    startActivity(intent);
                }
                break;
            case R.id.my_wallet_mingxi:
//                ToastUtil.showShort(MyWalletActivity.this, "明细！！！");
                intent.setClass(MyWalletActivity.this, MyWalletMxActivity.class);
                startActivity(intent);
                break;
            case R.id.back_left:
                MyWalletActivity.this.finish();
                break;
            case R.id.button2:
                MyWalletActivity.this.finish();
                break;
            case R.id.my_wallet_insert_card:
                intent.setClass(MyWalletActivity.this, MyWalletTixianActivity.class);
                intent.putExtra("tixianType", "yhk");
                startActivity(intent);
                break;
        }
    }

    private void changeRadio() {
        zfb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (zfb.isChecked()) {
                    yhk.setChecked(false);
                    Drawable right = getResources().getDrawable(R.drawable.radio_checked);
                    zfb.setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);

                    Drawable right1 = getResources().getDrawable(R.drawable.radio_unchecked);
                    yhk.setCompoundDrawablesWithIntrinsicBounds(null, null, right1, null);

                    btnInsertCard.setVisibility(View.GONE);
                }
            }
        });

        yhk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (yhk.isChecked()) {
                    zfb.setChecked(false);
                    Drawable right = getResources().getDrawable(R.drawable.radio_unchecked);
                    zfb.setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);

                    Drawable right1 = getResources().getDrawable(R.drawable.radio_checked);
                    yhk.setCompoundDrawablesWithIntrinsicBounds(null, null, right1, null);

                    btnInsertCard.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}
