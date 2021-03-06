package com.myplas.q.myself.setting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myplas.q.R;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.app.activity.BaseActivity;
import com.myplas.q.common.view.EditTextField;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 编写： 黄双
 * 电话：15378412400
 * 邮箱：15378412400@163.com
 * 时间：2017/3/28 10:25
 */
public class MyMainPro_LogisticsActivity extends BaseActivity implements View.OnClickListener {

    private int position;
    private boolean isLogistics, isClicked;
    private String type, hint, dataBack, logisticsStartData, logisticsEndData;

    private TextView mTextViewOK;
    private ImageView mImageView;
    private EditTextField mtextfieldStart, mtextfieldEnd;

    private LinearLayout mlayoutLogistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_logistics_input);
        init();
        showInfo();
    }

    private void init() {
        initTileBar();
        setRightTVVisibility(View.VISIBLE);
        setTitle(getIntent().getStringExtra("title"));

        type = getIntent().getStringExtra("type");
        hint = getIntent().getStringExtra("hint");

        mTextViewOK = F(R.id.titlebar_text_right);
        mImageView = F(R.id.datacommon_img_logistics);
        mtextfieldEnd = F(R.id.datacommon_edit_ending);
        mtextfieldStart = F(R.id.datacommon_edit_starting);
        mlayoutLogistics = F(R.id.datacommon_ll_logistics);

        mTextViewOK.setOnClickListener(this);
        mlayoutLogistics.setOnClickListener(this);
        mtextfieldStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    logisticsStartData = "";
                } else {
                    isClicked = false;
                    logisticsStartData = s.toString();
                    mImageView.setImageResource(R.drawable.btn_radio);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mtextfieldEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    logisticsEndData = "";
                } else {
                    isClicked = false;
                    logisticsEndData = s.toString();
                    mImageView.setImageResource(R.drawable.btn_radio);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showInfo() {
        if (hint.equals("全国路线")) {
            isClicked = true;
            mImageView.setImageResource(R.drawable.btn_radiohl);
        }
        if (hint.contains("-")) {
            isClicked = false;
            String start = hint.substring(0, hint.indexOf("-"));
            String end = hint.substring(hint.indexOf("-") + 1);
            mtextfieldEnd.setText(end);
            mtextfieldStart.setText(start);
            mtextfieldEnd.setSelection(end.length());
            mtextfieldStart.setSelection(start.length());

            showInPutKeybord(mtextfieldEnd);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.titlebar_text_right) {
            if (!isClicked) {
                if (TextUtils.notEmpty(logisticsStartData)) {
                    if (TextUtils.notEmpty(logisticsEndData)) {
                        dataBack = logisticsStartData + "-" + logisticsEndData;
                        Intent intent = new Intent();
                        intent.putExtra("updateData", dataBack);
                        setResult(1, intent);
                        this.finish();
                    } else {
                        TextUtils.toast(this, "目的地不能为空！");
                    }
                } else {
                    TextUtils.toast(this, "出发地不能为空！");
                }
            } else {
                dataBack = "全国路线";
                Intent intent = new Intent();
                intent.putExtra("updateData", dataBack);
                setResult(1, intent);
                this.finish();
            }
        } else {
            if (isClicked) {
                isClicked = false;
                mImageView.setImageResource(R.drawable.btn_radio);
            } else {
                isClicked = true;
                dataBack = "全国路线";
                logisticsEndData = "";
                logisticsStartData = "";
                mtextfieldEnd.setText("");
                mtextfieldStart.setText("");
                mImageView.setImageResource(R.drawable.btn_radiohl);
            }

        }
    }


}
