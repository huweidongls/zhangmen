package com.zan99.guaizhangmen.Activity.Men;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.BankInfoBean;
import com.zan99.guaizhangmen.Util.DownloadUtil;
import com.zan99.guaizhangmen.Util.ToastUtil;

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

public class MyWalletTixianActivity extends AppCompatActivity {

    @BindView(R.id.back_left)
    Button backLeft;
    @BindView(R.id.my_wallet_tijiao)
    TextView myWalletTijiao;
    @BindView(R.id.name)
    MaterialEditText name;
    @BindView(R.id.card_number)
    MaterialEditText cardNumber;
    @BindView(R.id.kaihuhang)
    TextView kaihuhang;
    @BindView(R.id.zfb_number)
    MaterialEditText zfbNumber;
    @BindView(R.id.zfb_name)
    MaterialEditText zfbName;
    @BindView(R.id.zfb_jine)
    MaterialEditText zfbJine;

    BankInfoBean bankInfoBean;

    String tixianType;
    @BindView(R.id.button2)
    LinearLayout button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet_tixian);
        ButterKnife.bind(this);

        init();

    }

    private void init() {

        Intent intent = getIntent();
        tixianType = intent.getStringExtra("tixianType");
        if (tixianType.equals("zfb")) {
            name.setVisibility(View.GONE);
            cardNumber.setVisibility(View.GONE);
            kaihuhang.setVisibility(View.GONE);
        } else if (tixianType.equals("yhk")) {
            zfbName.setVisibility(View.GONE);
            zfbNumber.setVisibility(View.GONE);
            zfbJine.setVisibility(View.GONE);
        }

        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 在输入数据前监听
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 在输入数据时监听
                int huoqu = cardNumber.getText().toString().length();
                if (huoqu >= 15) {
                    String huoqucc = cardNumber.getText().toString().trim();
                    bankInfoBean = new BankInfoBean(huoqucc);
                    kaihuhang.setText(bankInfoBean.getBankName());
                } else {
                    kaihuhang.setText(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 在输入数据后监听
            }
        });

    }

    @OnClick({R.id.button2, R.id.back_left, R.id.my_wallet_tijiao})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_left:
                MyWalletTixianActivity.this.finish();
                break;
            case R.id.button2:
                MyWalletTixianActivity.this.finish();
                break;
            case R.id.my_wallet_tijiao:
//                ToastUtil.showShort(MyWalletTixianActivity.this, "提交！");
                if (tixianType.equals("zfb") && !zfbNumber.getText().toString().equals("") && !zfbName.getText().toString().equals("") && !zfbJine.getText().toString().equals("")) {
                    tiXianZfb();
                } else if (tixianType.equals("yhk") && !name.getText().toString().equals("") && !cardNumber.getText().toString().equals("") && !kaihuhang.getText().toString().equals("")) {
                    tiXianYhk();
                } else {
                    ToastUtil.showShort(MyWalletTixianActivity.this, "请填写完整信息！");
                }
                break;
        }
    }

    private void tiXianZfb() {
        String name = zfbName.getText().toString();
        String number = zfbNumber.getText().toString();
        String amount = zfbJine.getText().toString();
        final String url = "/admin.php/Systeminterface_alipaygateway";
        final RequestBody requestBody = new FormBody.Builder()
                .add("payee_account", number)
                .add("payee_real_name", name)
                .add("amount", amount)
                .add("memberId", MenModel.member_id)
                .build();
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                DownloadUtil.get().post(url, requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("111", "请求失败！！！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Log.e("111", "请求成功！！！" + result);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            String message = jsonObject.getString("errmsg");
                            e.onNext(message);
                            e.onComplete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                ToastUtil.showShort(MyWalletTixianActivity.this, value);
                MyWalletTixianActivity.this.finish();
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

    private void tiXianYhk() {
        String userName = name.getText().toString();
        String card = cardNumber.getText().toString();
        String openaccount = kaihuhang.getText().toString();
        final String url = "/admin.php/Systeminterface_insertwithdrawal";
        final RequestBody requestBody = new FormBody.Builder()
                .add("memberId", MenModel.member_id)
                .add("openaccount", openaccount)
                .add("card", card)
                .add("name", userName)
                .build();
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                DownloadUtil.get().post(url, requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("111", "请求失败！！！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Log.e("111", "请求成功！！！" + result);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            String message = jsonObject.getString("errmsg");
                            e.onNext(message);
                            e.onComplete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                ToastUtil.showShort(MyWalletTixianActivity.this, value);
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
}
