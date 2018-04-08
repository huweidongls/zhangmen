package com.zan99.guaizhangmen.Util;

/**
 * Created by Administrator on 2017/9/26.
 */

import android.util.Log;


public class L {
    private L() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "zoneLog";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (!isDebug) return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.i(TAG, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")");
        Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (!isDebug) return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.d(TAG, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")");
        Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (!isDebug) return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.e(TAG, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")");
        Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (!isDebug) return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.v(TAG, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")");
        Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (!isDebug) return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.i(tag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")");
        Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (!isDebug) return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.d(tag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")");
        Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (!isDebug) return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.e(tag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")");
        Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (!isDebug) return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.v(tag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")");
        Log.v(tag, msg);
    }

    private static StackTraceElement getTargetStackTraceElement() {
        // find the target invoked method
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(L.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }

    /**
     * 加密
     *
     * @param input 数据源（需要加密的数据）
     * @param key   秘钥，即偏移量
     * @return 返回加密后的数据
     */
    public static String encrypt(String input, int key) {
        //得到字符串里的每一个字符
        char[] array = input.toCharArray();

        for (int i = 0; i < array.length; ++i) {
            //字符转换成ASCII 码值
            int ascii = array[i];
            //字符偏移，例如a->b
            ascii = ascii + key;
            //ASCII 码值转换为char
            char newChar = (char) ascii;
            //替换原有字符
            array[i] = newChar;

            //以上4 行代码可以简写为一行
            //array[i] = (char) (array[i] + key);
        }

        //字符数组转换成String
        return new String(array);
    }

    /**
     * 解密
     *
     * @param input 数据源（被加密后的数据）
     * @param key   秘钥，即偏移量
     * @return 返回解密后的数据
     */
    public static String decrypt(String input, int key) {
        //得到字符串里的每一个字符
        char[] array = input.toCharArray();
        for (int i = 0; i < array.length; ++i) {
            //字符转换成ASCII 码值
            int ascii = array[i];
            //恢复字符偏移，例如b->a
            ascii = ascii - key;
            //ASCII 码值转换为char
            char newChar = (char) ascii;
            //替换原有字符
            array[i] = newChar;

            //以上4 行代码可以简写为一行
            //array[i] = (char) (array[i] - key);
        }

        //字符数组转换成String
        return new String(array);
    }


}


