package com.myplas.q.release;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.myplas.q.R;
import com.myplas.q.app.fragment.BaseFragment;
import com.myplas.q.common.api.API;
import com.myplas.q.common.appcontext.ActivityManager;
import com.myplas.q.common.appcontext.Constant;
import com.myplas.q.common.net.ResultCallBack;
import com.myplas.q.common.utils.SharedUtils;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.common.view.CommonDialog;
import com.myplas.q.common.view.MyBottomSheetDialog;
import com.myplas.q.common.view.MyEditText;
import com.myplas.q.app.activity.MainActivity;
import com.myplas.q.myself.supdem.MySupDemActivity;
import com.myplas.q.myself.vip.UnEstablishedVipActivity;
import com.myplas.q.release.bean.PreViewBean;
import com.myplas.q.supdem.activity.SupDem_Detail_Activity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者:huangshuang
 * 事件 2017/10/7.
 * 邮箱： 15378412400@163.com
 */

public class QuicklyFragment extends BaseFragment implements View.OnClickListener
        , ResultCallBack
        , MyEditText.OnTextWatcher, CommonDialog.DialogShowInterface {
    private View view;
    private ListView preListView;
    private ImageView preImgClose;
    private MyEditText mEditText, mtvType;
    private TextInputLayout textInputLayout;
    private TextView dialogTitle, dialogContent, dialogOK, dialogCancle;

    private Dialog preDialog, mDialog;
    private MyBottomSheetDialog mButtomDialog;

    private SharedUtils mSharedUtils;
    private String content, type, isPreView, id;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedUtils = SharedUtils.getSharedUtils();
        id = getActivity().getIntent().getStringExtra("id");

        view = View.inflate(getActivity(), R.layout.fragment_layout_release_quickly, null);
        mEditText = (MyEditText) view.findViewById(R.id.release_edit);
        mtvType = (MyEditText) view.findViewById(R.id.release_ev_type_);
        textInputLayout = (TextInputLayout) view.findViewById(R.id.text_input_ll);

        mEditText.addOnTextWatcher(this);
        mtvType.setOnClickListener(this);
        //textInputLayout.setHint("在次文本框中，可快速复制粘贴供求信息，限制100字内！例如：伊朗石化 7000F 10000 上海浦东 现货");
        mEditText.setHint("在次文本框中，可快速复制粘贴供求信息，限制100字内！例如：伊朗石化 7000F 10000 上海浦东 现货");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.release_ev_type_:
                openBottom();
                break;
            case R.id.buttom_dialog_tv1:
                mButtomDialog.dismiss();
                type = "2";
                mtvType.setText("供给");
                break;
            case R.id.buttom_dialog_tv2:
                mButtomDialog.dismiss();
                type = "1";
                mtvType.setText("求购");
                break;

            //获取解析内容
            case R.id.btn_cancle:
                mDialog.dismiss();
                isPreView = "0";
                analysis();
                break;

            //不解析直接发布
            case R.id.btn_ok:
                mDialog.dismiss();
                isPreView = "1";
                pub();
                break;
            default:
                break;
        }
    }

    /**
     * 底部弹窗dialog
     */
    private void openBottom() {
        TextView textView1, textView2;
        View view = View.inflate(getActivity(), R.layout.release_buttom_dialog_layout, null);
        textView1 = (TextView) view.findViewById(R.id.buttom_dialog_tv1);
        textView2 = (TextView) view.findViewById(R.id.buttom_dialog_tv2);

        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
        textView1.setText("供给");
        textView2.setText("求购");

        mButtomDialog = new MyBottomSheetDialog(getActivity());
        mButtomDialog.setContentView(view);
        mButtomDialog.show();
    }

    /**
     * 不解析直接发布
     */

    public void analysis() {
        Map<String, String> map = new HashMap<>(8);
        map.put("type", type);
        map.put("content", content);
        getAsyn(getActivity(), API.ANALYSIS, map, this, 1);
    }

    /**
     * 解析后发布
     */

    public void pub() {
        Map<String, String> map = new HashMap<>(16);
        map.put("mode", "1");
        map.put("model", "");
        map.put("price", "");
        map.put("vendor", "");
        map.put("type", type);
        map.put("channel", "1");
        map.put("storehouse", "");
        map.put("content", content);
        map.put("transaction_type", "");
        map.put("is_preview", isPreView);
        postAsyn(getActivity(), API.RELEASE_MSG, map, this, 2);
    }

    @Override
    public void onTextChanged(View v, String s) {
        String s1 = s.toString();
        if (s.length() >= 100) {
            TextUtils.toast(getActivity(), "输入的字符已达上限！");
        }
    }

    @Override
    public void callBack(Object object, int type) {
        try {
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(object.toString());
            String err = jsonObject.getString("code");
            if (type == 1) {
                if ("0".equals(err)) {
                    PreViewBean preViewBean = gson.fromJson(object.toString(), PreViewBean.class);
                    if (preViewBean.getData().size() != 0) {
                        Intent intent = new Intent(getActivity(), InstantReleaseActivity.class);
                        intent.putExtra("preViewBean", preViewBean);
                        intent.putExtra("type", QuicklyFragment.this.type);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    } else {
                        TextUtils.toast(getActivity(), "请按照示例填写完整的参数！");
                    }
                } else {
                    TextUtils.toast(getActivity(), jsonObject.getString("message"));
                }
            }
            if (type == 2) {
                if ("0".equals(err)) {
                    //关闭activity
                    MainActivity mainActivity = (MainActivity) ActivityManager.getActivity(MainActivity.class);
                    mainActivity.goToSupDem();

                    //跳转到供求详情
                    Intent intent1 = new Intent(getActivity(), SupDem_Detail_Activity.class);
                    intent1.putExtra("id", jsonObject.getString("id"));
                    intent1.putExtra("userid", mSharedUtils.getData(getActivity(), Constant.USERID));
                    startActivity(intent1);

                    ActivityManager.finishActivity(ReleaseActivity.class);
                    if (id != null) {
                        ActivityManager.finishActivity(MySupDemActivity.class);
                    }
                } else {
                    TextUtils.toast(getActivity(), jsonObject.getString("message"));
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void failCallBack(int type, String message, int httpCode) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            String msg = jsonObject.getString("message");
            if (httpCode == 403) {
                CommonDialog dialog = new CommonDialog();
                dialog.showDialog(getActivity(), msg, 3, this);
            } else {
                TextUtils.toast(getActivity(), msg);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 提示dialog
     */
    public void showDialog() {
        if (!isInPutContent(2)) {
            return;
        }
        View view = View.inflate(getActivity(), R.layout.dialog_layout_common, null);
        if (mDialog == null) {
            mDialog = new Dialog(getActivity(), R.style.commondialog_style);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setContentView(view);

            setDialogWindowAttr(mDialog, 0.667f, -1);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        dialogOK = (TextView) view.findViewById(R.id.btn_ok);
        dialogCancle = (TextView) view.findViewById(R.id.btn_cancle);
        dialogTitle = (TextView) view.findViewById(R.id.dialog_title);
        dialogContent = (TextView) view.findViewById(R.id.dialog_message);

        dialogOK.setText("立即发布");
        dialogCancle.setText("确定");
        dialogTitle.setText("塑料圈通讯录");
        dialogContent.setText("您是否需要预览？可能需要等待几秒钟");
        dialogCancle.setTextColor(getResources().getColor(R.color.color_black));
        dialogOK.setOnClickListener(this);
        dialogCancle.setOnClickListener(this);
    }

    public boolean isInPutContent(int _type) {
        String t = mtvType.getText().toString();
        content = mEditText.getText().toString();
        if (_type == 1) {
            return !TextUtils.notEmpty(content)
                    && !TextUtils.notEmpty(t) ? false : true;
        } else {
            if (!TextUtils.notEmpty(content) || !TextUtils.notEmpty(t)) {
                TextUtils.toast(getActivity(), "您还未输入内容或者没有选择发布类型！");
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 设置dialog属性
     */
    public void setDialogWindowAttr(Dialog dialog, float _width, float _height) {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = (int) (width * _width);//宽高可设置具体大小
        lp.height = _height == -1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (_height * height);
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void dialogClick(int type) {
        if (type == 3) {
            startActivity(new Intent(getActivity(), UnEstablishedVipActivity.class));
        }
    }
}
