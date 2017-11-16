package com.myplas.q.myself.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myplas.q.R;
import com.myplas.q.common.api.API;
import com.myplas.q.common.netresquset.ResultCallBack;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.common.view.MyEditText;
import com.myplas.q.guide.activity.BaseActivity;
import com.myplas.q.myself.supdem.MySupDemActivity;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 黄双
 * @date 2017/11/1 0001
 */

public class FragmentRegister1 extends Fragment implements View.OnClickListener
        , ResultCallBack
        , MyEditText.OnTextWatcher {
    private View mView;
    private Button buttonNext;
    private ImageView mImgRead;
    private LinearLayout mLayoutRead;
    private TextView mTVIndentify, mTVRead;
    private MyEditText mPhone, mPassWord, mIndentify;

    private int count;
    private boolean checked;
    private Handler mHandler;
    private WeakReference<Activity> wr;
    public BaseInterface mBaseInterface;
    private String phone, pass, indentify;


    public static FragmentRegister1 newInstance() {
        FragmentRegister1 fragment = new FragmentRegister1();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("interface", mBaseInterface);
//        fragment.setArguments(bundle);
        return fragment;
    }


    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        count = 60;
//        mBaseInterface = (BaseInterface) getArguments().getSerializable("interface");
        wr = new WeakReference<Activity>(getActivity());
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Activity activity = wr.get();
                if (msg.what == 1 && activity != null) {
                    mTVIndentify.setText(msg.obj.toString() + "秒后重试");
                    mTVIndentify.setClickable(false);
                    if ("0".equals(msg.obj.toString())) {
                        mTVIndentify.setText("重新发送");
                        mTVIndentify.setClickable(true);
                    }
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = View.inflate(container.getContext(), R.layout.fragment_register1_layout, null);
        buttonNext = (Button) mView.findViewById(R.id.register_ok);
        mPhone = (MyEditText) mView.findViewById(R.id.register_tel);
        mImgRead = (ImageView) mView.findViewById(R.id.register_img);
        mTVRead = (TextView) mView.findViewById(R.id.register_tv_read);
        mPassWord = (MyEditText) mView.findViewById(R.id.register_pass);
        mLayoutRead = (LinearLayout) mView.findViewById(R.id.register_read);
        mIndentify = (MyEditText) mView.findViewById(R.id.register_identify);
        mTVIndentify = (TextView) mView.findViewById(R.id.register_tv_indentify);

        mTVRead.setText(Html.fromHtml("已阅读<font color='#0099cc'>" + "《塑料圈通讯录协议》</font>"));

        mTVRead.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        mLayoutRead.setOnClickListener(this);
        mTVIndentify.setOnClickListener(this);

        mPhone.addOnTextWatcher(this);
        mPassWord.addOnTextWatcher(this);
        mIndentify.addOnTextWatcher(this);

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_tv_indentify:
                validUserMobile();
                break;
            case R.id.register_ok:
                validIndentify();
                break;
            case R.id.register_read:
                mImgRead.setImageResource(checked
                        ? R.drawable.btn_checkbox_hl
                        : R.drawable.btn_checkbox1);
                checked = !checked;
                break;
            case R.id.register_tv_read:
                startActivity(new Intent(getActivity(), RegisterAgreementActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 获取验证码之前先验证手机号
     */
    public void validUserMobile() {
        phone = mPhone.getText().toString();
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", phone);
        BaseActivity.postAsyn1(getActivity(), API.BASEURL + API.VALIDUSERMOBILE, map, this, 1, false);
    }

    /**
     * 获取验证码
     */
    private void getIndentify() {
        if (!TextUtils.isPhoneNum(phone)) {
            TextUtils.Toast(getActivity(), "手机号输入有误！");
            return;
        }
        Map<String, String> map1 = new HashMap<String, String>(4);
        map1.put("type", "0");
        map1.put("mobile", phone);
        String url = API.BASEURL + API.SEND_MSG;
        BaseActivity.postAsyn(getActivity(), url, map1, this, 2);
    }

    /**
     * 验证验证码
     */
    private void validIndentify() {
        pass = mPassWord.getText().toString();
        indentify = mIndentify.getText().toString();
        if (!TextUtils.isNullOrEmpty(pass) || !TextUtils.isNullOrEmpty(indentify)) {
            TextUtils.Toast(getActivity(), "请输入完整信息！");
            return;
        }
        if (checked) {
            TextUtils.Toast(getActivity(), "请您先阅读《塑料圈通讯录》相关协议！");
            return;
        }
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("mobile", phone);
        map1.put("password", pass);
        map1.put("verification", indentify);
        String url = API.BASEURL + API.VALIDVERIFICATIONCODE;
        BaseActivity.postAsyn(getActivity(), url, map1, this, 3);
    }


    @Override
    public void onTextChanged(View v, String s) {
        boolean isNomalNull = TextUtils.isNullOrEmpty(mPhone.getText().toString())
                && TextUtils.isNullOrEmpty(mIndentify.getText().toString())
                && TextUtils.isNullOrEmpty(mPassWord.getText().toString());
        buttonNext.setBackgroundResource(isNomalNull
                ? R.drawable.login_btn_shape_hl
                : R.drawable.login_btn_shape);
    }

    public void initThread() {
        new Thread() {
            @Override
            public void run() {
                for (int i = count; i >= 0; i--) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = i;
                    mHandler.sendMessage(msg);
                    try {
                        sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    @Override
    public void callBack(Object object, int type) {
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if (type == 1) {
                if (!jsonObject.getString("err").equals("0")) {
                    TextUtils.Toast(getActivity(), jsonObject.getString("msg"));
                } else {
                    getIndentify();
                }
            }
            if (type == 2) {
                TextUtils.Toast(getActivity(), jsonObject.getString("msg"));
                if ("0".equals(jsonObject.getString("err"))) {
                    initThread();
                }
            }
            if (type == 3) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPhone.getWindowToken(), 0);
                if (!jsonObject.getString("err").equals("0")) {
                    buttonNext.setBackgroundResource(R.drawable.login_btn_shape_hl);
                    TextUtils.Toast(getActivity(), jsonObject.getString("msg"));
                } else {
                    if (mBaseInterface != null) {
                        mBaseInterface.complete(1);
                        mBaseInterface.dataBack(this, Arrays.asList(phone, pass, indentify));
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void failCallBack(int type) {

    }
}
