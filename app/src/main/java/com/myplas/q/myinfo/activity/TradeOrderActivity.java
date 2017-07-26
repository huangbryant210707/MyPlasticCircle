package com.myplas.q.myinfo.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myplas.q.R;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.common.view.CustomPopupWindow;
import com.myplas.q.common.view.MyListview;
import com.myplas.q.common.view.NoResultLayout;
import com.myplas.q.guide.activity.BaseActivity;
import com.myplas.q.myinfo.adapter.TradeOrder_Listview_Adapter;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;
import com.umeng.analytics.MobclickAgent;

/**
 * 编写： 黄双
 * 电话：15378412400
 * 邮箱：15378412400@163.com
 * 时间：2017/3/28 10:25
 */
public class TradeOrderActivity extends BaseActivity implements OnClickListener, TradeOrder_Listview_Adapter.MyOnClickListener {
    private Information information;
    private String appkey = "c1ff771c06254db796cd7ce1433d2004";

    private EditText mEditText;
    private MyListview mListView;
    private ImageView mImageView;
    private NoResultLayout mNoResultLayout;

    private TradeOrder_Listview_Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_tradeorder);
        goBack(findViewById(R.id.img_back));

        initView();
    }

    public void initView() {
        mImageView = F(R.id.img_contact);
        mListView = F(R.id.trade_order_listview);
        mEditText = F(R.id.trade_order_edittext);
        mNoResultLayout = F(R.id.trade_order_noresultlayout);

        mImageView.setOnClickListener(this);
        mAdapter = new TradeOrder_Listview_Adapter(this, null);
        mAdapter.setMyOnClickListener(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_contact:
                information = new Information();
                information.setAppkey(appkey);
                SobotApi.startSobotChat(TradeOrderActivity.this, information);
                break;

        }
    }

    @Override
    public void onClickSigned(View view) {
        showSignedPopou("签收时间：2017-07-07 13:30", view);
    }

    @Override
    public void onClick2(int position) {

    }

    //签收时间的popou
    public void showSignedPopou(String text, View v) {
        View view = View.inflate(this, R.layout.popou_layout_signed, null);
        TextView textView_refresh = (TextView) view.findViewById(R.id.popou_layout_signed_text);
        CustomPopupWindow popupWindow = new CustomPopupWindow(view, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        textView_refresh.setText(text);
        showPopou(popupWindow, v);
    }

    public void showPopou(CustomPopupWindow popupWindow, View view) {
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            int[] a = new int[2];
            view.getLocationInWindow(a);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, a[1] + mEditText.getHeight());
        } else {
            popupWindow.showAtLoaction(view);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
