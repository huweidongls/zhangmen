package com.zan99.guaizhangmen.Util;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;

import com.zan99.guaizhangmen.R;


/**
 * Created by WANGMUTIAN on 2017/9/26.
 */


public class CountDownTimerUtils extends CountDownTimer {
    private Button button;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils(Button button, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.button = button;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        button.setClickable(false); //设置不可点击
        button.setText(millisUntilFinished / 1000 + "秒后可重新发送");  //设置倒计时时间
        button.setBackgroundResource(R.drawable.radius_bg_gray); //设置按钮为灰
        SpannableString spannableString = new SpannableString(button.getText().toString());
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        button.setTextColor(Color.parseColor("#ffffff"));
        button.setText(spannableString);
    }

    @Override
    public void onFinish() {
        button.setText("重新获取验证码");
        button.setClickable(true);//重新获得点击
        button.setTextColor(Color.parseColor("#e33109"));
        button.setBackgroundResource(R.drawable.radius_border_red);
    }
}