package com.zan99.guaizhangmen.Widget;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zan99.guaizhangmen.R;

/**
 * Created by Administrator on 2017/11/30.
 */

public class RadioButton   extends LinearLayout {

        private Context context;

        private ImageView imageView;

        private TextView textView;

        private int index = 0;
        private int id = 0;// 判断是否选中

        private RadioButton tempRadioButton;// 模版用于保存上次点击的对象
        private int state[] = { R.drawable.radio_unchecked,
                R.drawable.radio_checked };


        public RadioButton(Context context) {
            super(context);
        }

        /***

         * 改变图片

         */

        public void ChageImage() {



            index++;

            id = index % 2;// 获取图片id

            imageView.setImageResource(state[id]);

        }



        /***

         * 设置文本

         *

         * @param text

         */

        public void setText(String text) {

            textView.setText(text);

        }



        public String getText() {

            return id == 0 ? "" : textView.getText().toString();



        }



}
