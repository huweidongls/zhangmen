package com.zan99.guaizhangmen.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Fragment.GuaiFragment;
import com.zan99.guaizhangmen.Fragment.MenFragment;
import com.zan99.guaizhangmen.Fragment.ZhangFragment;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/11/27.
 */

public class MenuActivity extends BaseActivity {

    @BindView(R.id.tab_menu_message)
    TextView tab_menu_message;

    private TabHost tabHost;
    public static String uploadid="0";

    private List<Fragment> fragmentList = new ArrayList<>();
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    /**
     * 底部菜单第一个按钮
     */
    @BindView(R.id.guai)
    RadioButton radioButton1;
    /**
     * 底部菜单第二个按钮
     */
    @BindView(R.id.zhang)
    RadioButton radioButton2;
    /**
     * 底部菜单第三个按钮
     */
    @BindView(R.id.men)
    RadioButton radioButton3;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        initView();
        initFragment();
        //存储请求二维码的id
        uploadid();
        //请求存储的权限
        verifyStoragePermissions(this);
        //请求memberid 和 二维码id
        init();
    }

    /**
     * 加载fragment
     */
    private void initFragment() {

        Fragment guaiFragment = new GuaiFragment();
        Fragment zhangFragment = new ZhangFragment();
        Fragment menFragment = new MenFragment();

        fragmentList.add(guaiFragment);
        fragmentList.add(zhangFragment);
        fragmentList.add(menFragment);

        fragmentTransaction.add(R.id.fl_container,guaiFragment);
        fragmentTransaction.add(R.id.fl_container,zhangFragment);
        fragmentTransaction.add(R.id.fl_container,menFragment);

        fragmentTransaction.show(guaiFragment).hide(zhangFragment).hide(menFragment);
        fragmentTransaction.commit();
        selectButton(radioButton1);

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        //存储请求二维码的id
        uploadid();
        //请求memberid 和 二维码id
        init();
    }


    private void init(){
        SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
        String memberId = L.decrypt(pref.getString("member_id",""),MenModel.LKEY);
        MenuActivity.uploadid = L.decrypt(pref.getString("uploadid",""),MenModel.LKEY);

        if(memberId!=""){
            MenModel.member_id = memberId;
        }


    }


    private void uploadid(){
        SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
        editor.putString("uploadid", L.encrypt(uploadid,MenModel.LKEY));
        editor.commit();
    }

    // 加载视图
    private void initView() {
//        tab_menu_message.setVisibility(View.VISIBLE);

        // 加载底部菜单
//        initTabView();

        // 关联事件
        initEvent();
    }

    /*
     *  关联底部TabView
     */
//    private void initTabView() {
//        String guai=this.getString(R.string.guai);
//        System.out.println("guai:"+guai);
//        tabHost = this.getTabHost();
//
//        TabHost.TabSpec spec;
//        Intent intent;
//
//        intent = new Intent().setClass(this, GuaiActivity.class);
//        spec=tabHost.newTabSpec("zhangmenketang").setIndicator("zhangmenketang").setContent(intent);
//        tabHost.addTab(spec);
//
//        intent = new Intent().setClass(this, ZhangActivity.class);
//        spec = tabHost.newTabSpec("dakafangtan").setIndicator("dakafangtan").setContent(intent);
//        tabHost.addTab(spec);
//
//        intent = new Intent().setClass(this, MenActivity.class);
//        spec=tabHost.newTabSpec("gerenzhongxin").setIndicator("gerenzhongxin").setContent(intent);
//        tabHost.addTab(spec);
//
//    }

    // 注册点击事件
    private void initEvent() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rgMenus);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.guai:
                        selectButton(radioButton1);
                        switchFragment(0);
                        break;
                    case R.id.zhang:
                        selectButton(radioButton2);
                        switchFragment(1);
                        break;
                    case R.id.men:
                        selectButton(radioButton3);
                        switchFragment(2);
                        break;
                }
            }
        });
    }

    /**
     * 控制底部菜单按钮的选中
     * @param v
     */
    public void selectButton(View v) {
        radioButton1.setSelected(false);
        radioButton2.setSelected(false);
        radioButton3.setSelected(false);
        v.setSelected(true);
    }

    /**
     * 选择隐藏与显示的Fragment
     * @param index 显示的Frgament的角标
     */
    private void switchFragment(int index){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        for(int i = 0; i < fragmentList.size(); i++){
            if (index == i){
                fragmentTransaction.show(fragmentList.get(index));
            }else {
                fragmentTransaction.hide(fragmentList.get(i));
            }
        }
        fragmentTransaction.commit();
    }

//    private void updateTab(int checkedId) {
//
//        int[] androidid={R.id.guai,R.id.zhang,R.id.men};
//        int[] drawableid={R.drawable.guai2,R.drawable.zhang2,R.drawable.men2};
//        int[] drawablehoverid={R.drawable.guai2hover,R.drawable.zhang2hover,R.drawable.men2hover};
//        for(int i=0;i<androidid.length;i++){
//            int aid=androidid[i];
//
//            RadioButton radioButton= (RadioButton) findViewById(aid);
//            if(aid == checkedId){
//                radioButton.setTextColor(Color.parseColor("#c71521"));
//                Drawable top = getResources().getDrawable(drawablehoverid[i]);
//                radioButton.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
//            }else{
//                radioButton.setTextColor(Color.parseColor("#a7a7a7"));
//                Drawable top = getResources().getDrawable(drawableid[i]);
//                radioButton.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
//            }
//        }
//    }


    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(this);      //调用双击退出函数
        }
        return false;  //不会执行退出事件
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    public static void exitBy2Click(Activity activity) {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(activity, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            activity.finish();
            System.exit(0);
        }
    }


}
