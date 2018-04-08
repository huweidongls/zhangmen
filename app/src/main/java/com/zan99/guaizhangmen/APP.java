package com.zan99.guaizhangmen;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by Administrator on 2017/11/28.
 */

public class APP extends Application {

    //app2018.04.08
    private static APP instance;

    public synchronized static APP getInstance() {
        if (null == instance) {
            instance = new APP();
        }
        return instance;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
