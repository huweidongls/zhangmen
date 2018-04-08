package com.zan99.guaizhangmen.Activity.Men;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zan99.guaizhangmen.Adapter.MyWalletMxAdapter;
import com.zan99.guaizhangmen.Bean.WalletMxBean;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.DownloadUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class MyWalletMxActivity extends AppCompatActivity {

    @BindView(R.id.back_left)
    Button backLeft;
    @BindView(R.id.my_wallet_mingxi_list)
    ListView myWalletMingxiList;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.button2)
    LinearLayout button2;

    private int page = 1;
    private List<WalletMxBean.DataListBean> data = new ArrayList<>();
    private MyWalletMxAdapter adapter;
    private static final String TAG = "MyWalletMxActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet_mx);
        ButterKnife.bind(this);

        init();

    }

    private void init() {

        refreshLayout.setRefreshHeader(new MaterialHeader(MyWalletMxActivity.this).setShowBezierWave(true));
        refreshLayout.setRefreshFooter(new BallPulseFooter(MyWalletMxActivity.this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                data.clear();
                loadData(page);
                refreshlayout.finishRefresh();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page = page + 1;
                loadData(page);
                refreshlayout.finishLoadmore();
            }
        });

        loadData(page);

    }

    private void loadData(int page) {

        final String url = "/admin.php/Systeminterface_distributionlog";
        final RequestBody requestBody = new FormBody.Builder()
                .add("memberId", MenModel.member_id)
                .add("page", String.valueOf(page))
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
                        Log.d(TAG, "onResponse: " + result);
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
                loadList(value);
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

    /**
     * 加载列表，解析数据
     *
     * @param value
     */
    private void loadList(String value) {

        Gson gson = new Gson();
        WalletMxBean walletMxBean = new WalletMxBean();
        walletMxBean = gson.fromJson(value, WalletMxBean.class);
        if (data.size() == 0) {
            data = walletMxBean.getDataList();
            adapter = new MyWalletMxAdapter(MyWalletMxActivity.this, data);
            myWalletMingxiList.setAdapter(adapter);
        } else if (data.size() > 0) {
            data.addAll(walletMxBean.getDataList());
            adapter.notifyDataSetChanged();
        }

        Log.d(TAG, "loadList: " + data.size());

    }

    @OnClick({R.id.button2, R.id.back_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_left:
                MyWalletMxActivity.this.finish();
                break;
            case R.id.button2:
                MyWalletMxActivity.this.finish();
                break;
        }
    }
}
