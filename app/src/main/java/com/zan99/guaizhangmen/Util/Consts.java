package com.zan99.guaizhangmen.Util;

/**
 * Created by 99zan on 2018/1/18.
 */

public class Consts {

    /**
     * 当前数据库版本号
     */
    public static final int DATABASE_VERSION = 1;
    /**
     * 播放请求的广播
     */
    public static final String ACTION_PLAY = "play";
    /**
     * 暂停播放的广播
     */
    public static final String ACTION_PAUSE = "pause";
    /**
     * 播放下一个的广播
     */
    public static final String ACTION_NEXT = "next";
    /**
     * 播放上一个的广播
     */
    public static final String ACTION_PRE = "pre";
    /**
     * 指定播放列表position的广播
     */
    public static final String ACTION_PLAY_TO = "play_to";
    /**
     * seekbar播放的广播
     */
    public static final String ACTION_SEEK_TO = "seek_to";
    /**
     * 更新锁屏界面的广播
     */
    public static final String ACTION_UPDATA_SUOPING = "updata_suoping";

    public static final String ACTION_MEDIA_PLAY_COMPLETE = "on_complete";

}
