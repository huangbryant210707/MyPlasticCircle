package com.myplas.q.common.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.myplas.q.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 编写： 黄双
 * 电话：15378412400
 * 邮箱：15378412400@163.com
 * 时间：2017/3/27 09:34
 */
public class TextUtils {
    public static final boolean isPhoneNum(String phone){
        Pattern p=Pattern.compile("13\\d{9}|14[57]\\d{8}|15[012356789]\\d{8}|18[01256789]\\d{8}|17[0678]\\d{8}");
        Matcher m=p.matcher(phone);
        return m.matches();
    }
    public static boolean isNullOrEmpty(String s) {
        if ((null==s)||("").equals(s)) {
            return false;
        } else {
            return true;
        }
    }
    public static void Toast(Context context,String s){
        View view= LayoutInflater.from(context).inflate(R.layout.layout_toast,null);
        Toast toast = Toast.makeText(context,"" , Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 30);
        toast.setView(view);
        TextView textView= (TextView) view.findViewById(R.id.toast_text);
        textView.setText(s);
        toast.show();
    }
    public static boolean isQQ(String s) {
        if (s.matches("[1-9][0-9]{4,14}")) {
            return true;
        } else {
            return false;
        }
    }
}