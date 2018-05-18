package com.myplas.q.myself.vip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.myplas.q.R;
import com.myplas.q.app.activity.BaseActivity;
import com.myplas.q.common.api.API;
import com.myplas.q.common.net.ResultCallBack;
import com.myplas.q.common.view.CommonDialog;
import com.myplas.q.common.view.RoundCornerImageView;
import com.myplas.q.myself.beans.Member;
import com.myplas.q.myself.beans.MyZone;

import org.json.JSONObject;

/**
 * @author Huangshuang  2018/4/27 0027
 */

public class EstablishedVipActivity extends BaseActivity implements View.OnClickListener, ResultCallBack, CommonDialog.DialogShowInterface {

    private ImageView ivNews, ivStore, ivTried, ivTag;

    private RoundCornerImageView ivHead;
    private TextView tvName, tvC_Name, tvRank;
    private boolean isHeadVip, isStoreVip, isTrialVip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_vip_established);
        initTileBar();
        setTitle("塑料圈会员");

        initView();
        memberStatus();
    }

    private void initView() {
        ivTag = F(R.id.xq_rz);
        ivHead = F(R.id.xq_tx);
        tvRank = F(R.id.wd_title_pm);
        ivNews = F(R.id.img_vip_news);
        tvName = F(R.id.wd_title_name);
        tvC_Name = F(R.id.wd_title_gs);
        ivStore = F(R.id.img_vip_store);
        ivTried = F(R.id.img_vip_ontrial);

        ivNews.setOnClickListener(this);
        ivStore.setOnClickListener(this);
        ivTried.setOnClickListener(this);
    }

    /**
     * 获取会员状态
     */
    public void memberStatus() {
        getAsyn(this, API.MEMBERSHIP, null, this, 1, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_vip_news:
            case R.id.img_vip_store:
            case R.id.img_vip_ontrial:
                openDialog();
                break;
            default:
                break;
        }
    }

    private void openDialog() {
        CommonDialog dialog = new CommonDialog();
        dialog.showDialog(this, "您好，开通会员请拨打400-6129-965", 3, this);
    }

    @Override
    public void callBack(Object object, int type) {
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            String code = jsonObject.getString("code");
            if ("0".equals(code)) {
                Member member = new Gson().fromJson(object.toString(), Member.class);
                showInfo(member.getData());
            }
        } catch (Exception e) {

        }
    }

    private void showInfo(Member.DataBean dataBean) {
        if (dataBean == null) {
            return;
        }
        isVip(dataBean);
        Glide.with(this)
                .load(dataBean.getThumb())
                .into(ivHead);
        tvC_Name.setText(dataBean.getC_name());

        String sex = dataBean.getName() + "  "
                + dataBean.getMobile();
        tvName.setText(sex);

        tvRank.setText("  " + dataBean.getEnd_time() + "  到期");
        //tvRank.setCompoundDrawablesWithIntrinsicBounds(getResIdByVipType(), 0, 0, 0);

        ivTag.setImageResource(getResIdByVipType());
        showBtnResByVipType();
    }

    private void isVip(Member.DataBean dataBean) {
        if (dataBean == null) {
            return;
        }
        isHeadVip = TextUtils.equals("1", dataBean.getHeadingVip());
        isStoreVip = TextUtils.equals("1", dataBean.getCustomerVip());
        isTrialVip = TextUtils.equals("1", dataBean.getApplyCustomerVip());

    }

    private int getResIdByVipType() {
        if (isStoreVip) {
            return R.drawable.icon_store_member;
        } else if (isHeadVip) {
            return R.drawable.icon_news_member;
        }
//        else if (isTrialVip) {
//            return R.drawable.icon_ontrail_member;
//        }
        return 0;
    }

    /**
     * 根据已开通Vip类型显示button样式
     */
    private void showBtnResByVipType() {

        if (isTrialVip) {
            ivTried.setClickable(false);
            ivTried.setImageResource(R.drawable.btn_open_disabled);
        }

        if (isHeadVip) {
            ivNews.setClickable(false);
            ivTried.setClickable(false);
            ivTried.setImageResource(R.drawable.btn_rightnow);
            ivNews.setImageResource(R.drawable.btn_open_disabled);
        }

        if (isStoreVip) {
            ivNews.setClickable(false);
            ivTried.setClickable(false);
            ivStore.setClickable(false);
            ivNews.setImageResource(R.drawable.btn_rightnow);
            ivTried.setImageResource(R.drawable.btn_rightnow);
            ivStore.setImageResource(R.drawable.btn_open_disabled);
        }
    }

    @Override
    public void failCallBack(int type, String message, int httpCode) {

    }

    @Override
    public void dialogClick(int type) {
        if (type == 3) {
            call("4006129965");
        }
    }
}