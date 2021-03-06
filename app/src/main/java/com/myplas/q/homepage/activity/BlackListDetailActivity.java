package com.myplas.q.homepage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.huangbryant.hbanner.HBanner;
import com.huangbryant.hbanner.HBannerConfig;
import com.huangbryant.hbanner.Transformer;
import com.huangbryant.hbanner.listener.OnHBannerClickListener;
import com.huangbryant.hbanner.loader.ImageLoader;
import com.myplas.q.R;
import com.myplas.q.app.activity.BaseActivity;
import com.myplas.q.app.activity.ShareActivity;
import com.myplas.q.common.api.API;
import com.myplas.q.common.listener.MyOnItemClickListener;
import com.myplas.q.common.listener.OnKeyboardChangeListener;
import com.myplas.q.common.net.ResultCallBack;
import com.myplas.q.common.utils.KeyboardHelper;
import com.myplas.q.common.utils.StatusUtils;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.common.view.MyBottomSheetDialog;
import com.myplas.q.common.view.MyListview;
import com.myplas.q.homepage.adapter.BlackListDetailAdapter;
import com.myplas.q.homepage.adapter.BlackListDetailIMGAdapter;
import com.myplas.q.homepage.beans.BlackListsDetailBean;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 黄双
 * @date 2018/1/17 0017
 */

public class BlackListDetailActivity extends BaseActivity implements View.OnClickListener
        , MyOnItemClickListener
        , OnKeyboardChangeListener.OnChangeListener
        , ResultCallBack
        , OnHBannerClickListener {

    private int color;
    private boolean isShowKeyboard;
    private String id, content, commentId;

    private View root;
    private Button button;
    private EditText editText;
    private RecyclerView rvImg;
    private RecyclerView mRecycleView;
    private NestedScrollView scrollView;
    private LinearLayout layoutComments, layoutInput;
    private TextView tvTitle, tvAuthor, tvTime, tvPV, tvContent;

    private KeyboardHelper mKeyboardHelper;
    private BlackListDetailAdapter mAdapter;
    private OnKeyboardChangeListener mKeyboardChangeListener;
    private MyBottomSheetDialog mButtomDialog;
    private BlackListsDetailBean bean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusUtils.setStatusBar(this, false, false);
        StatusUtils.setStatusTextColor(true, this);
        setContentView(R.layout.activity_blacklistdetail_layout);
        initTileBar();
        setTitle("详细信息");
        setLeftIVResId(R.drawable.btn_back_black);
        setTitleBarBackground(R.color.colorAccent);
        setTitleBarTextColor(R.color.color_black1);
        setRightIVResId(R.drawable.btn_share_black);

        initView();
        getBlackLists();

    }

    private void initView() {
        commentId = "0";
        id = getIntent().getStringExtra("id");
        color = ContextCompat.getColor(this, R.color.color_red);

        tvPV = F(R.id.detail_pv);
        root = F(R.id.detail_root);
        tvTime = F(R.id.detail_time);
        tvTitle = F(R.id.detail_title);
        scrollView = F(R.id.detail_sv);
        mRecycleView = F(R.id.detail_rv);
        rvImg = F(R.id.detail_mlistview);
        tvAuthor = F(R.id.detail_author);
        tvContent = F(R.id.detail_content);
        layoutComments = F(R.id.layout_comments);
        layoutInput = F(R.id.detail_layout_input);

        button = F(R.id.blacklist_detail_btn);
        editText = F(R.id.blacklist_detail_ev);

        editText.setHint("写留言");
        button.setOnClickListener(this);
        mIVRight.setOnClickListener(this);
        mKeyboardChangeListener = new OnKeyboardChangeListener(this, this);
        layoutInput.addOnLayoutChangeListener(mKeyboardChangeListener);

        mKeyboardHelper = new KeyboardHelper(this, root);
        mKeyboardHelper.enable();

        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        LinearLayoutManager manager2 = new LinearLayoutManager(this);
        mRecycleView.setNestedScrollingEnabled(false);
        rvImg.setNestedScrollingEnabled(false);
        mRecycleView.setLayoutManager(manager1);
        rvImg.setLayoutManager(manager2);
    }

    /**
     * 获取黑名单详情数据
     */
    public void getBlackLists() {
        Map<String, String> map = new HashMap<>(16);
        map.put("id", id);
        getAsyn(this, API.BLACKLISTS + "/" + id, null, this, 1, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        layoutInput.removeOnLayoutChangeListener(mKeyboardChangeListener);
        mKeyboardHelper.disable();
    }

    @Override
    public void onClick(View v) {
        if (bean == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.blacklist_detail_btn:
                reply();
                break;
            case R.id.titlebar_img_right:
                Intent intent = new Intent(this, ShareActivity.class);
                intent.putExtra("type", "6");
                intent.putExtra("id", bean.getBlacklist().getId());
                intent.putExtra("title", bean.getBlacklist().getSubject());
                intent.putExtra("des", bean.getBlacklist().getContent().length() > 25
                        ? bean.getBlacklist().getContent().substring(0, 24) + "..."
                        : bean.getBlacklist().getContent());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 回复
     */
    public void reply() {
        content = editText.getText().toString();
        if (TextUtils.notEmpty(content)) {
            Map<String, String> map = new HashMap<>(16);
            map.put("blacklist_id", id);
            map.put("comments", content);
            map.put("comment_id", commentId);
            postAsyn(this, API.BLACKLISTSCOMMENTS, map, this, 2, false);
        } else {
            TextUtils.toast(this, "请输入回复内容！");
        }
    }

    @Override
    public void OnBannerClick(int position) {

    }

    @Override
    public void onItemClick(String sign, String name, String id, String pur_id, String user_id) {
        commentId = sign;
        editText.setHint("回复:" + name);
        showInPutKeybord(editText);
    }

    @Override
    public void onKeyboardShow() {
        isShowKeyboard = true;
    }

    @Override
    public void onKeyboardHidden() {
        commentId = "0";
        editText.setHint("写留言");
        isShowKeyboard = false;
    }

    @Override
    public void callBack(Object object, int type) {
        try {
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(object.toString());
            if (type == 1 && "0".equals(jsonObject.getString("code"))) {
                bean = gson.fromJson(object.toString(), BlackListsDetailBean.class);
                showDetail(bean.getBlacklist());
            }
            if (type == 2 && "0".equals(jsonObject.getString("code"))) {
                getBlackLists();
                editText.setText("");
                editText.setHint("写留言");
                TextUtils.toast(this, jsonObject.getString("message"));
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void failCallBack(int type, String message, int httpCode) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            TextUtils.toast(this, jsonObject.getString("message"));
        } catch (Exception e) {

        }
    }

    /**
     * 显示数据
     *
     * @param bean
     */
    private void showDetail(BlackListsDetailBean.BlacklistBean bean) {
        if (rvImg.getVisibility() == View.GONE) {
            rvImg.setVisibility(View.VISIBLE);

            BlackListDetailIMGAdapter adapter = new BlackListDetailIMGAdapter(this, bean.getIllustration());
            rvImg.setAdapter(adapter);

            tvPV.setText(bean.getPv());
            tvTitle.setText(bean.getSubject());
            tvContent.setText(bean.getContent());
            tvTime.setText(bean.getCreated_at());
            //tvAuthor.setText(bean.getCreated_at());

            mAdapter = new BlackListDetailAdapter(this, this);
            mAdapter.setList(bean.getComments());
            mRecycleView.setAdapter(mAdapter);

        } else {
            mAdapter.setList(bean.getComments());
            mAdapter.notifyDataSetChanged();
        }

        layoutComments.setVisibility(bean.getComments().size() == 0
                ? View.GONE
                : View.VISIBLE);
    }
}
