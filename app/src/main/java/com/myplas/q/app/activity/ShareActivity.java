package com.myplas.q.app.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.myplas.q.R;
import com.myplas.q.common.net.ResultCallBack;
import com.myplas.q.common.utils.ScreenUtils;
import com.myplas.q.common.utils.SharedUtils;
import com.myplas.q.common.utils.StatusUtils;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.common.api.API;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编写： 黄双
 * 电话：15378412400
 * 邮箱：15378412400@163.com
 * 时间：2017/4/14 15:32
 */
public class ShareActivity extends BaseActivity implements View.OnClickListener, ResultCallBack {
    private View view;
    private IWXAPI api;
    private ImageView shareTocircle, sharetofriends;

    private int flag;
    private String type;
    private boolean isShareed;
    private final String DESCRIPTION = "塑料圈通讯录-塑料人都在用";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wd_share_popou);
        int screenHeight = ScreenUtils.getScreenHeight(this);
        int statusBarHeight = StatusUtils.getStatusBarHeight(this);
        int dialogHeight = screenHeight - statusBarHeight;
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight == 0
                ? ViewGroup.LayoutParams.MATCH_PARENT
                : dialogHeight);

        view = F(R.id.share_view);
        shareTocircle = F(R.id.share_py);
        sharetofriends = F(R.id.share_wx);
        type = getIntent().getStringExtra("type");

        view.setOnClickListener(this);
        shareTocircle.setOnClickListener(this);
        sharetofriends.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_py:
            case R.id.share_wx:
                flag = (v.getId() == R.id.share_py) ? (0) : (1);
                switch (type) {
                    case "1"://店铺
                        isShareed = shareToWX(API.PLASTIC_CONTACT + getIntent().getStringExtra("id")
                                , getIntent().getStringExtra("title")
                                , getIntent().getStringExtra("des")
                                , R.drawable.personshare);

                        shareLog("5", getIntent().getStringExtra("id"));//加积分
                        break;
                    case "2"://文章
                        isShareed = shareToWX(API.PLASTIC_SUCRIBLE + getIntent().getStringExtra("id")
                                , getIntent().getStringExtra("title")
                                , null
                                , R.drawable.toutiaologo);

                        shareLog("3", getIntent().getStringExtra("id"));//加积分
                        break;
                    case "3"://授信
                        shareToWX(API.PLASTIC_CREDIT + getIntent().getStringExtra("id")
                                , getIntent().getStringExtra("title")
                                , null
                                , R.drawable.sharelog);
                        break;
                    case "4"://供求
                        isShareed = shareToWX(API.PLASTIC_SUPPLY_DEMAND + getIntent().getStringExtra("id") + "/" + getIntent().getStringExtra("userid")
                                , getIntent().getStringExtra("title")
                                , null
                                , R.drawable.gongqiulogo);

                        shareLog(getIntent().getStringExtra("supdemType")
                                , getIntent().getStringExtra("id"));//加积分
                        break;
                    case "5"://供求-QQ
                        isShareed = shareToWX(API.PLASTIC_SUPPLY_DEMAND_QQ + getIntent().getStringExtra("id")
                                , getIntent().getStringExtra("title")
                                , null
                                , R.drawable.gongqiulogo);

                        shareLog(getIntent().getStringExtra("supdemType")
                                , getIntent().getStringExtra("id"));//加积分
                        break;
                    case "6"://黑名单
                        isShareed = shareToWX(API.PLASTIC_BLACKLIST + getIntent().getStringExtra("id")
                                , getIntent().getStringExtra("title")
                                , getIntent().getStringExtra("des")
                                , R.drawable.blacklist);
                        break;
                    default:
                        break;
                }
                finish();
                break;
            case R.id.share_view:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * @param url
     * @param title
     * @param des
     * @param resId
     * @return
     */
    public boolean shareToWX(String url, String title, String des, int resId) {
        if (isWebchatAvaliable()) {
            api = WXAPIFactory.createWXAPI(this, API.WXAPI);
            api.registerApp(API.WXAPI);
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;

            //创建一个WXMediaMessage对象
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = title;
            msg.description = (null == des ? DESCRIPTION : des);

            Bitmap bp = BitmapFactory.decodeResource(getResources(), resId);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bp, 150, 150, true);
            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
            ;
            thumbBmp.recycle();
            bp.recycle();

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());//transaction字段用于唯一标识一个请求，这个必须有，否则会出错
            req.message = msg;
            //表示发送给朋友圈  WXSceneTimeline  表示发送给朋友  WXSceneSession
            req.scene = flag == 1
                    ? SendMessageToWX.Req.WXSceneSession
                    : SendMessageToWX.Req.WXSceneTimeline;

            return api.sendReq(req);
        } else {
            TextUtils.toast(this, "你的手机没有安装微信！");
            return false;
        }
    }

    /**
     * 分享加塑豆
     *
     * @param type
     * @param id
     */
    public void shareLog(String type, String id) {
        if (isShareed) {
            Map<String, String> map = new HashMap<String, String>(8);
            map.put("id", id);
            map.put("type", type);
            postAsyn(this, API.SAVE_SHARE_LOG, map, this, 1);
        }
    }

    @Override
    public void callBack(Object object, int type) {

    }

    @Override
    public void failCallBack(int type, String message, int httpCode) {

    }

    /**
     * 判断是否安装微信
     *
     * @return
     */
    private boolean isWebchatAvaliable() {
        try {
            final PackageManager packageManager = getPackageManager();// 获取packagemanager
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    if ("com.tencent.mm".equals(pn)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
