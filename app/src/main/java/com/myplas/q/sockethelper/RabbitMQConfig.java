package com.myplas.q.sockethelper;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myplas.q.common.api.API;
import com.myplas.q.common.appcontext.Constant;
import com.myplas.q.common.utils.ACache;
import com.myplas.q.common.utils.SharedUtils;
import com.myplas.q.guide.activity.BaseActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者:huangshuang
 * 事件 2017/10/16 0016.
 * 邮箱： 15378412400@163.com
 */

public class RabbitMQConfig implements com.myplas.q.common.netresquset.ResultCallBack {

    private ACache mACache;
    private Context context;
    private static RabbitMQConfig mRabbitMQConfig;


    private RabbitMQConfig(Context context) {
        this.context = context;
    }

    public static RabbitMQConfig getInstance(Context context) {
        if (mRabbitMQConfig == null) {
            mRabbitMQConfig = new RabbitMQConfig(context.getApplicationContext());
            return mRabbitMQConfig;
        }
        return mRabbitMQConfig;
    }

    /*关闭链接*/
    public void closed() {
        Map<String, String> map = new HashMap<>();
        map.put("userid", SharedUtils.getSharedUtils().getData(context, Constant.USERID));
        BaseActivity.postAsyn1(context, API.BASEURL + API.CLOSED, map, this, 2, false);
    }

    /*链接成功*/
    public void connected() {
        BaseActivity.postAsyn1(context, API.BASEURL + API.CONNECTED, null, this, 3, false);
    }

    @Override
    public void callBack(Object object, int type) {
        try {
            Gson gson = new Gson();
            String err = new JSONObject(object.toString()).getString("err");
        } catch (Exception e) {

        }
    }

    @Override
    public void failCallBack(int type) {

    }
}