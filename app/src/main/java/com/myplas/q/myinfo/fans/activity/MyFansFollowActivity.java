package com.myplas.q.myinfo.fans.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.myplas.q.R;
import com.myplas.q.addresslist.Beans.MyFansBean;
import com.myplas.q.addresslist.adapter.MyFansFollowAdapter;
import com.myplas.q.common.utils.DialogShowUtils;
import com.myplas.q.guide.activity.BaseActivity;
import com.myplas.q.common.netresquset.ResultCallBack;
import com.myplas.q.common.utils.SharedUtils;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.common.view.XListView;
import com.myplas.q.myinfo.integral.activity.IntegralPayActivtity;
import com.myplas.q.myinfo.fans.adapter.MyFollowAdapter;
import com.myplas.q.common.api.API;
import com.myplas.q.myinfo.beans.MyFollowBean;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编写： 黄双
 * 电话：15378412400
 * 邮箱：15378412400@163.com
 * 时间：2017/3/20 22:15
 */
public class MyFansFollowActivity extends BaseActivity implements ResultCallBack,
        XListView.IXListViewListener, DialogShowUtils.DialogShowInterface {
    private List<MyFansBean.DataBean> list;
    private List<MyFollowBean.DataBean> list1;
    private List<MyFansBean.DataBean> list_more;
    private List<MyFollowBean.DataBean> list1_more;
    private XListView listView;
    private MyFansFollowAdapter wdgz_adapter;
    private MyFollowAdapter myFollowAdapter;
    private SharedUtils sharedUtils;
    private TextView textView_title;
    private Dialog normalDialog = null;
    private String type = "1", user_id, id_;
    private int page = 1, visibleItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.txl_wdgz_activity);
        goBack(findViewById(R.id.back));
        sharedUtils = SharedUtils.getSharedUtils();
        listView = (XListView) findViewById(R.id.wdgz_listview);
        listView.setPullLoadEnable(true);
        listView.setPullRefreshEnable(false);
        listView.setXListViewListener(this);
        list1_more = new ArrayList<>();
        list_more = new ArrayList<>();
        textView_title = (TextView) findViewById(R.id.title_name);
        textView_title.setText(getIntent().getStringExtra("titlename"));
        type = getIntent().getStringExtra("type");
        //关注、粉丝
        if (TextUtils.isNullOrEmpty(type)) {
            getMyFans("1");
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(MyFansFollowActivity.this, PersonInfoActivity.class);
                    if (type.equals("1")) {
                        user_id = ((list_more.size() == 0) ? (list) : (list_more)).get(position - 1).getUser_id().getUser_id();
                        id_ = ((list_more.size() == 0) ? (list) : (list_more)).get(position - 1).getFocused_id();
                        intent.putExtra("id", id_);
                    }
                    if (type.equals("2")) {
                        user_id = ((list1_more.size() == 0) ? (list1) : (list1_more)).get(position - 1).getFocused_id().getUser_id();
                        id_ = ((list1_more.size() == 0) ? (list1) : (list1_more)).get(position - 1).getFocused_id().getUser_id();
                        intent.putExtra("id", id_);
                    }
                    //判断是否消耗积分
                    getPersonInfoData(user_id, "1", 5);
                } catch (Exception e) {
                }
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && listView.getCount() > visibleItemCount) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        page++;
                        getMyFans(String.valueOf(page));
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                MyFansFollowActivity.this.visibleItemCount = visibleItemCount;
            }
        });
    }

    public void getMyFans(String page) {
        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("token", sharedUtils.getData(this, "token"));
            map.put("page", page);
            map.put("type", type);
            map.put("size", "10");
            postAsyn(this, API.BASEURL + API.GET_MY_FUNS, map, this, Integer.parseInt(type));
        } catch (Exception e) {
        }
    }

    public void getPersonInfoData(String userid, String showtype, int type) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("token", sharedUtils.getData(this, "token"));
        map.put("user_id", userid);
        map.put("showType", showtype);
        String url = API.BASEURL + API.GET_ZONE_FRIEND;
        postAsyn(this, url, map, this, 5);
    }

    @Override
    public void callBack(Object object, int type) {
        try {
            Gson gson = new Gson();
            String err = new JSONObject(object.toString()).getString("err");
            if (type == 1) {
                MyFansBean wdgzBean = null;
                if (err.equals("0")) {
                    wdgzBean = gson.fromJson(object.toString(), MyFansBean.class);
                    list = wdgzBean.getData();
                    if (page == 1) {
                        wdgz_adapter = new MyFansFollowAdapter(this, list);
                        listView.setAdapter(wdgz_adapter);
                        listView.stopRefresh();
                        list_more.clear();
                        list_more.addAll(list);
                    } else {
                        if (list != null && list.size() != 0) {
                            listView.stopLoadMore();
                            list_more.addAll(list);
                            wdgz_adapter.setList(list_more);
                            wdgz_adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    TextUtils.Toast(this, new JSONObject(object.toString()).getString("msg"));
                }
            } else if (type == 2) {
                MyFollowBean wdgzBean = null;
                if (err.equals("0")) {
                    wdgzBean = gson.fromJson(object.toString(), MyFollowBean.class);
                    list1 = wdgzBean.getData();
                    if (page == 1) {
                        myFollowAdapter = new MyFollowAdapter(this, list1);
                        listView.setAdapter(myFollowAdapter);
                        listView.stopRefresh();
                        list1_more.clear();
                        list1_more.addAll(list1);
                    } else {
                        if (list1 != null && list1.size() != 0) {
                            listView.stopLoadMore();
                            list1_more.addAll(list1);
                            myFollowAdapter.setList(list1_more);
                            myFollowAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    TextUtils.Toast(this, new JSONObject(object.toString()).getString("msg"));
                }
            }
            //是否消耗积分//
            if (type == 5 && err.equals("99")) {
                String content = new JSONObject(object.toString()).getString("msg");
                DialogShowUtils dialogShowUtils = new DialogShowUtils();
                dialogShowUtils.showDialog(this, content, 1, this);
            }
            //已经消耗积分
            if (type == 5 && err.equals("0")) {
                Intent intent = new Intent(this, PersonInfoActivity.class);
                intent.putExtra("userid", user_id);
                intent.putExtra("id", user_id);
                startActivity(intent);
            }
            //减积分成功
            if (type == 3 && err.equals("0")) {
                Intent intent = new Intent(this, PersonInfoActivity.class);
                intent.putExtra("id", id_);
                intent.putExtra("userid", user_id);
                startActivity(intent);
            }
            //积分不足
            if (type == 3 && !err.equals("0")) {
                String content = new JSONObject(object.toString()).getString("msg");
                DialogShowUtils dialogShowUtils = new DialogShowUtils();
                dialogShowUtils.showDialog(this, content, (err.equals("100")) ? (2) : (3), this);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void failCallBack(int type) {

    }

    @Override
    public void ok(int type) {
        switch (type) {
            case 1:
                getPersonInfoData(user_id, "5", 3);
                break;
            case 2:
                startActivity(new Intent(this, IntegralPayActivtity.class));
                break;
        }
    }

    //刷新
    @Override
    public void onRefresh() {
        page = 1;
        getMyFans(String.valueOf(page));
    }

    //加载
    @Override
    public void onLoadMore() {

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