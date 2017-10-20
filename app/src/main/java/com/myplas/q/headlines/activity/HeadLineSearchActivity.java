package com.myplas.q.headlines.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.myplas.q.R;
import com.myplas.q.common.api.API;
import com.myplas.q.common.netresquset.ResultCallBack;
import com.myplas.q.common.view.CommonDialog;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.common.view.MyGridview;
import com.myplas.q.common.view.RefreshPopou;
import com.myplas.q.guide.activity.BaseActivity;
import com.myplas.q.headlines.adapter.HeadSearch_LV_Adapter;
import com.myplas.q.headlines.bean.HeadSearchBean;
import com.myplas.q.myself.integral.activity.IntegralActivity;
import com.myplas.q.supdem.beans.HistoryBean;
import com.myplas.q.headlines.bean.SearchNoResultBean;
import com.myplas.q.supdem.adapter.SupDem_Search_Grid_Adapter;
import com.optimus.edittextfield.EditTextField;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 编写： 黄双
 * 电话：15378412400
 * 邮箱：15378412400@163.com
 * 时间：2017/3/19 15:44
 */

public class HeadLineSearchActivity extends BaseActivity implements View.OnClickListener
        , ResultCallBack
        , AdapterView.OnItemClickListener
        , CommonDialog.DialogShowInterface {

    private ListView listView;
    private ImageView imageView;
    private EditTextField editText;
    private RefreshPopou mRefreshPopou;
    private TextView textView, textView_no;
    private MyGridview mGV_History, mGV_Empty, mGV_Subcribe;
    private LinearLayout mLayoutDefault, mLayoutResult, mLayoutEmpty;

    private HistoryBean historyBean;
    private SearchNoResultBean bean;
    private List<HeadSearchBean.DataBean> list;
    private HeadSearch_LV_Adapter mSearchLvAdapter;
    private SupDem_Search_Grid_Adapter mSearchGridAdapter;

    private String keywords;
    private int page, visibleItemCount, position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_headline_search);
        goBack(findViewById(R.id.back));
        initView();
        String data = getIntent().getStringExtra("data");
        if (TextUtils.isNullOrEmpty(data)) {//从供求qq页面跳转过来
            editText.setText(data);
            get_Subscribe(1, data, true);
            editText.setSelection(data.length());
        } else {                            //从头条跳转过来
            getSearch_Record();
        }
    }

    public void initView() {
        page = 1;
        keywords = "7000F";
        list = new ArrayList<>();
        mRefreshPopou = new RefreshPopou(this, 1);

        imageView = F(R.id.img_search_delete);
        mGV_Empty = F(R.id.mygrid_search_null);
        listView = F(R.id.search_listview_result1);
        textView = F(R.id.supplydemand_btn_search);
        editText = F(R.id.supplydemand_search_edit);
        mGV_History = F(R.id.mygrid_search_history);
        mLayoutResult = F(R.id.search_result_linear);
        mGV_Subcribe = F(R.id.mygrid_search_subcribe);
        textView_no = F(R.id.search_result_text_null);
        mLayoutDefault = F(R.id.search_default_linear);
        mLayoutEmpty = F(R.id.search_result_linear_null);

        textView.setOnClickListener(this);
        imageView.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        mGV_Empty.setOnItemClickListener(this);
        mGV_History.setOnItemClickListener(this);
        mGV_Subcribe.setOnItemClickListener(this);

        editText.setHintTextColor(getResources().getColor(R.color.color_gray));
        editText.setPadding(20, 0, 0, 0);


        //edittext的回车监听
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEARCH | (arg2 != null && arg2.getAction() == KeyEvent.ACTION_DOWN)) {
                    page = 1;
                    mRefreshPopou.setCanShowPopou(true);
                    mLayoutDefault.setVisibility(View.GONE);
                    mLayoutResult.setVisibility(View.VISIBLE);
                    keywords = (editText.getText().toString().equals("")) ? ("7000f") : (editText.getText().toString());
                    get_Subscribe(page, keywords, true);
                    return true;
                }
                return false;
            }
        });
        //加载更多
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && listView.getCount() >= visibleItemCount) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        page++;
                        get_Subscribe(page, keywords, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                HeadLineSearchActivity.this.visibleItemCount = visibleItemCount;
            }
        });
    }

    //获取历史搜索
    public void getSearch_Record() {
        Map map = new HashMap();
        map.put("keywords", "");
        postAsyn(this, API.BASEURL + API.TOUTIAO_SEARCH_LOG, map, this, 1);
    }

    //查询
    public void get_Subscribe(int page, String keywords, boolean isShowLoading) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", page + "");
        map.put("size", "15");
        map.put("keywords", keywords);
        map.put("subscribe", "1");
        postAsyn1(this, API.BASEURL + API.GET_SUBSCRIBE, map, this, 2, isShowLoading);
    }


    //删除搜索历史记录
    public void delSearch_Record() {
        postAsyn(this, API.BASEURL + API.DEL_TOUTIAO_SEARCH_LOG, null, this, 3);
    }

    //检查权限
    public void isPaidSubscription(String cate_id) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", cate_id);
        BaseActivity.postAsyn(this, API.BASEURL + API.IS_PAID_SUBSCRIPTION, map, this, 4);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.supplydemand_btn_search:
                page = 1;
                mRefreshPopou.setCanShowPopou(true);
                mLayoutDefault.setVisibility(View.GONE);
                mLayoutResult.setVisibility(View.VISIBLE);
                keywords = (editText.getText().toString().equals(""))
                        ? ("7000f")
                        : (editText.getText().toString());
                get_Subscribe(page, keywords, true);
                break;
            case R.id.img_search_delete:
                delSearch_Record();
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.mygrid_search_history://历史搜索
                keywords = historyBean.getHistory().get(position);
                getData(keywords);
                break;
            case R.id.mygrid_search_subcribe://猜你所想
                keywords = historyBean.getRecommend().get(position);
                getData(keywords);
                break;
            case R.id.mygrid_search_null://相关搜索
                keywords = bean.getRecommendation().get(position);
                getData(keywords);
                break;
            case R.id.search_listview_result1:
                HeadLineSearchActivity.this.position = position;
                isPaidSubscription(list.get(position).getId());
                break;
        }
    }

    private void getData(String keywords) {
        page = 1;
        mRefreshPopou.setCanShowPopou(true);
        mLayoutDefault.setVisibility(View.GONE);
        mLayoutResult.setVisibility(View.VISIBLE);

        editText.setText(keywords);
        editText.setSelection(keywords.length());
        get_Subscribe(page, keywords, true);
    }

    private void showInPutKeybord() {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }

        }, 200);
    }

    @Override
    public void callBack(Object object, int type) {
        try {
            Gson gson = new Gson();
            String err = new JSONObject(object.toString()).getString("err");
            if (type == 1 && err.equals("0")) {
                historyBean = gson.fromJson(object.toString(), HistoryBean.class);
                mSearchGridAdapter = new SupDem_Search_Grid_Adapter(this, historyBean.getHistory());
                mGV_History.setAdapter(mSearchGridAdapter);
                SupDem_Search_Grid_Adapter adapter_grid1 = new SupDem_Search_Grid_Adapter(this, historyBean.getRecommend());
                mGV_Subcribe.setAdapter(adapter_grid1);

//                keywords = historyBean.getHot_search().getContent();
//                editText.setHint(keywords);
            }
            if (type == 2) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if (err.equals("0")) {
                    HeadSearchBean searchBean = gson.fromJson(object.toString(), HeadSearchBean.class);
                    if (page == 1) {
                        listView.setVisibility(View.VISIBLE);
                        mLayoutEmpty.setVisibility(View.GONE);
                        mSearchLvAdapter = new HeadSearch_LV_Adapter(this, searchBean.getData());
                        listView.setAdapter(mSearchLvAdapter);

                        list.clear();
                        list.addAll(searchBean.getData());
                        mRefreshPopou.show(F(R.id.divider_result), "为你搜索" + searchBean.getTotal() + "条信息");
                    } else {
                        list.addAll(searchBean.getData());
                        mSearchLvAdapter.setList(list);
                        mSearchLvAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (page == 1) {
                        listView.setVisibility(View.GONE);
                        mLayoutEmpty.setVisibility(View.VISIBLE);
                        textView_no.setText("抱歉，未能找到相关搜索！");
                        bean = gson.fromJson(object.toString(), SearchNoResultBean.class);
                        mSearchGridAdapter = new SupDem_Search_Grid_Adapter(this, bean.getRecommendation());
                        mGV_Empty.setAdapter(mSearchGridAdapter);
                    } else {
                        TextUtils.Toast(this, "没有更多数据了！");
                    }
                }
            }
            if (type == 3 && err.equals("0")) {
                TextUtils.Toast(this, "删除成功！");
                mSearchGridAdapter = new SupDem_Search_Grid_Adapter(this, null);
                mGV_History.setAdapter(mSearchGridAdapter);
            }
            if (type == 4) {
                if (err.equals("0")) {
                    Intent intent = new Intent(this, HeadLinesDetailActivity.class);
                    intent.putExtra("id", list.get(position).getId());
                    startActivity(intent);
                } else {
                    String content = new JSONObject(object.toString()).getString("msg");
                    CommonDialog commonDialog = new CommonDialog();
                    commonDialog.showDialog(this, content, (err.equals("2")) ? (1) : (3), this);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void ok(int type) {
        Intent intent = new Intent(this, IntegralActivity.class);
        intent.putExtra("type", "0");
        startActivity(intent);
    }

    @Override
    public void failCallBack(int type) {
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }
}
