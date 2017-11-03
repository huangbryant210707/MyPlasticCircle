package com.myplas.q.myself.setting.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.myplas.q.R;
import com.myplas.q.common.api.API;
import com.myplas.q.common.netresquset.ResultCallBack;
import com.myplas.q.common.utils.SharedUtils;
import com.myplas.q.common.view.RoundCornerImageView;
import com.myplas.q.guide.activity.BaseActivity;
import com.myplas.q.myself.beans.MySelfInfo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 编写： 黄双
 * 电话：15378412400
 * 邮箱：15378412400@163.com
 * 时间：2017/3/23 13:39
 */
public class MyDataActivity extends BaseActivity implements View.OnClickListener, ResultCallBack {

    private IntentFilter mFilter;
    private MyReceiver myReceiver;

    private int regionPosition;
    private MySelfInfo mySelfInfo;
    private SharedUtils sharedUtils;
    private Map<String, String> map;
    private String sexInPut, regionInPut;
    private String type, address, addressId, sex, region, product, monthUse, mainPro, model;

    private RoundCornerImageView image_tx;
    private ImageView imageShch, cardMore, headMore;
    private RadioGroup radiogroupSex, radiogroupAddress;
    private LinearLayout llProMonth, llAdd, llSex, llRegion, llPro, llMothonuse, llMainsell, llMode;
    private TextView textXb, textviewDzh, textGs, textDh, textviewZhy, textviewPh, textviewNum, textviewProduct,
            textviewAddress, textviewCompany, myMainProd, myMainProdSave;

    private boolean isFromContact;
    private final String ACTION = "com.broadcast.databack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_seeting_data);
        initTileBar();
        initView();
    }

    public void initView() {
        map = new HashMap<String, String>();
        sharedUtils = SharedUtils.getSharedUtils();
        isFromContact = getIntent().getStringExtra("userid") == null ? false : true;

        textGs = F(R.id.wd_zl_gs);
        textDh = F(R.id.wd_zl_tel);
        cardMore = F(R.id.data_img_card);
        headMore = F(R.id.data_head_card);
        image_tx = F(R.id.wd_zl_text_headimg);
        imageShch = F(R.id.wd_zl_text_upload);

        myMainProd = F(R.id.textView8);
        textXb = F(R.id.wd_zl_text_xb);
        textviewPh = F(R.id.wd_zl_text_ph);
        textviewNum = F(R.id.wd_zl_text_num);
        textviewDzh = F(R.id.wd_zl_text_dzh);
        textviewZhy = F(R.id.wd_zl_text_zhy);
        textviewAddress = F(R.id.wd_zl_text_address);
        textviewCompany = F(R.id.wd_zl_text_company);
        textviewProduct = F(R.id.wd_zl_text_products);

        llSex = F(R.id.setting_data_sex);
        llMode = F(R.id.wd_zl_linear_ph);
        llPro = F(R.id.setting_data_product);
        llAdd = F(R.id.setting_data_address);
        llRegion = F(R.id.setting_data_region);
        llProMonth = F(R.id.linear_show_close);
        llMainsell = F(R.id.setting_data_mainsell);
        llMothonuse = F(R.id.setting_data_monthlyuse);

        if (isFromContact) {  //从通讯录跳转
            setTitle("个人资料");
            getPersonInfo();
        } else {
            requestNetData();
            setTitle("个人信息");

            //注册广播；
            myReceiver = new MyReceiver();
            mFilter = new IntentFilter(ACTION);
            registerReceiver(myReceiver, mFilter);

            llSex.setOnClickListener(this);
            llAdd.setOnClickListener(this);
            llPro.setOnClickListener(this);
            llMode.setOnClickListener(this);
            image_tx.setOnClickListener(this);
            llRegion.setOnClickListener(this);
            imageShch.setOnClickListener(this);
            llMainsell.setOnClickListener(this);
            llProMonth.setOnClickListener(this);
            llMothonuse.setOnClickListener(this);

            cardMore.setVisibility(View.VISIBLE);
            headMore.setVisibility(View.VISIBLE);
            textXb.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_more_small, 0);
            textviewPh.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_more_small, 0);
            textviewNum.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_more_small, 0);
            textviewZhy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_more_small, 0);
            textviewDzh.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_more_small, 0);
            textviewAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_more_small, 0);
            textviewProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_more_small, 0);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wd_zl_text_headimg:
                Intent intent1 = new Intent(this, TakePhotoDialogActivity.class);
                intent1.putExtra("type", "1");
                startActivityForResult(intent1, 100);
                break;
            case R.id.wd_zl_text_upload:
                Intent intent2 = new Intent(this, TakePhotoDialogActivity.class);
                intent2.putExtra("type", "2");
                startActivityForResult(intent2, 200);
                break;
            case R.id.setting_data_sex:
                Intent intent3 = new Intent(this, DataCommonActivity.class);
                intent3.putExtra("type", "0");
                intent3.putExtra("title", "性别");
                intent3.putExtra("hint", sexInPut);
                startActivity(intent3);
                break;
            case R.id.setting_data_region:
                Intent intent4 = new Intent(this, DataCommonActivity.class);
                intent4.putExtra("type", "1");
                intent4.putExtra("title", "所属地区");
                intent4.putExtra("hint", regionPosition + "");
                startActivity(intent4);
                break;

            case R.id.setting_data_address:
                Intent intent5 = new Intent(this, AddressSelectedActivity.class);
                intent5.putExtra("type", "3");
                intent5.putExtra("title", "地址");
                intent5.putExtra("address", address);
                intent5.putExtra("addressId", addressId);
                startActivity(intent5);
                break;
            case R.id.setting_data_product:
                Intent intent6 = new Intent(this, DataCommonActivity.class);
                intent6.putExtra("type", "2");
                intent6.putExtra("title", "生产产品");
                intent6.putExtra("hint", product);
                startActivityForResult(intent6, 6);
                break;
            case R.id.setting_data_monthlyuse:
                Intent intent7 = new Intent(this, DataCommonActivity.class);
                intent7.putExtra("type", "2");
                intent7.putExtra("title", "月用量");
                intent7.putExtra("hint", monthUse);
                startActivityForResult(intent7, 7);
                break;
            case R.id.setting_data_mainsell:
                Intent intent8 = new Intent(this, ("4".equals(type))
                        ? (MyMainPro_LogisticsActivity.class)
                        : (DataCommonActivity.class));
                intent8.putExtra("title", (type.equals("1"))
                        ? ("我的需求")
                        : ("我的主营"));
                intent8.putExtra("type", "2");
                intent8.putExtra("hint", mainPro);
                startActivityForResult(intent8, 8);
                break;
            case R.id.wd_zl_linear_ph:
                Intent intent9 = new Intent(this, DataCommonActivity.class);
                intent9.putExtra("type", "2");
                intent9.putExtra("title", "关注的牌号");
                intent9.putExtra("hint", model);
                startActivityForResult(intent9, 9);
                break;
            default:
                break;
        }
    }

    //保存资料。。。
    public void saveData() {
        try {
            Map map = new HashMap();
            String sMainPro = mainPro
                    .replace("  ", ",")
                    .replace(" ", ",")
                    .replace("，", ",")
                    .replace("|", ",")
                    .replace("、", ",")
                    .replace("/", ",")
                    .replace("。", ",")
                    .replace(".", ",");
            String sModel = model
                    .replace("  ", ",")
                    .replace(" ", ",")
                    .replace("，", ",")
                    .replace("|", ",")
                    .replace("、", ",")
                    .replace("/", ",")
                    .replace("。", ",")
                    .replace(".", ",");

            if (type.equals("1")) {
                map.put("month_consum", monthUse);
                map.put("main_product", product);
            }
            map.put("type", type);
            map.put("sex", sexInPut);
            map.put("concern", sModel);
            map.put("major", sMainPro);
            map.put("address", address);
            map.put("address_id", addressId);
            map.put("dist", regionInPut);
            map.put("token", sharedUtils.getData(this, "token"));
            saveSelfInfo(API.SAVE_SELFINFO, map, 3);
        } catch (Exception e) {
        }
    }

    public void requestNetData() {
        Map<String, String> map = new HashMap<>();
        map.put("token", sharedUtils.getData(this, "token"));
        String url = API.BASEURL + API.GET_SELF_INFO;
        postAsyn(this, url, map, this, 1);
    }

    public void getPersonInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", getIntent().getStringExtra("userid"));
        String url = API.BASEURL + API.GETINFORMATION;
        postAsyn(this, url, map, this, 1);
    }

    public void saveSelfInfo(String method, Map map, int type) {
        String url = API.BASEURL + method;
        postAsyn1(this, url, map, this, type, false);
    }

    public void upLoadImg(String method, String imgpath, int type) {
        Map<String, String> map = new HashMap<>();
        String token = sharedUtils.getData(this, "token");
        postUpLoadIMG(this, API.BASEURL + method, imgpath, token, this, type);
    }


    @Override
    public void callBack(Object object, int type) {
        try {
            String err = new JSONObject(object.toString()).getString("err");
            if (type == 1) {
                mySelfInfo = null;
                if (err.equals("0")) {
                    Gson gson = new Gson();
                    mySelfInfo = gson.fromJson(object.toString(), MySelfInfo.class);
                    showInfo(mySelfInfo);
                }
            }
            if (type == 3 && err.equals("0")) {
                requestNetData();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void failCallBack(int type) {
    }

    public void showInfo(MySelfInfo mySelfInfo) {
        try {
            type = mySelfInfo.getData().getType();
            sex = mySelfInfo.getData().getSex() + "  ";
            region = mySelfInfo.getData().getAdistinct();
            address = mySelfInfo.getData().getAddress() + "  ";
            addressId = mySelfInfo.getData().getOrigin() + "  ";
            model = mySelfInfo.getData().getConcern_model() + "  ";
            product = mySelfInfo.getData().getMain_product() + "  ";
            mainPro = mySelfInfo.getData().getNeed_product() + "  ";
            monthUse = mySelfInfo.getData().getMonth_consum() + "  ";

            sexInPut = ("男".equals(sex)) ? ("0") : ("1");

            textXb.setText(sex);
            textviewPh.setText(model);
            textviewDzh.setText(address);
            textviewZhy.setText(mainPro);
            textviewAddress.setText(region);
            textGs.setText(mySelfInfo.getData().getC_name() + "  ");
            textDh.setText(mySelfInfo.getData().getMobile() + "  ");
            Glide.with(this)
                    .load(mySelfInfo.getData().getThumbcard())
                    .placeholder(R.drawable.card)
                    .into(imageShch);
            Glide.with(this)
                    .load(mySelfInfo.getData().getThumb())
                    .placeholder(R.drawable.contact_image_defaul_male)
                    .into(image_tx);

            switch (type) {
                case "1":
                    llMode.setVisibility(View.VISIBLE);       //"‘关注的牌号’是否显示"
                    llProMonth.setVisibility(View.VISIBLE); //"‘月用量与生产产品’是否显示"
                    textviewCompany.setText("塑料制品厂  ");
                    myMainProd.setText("我的需求：");
                    textviewProduct.setText(product);
                    textviewNum.setText(monthUse);
                    break;
                case "2":
                    llMode.setVisibility(View.VISIBLE);
                    textviewCompany.setText("原料供应商  ");
                    llProMonth.setVisibility(View.GONE);
                    break;
                case "4":
                    llMode.setVisibility(View.GONE);
                    llProMonth.setVisibility(View.GONE);
                    myMainProd.setText("我的主营：");
                    textviewCompany.setText("物流商  ");
                    break;
                case "5":
                    llMode.setVisibility(View.GONE);
                    llProMonth.setVisibility(View.GONE);
                    textviewCompany.setText("金融公司  ");
                    break;
                case "6":
                    llMode.setVisibility(View.GONE);
                    llProMonth.setVisibility(View.GONE);
                    textviewCompany.setText("塑化电商  ");
                    break;
                case "7":
                    llMode.setVisibility(View.GONE);
                    llProMonth.setVisibility(View.GONE);
                    textviewCompany.setText("回料(含新材料)  ");
                    break;
                case "8":
                    llMode.setVisibility(View.GONE);
                    llProMonth.setVisibility(View.GONE);
                    textviewCompany.setText("期货  ");
                    break;
                case "9":
                    textviewCompany.setText("塑机  ");
                    llMode.setVisibility(View.GONE);
                    llProMonth.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            //设置地区
            switch (region) {
                case "华东":
                    regionPosition = 0;
                    regionInPut = "EC";
                    break;
                case "华南":
                    regionPosition = 1;
                    regionInPut = "SC";
                    break;
                case "华北":
                    regionPosition = 2;
                    regionInPut = "NC";
                    break;
                case "其他":
                    regionPosition = 3;
                    regionInPut = "OT";
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
        }
    }

    //选择照片返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == 1) {
            String imagePath = data.getStringExtra("img_url");
            Glide.with(this).load(imagePath).into(image_tx);
            upLoadImg(API.SAVE_PIC_TO_SERVER, imagePath, 5);
        }
        if (requestCode == 200 && resultCode == 2) {
            String imagePath = data.getStringExtra("img_url");
            Glide.with(this).load(imagePath).into(imageShch);
            upLoadImg(API.SAVE_CARD_IMG, imagePath, 6);
        }
        if (requestCode == 6 && data != null) {
            if (!product.equals(data.getStringExtra("updateData"))) {
                product = data.getStringExtra("updateData");
                saveData();
            }
        }
        if (requestCode == 7 && data != null) {
            if (!monthUse.equals(data.getStringExtra("updateData"))) {
                monthUse = data.getStringExtra("updateData");
                saveData();
            }
        }
        if (requestCode == 8 && data != null) {
            if (!mainPro.equals(data.getStringExtra("updateData"))) {
                mainPro = data.getStringExtra("updateData");
                saveData();
            }
        }
        if (requestCode == 9 && data != null) {
            if (!model.equals(data.getStringExtra("updateData"))) {
                model = data.getStringExtra("updateData");
                saveData();
            }
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION)) {
                String type = intent.getStringExtra("type");
                String data = intent.getStringExtra("updateData");
                if (type.equals("0")) {
                    if (!sexInPut.equals(data)) {
                        sexInPut = data;
                        saveData();
                    }
                } else if (type.equals("1")) {
                    if (!regionInPut.equals(data)) {
                        regionInPut = data;
                        saveData();
                    }
                } else {
                    address = data;
                    addressId = intent.getStringExtra("addressId");
                    saveData();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
    }
}
