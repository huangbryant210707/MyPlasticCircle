package com.myplas.q.homepage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.myplas.q.R;
import com.myplas.q.app.fragment.BaseFragment;
import com.myplas.q.common.api.API;
import com.myplas.q.common.net.ResultCallBack;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.common.view.CommonDialog;
import com.myplas.q.common.view.EmptyView;
import com.myplas.q.homepage.activity.BrokeNewsActivtiy;
import com.myplas.q.homepage.adapter.BlackListAdapter;
import com.myplas.q.homepage.beans.BlackListsBean;
import com.myplas.q.myself.login.LoginActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 黄双
 * @date 2018/1/11 0011
 */

public class FragmentHomePageBlackList extends BaseFragment implements View.OnClickListener
        , ResultCallBack
        , SwipeRefreshLayout.OnRefreshListener
        , CommonDialog.DialogShowInterface {

    private View view;
    private EmptyView emptyView;
    private ImageView mActionButton;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    private int page;
    private boolean isLoading;
    private BlackListAdapter mAdapter;
    private List<BlackListsBean.BlacklistsBean> list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = 1;
        list = new ArrayList<>();
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_layout_homepage_blacklist, null, false);
        emptyView = F(view, R.id.blacklist_emptyView);
        mRecyclerView = F(view, R.id.blacklist_listview);
        mActionButton = F(view, R.id.blacklist_floatingbtn);
        mRefreshLayout = F(view, R.id.blacklist_refreshlayout);

        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.color_red));

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);

        mActionButton.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);

        getBlackLists(page);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return view;
    }


    /**
     * 获取黑名单列表数据
     */
    public void getBlackLists(int page) {
        Map<String, String> map = new HashMap<>(16);
        map.put("page", page + "");
        map.put("size", "20");
        getAsyn(getActivity(), API.BLACKLISTS, map, this, 1, false);
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isLogin(getActivity(), this)) {
            Intent intent = new Intent(getActivity(), BrokeNewsActivtiy.class);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        getBlackLists(page);
    }

    @Override
    public void callBack(Object object, int type) {
        try {
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(object.toString());
            String code = jsonObject.getString("code");
            mRefreshLayout.setRefreshing(false);
            if ("0".equals(code)) {
                BlackListsBean bean = gson.fromJson(object.toString(), BlackListsBean.class);
                if (page == 1) {
                    emptyView.setVisibility(View.GONE);
                    mRefreshLayout.setVisibility(View.VISIBLE);

                    mAdapter = new BlackListAdapter(getActivity(), bean.getBlacklists());
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setlistener(this);

                    list.clear();
                    list.addAll(bean.getBlacklists());
                } else {
                    isLoading = false;
                    list.addAll(bean.getBlacklists());
                    mAdapter.setList(list);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void failCallBack(int type, String message, int httpCode) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (page == 1) {
                if (httpCode == 404) {
                    emptyView.setVisibility(View.VISIBLE);
                    mRefreshLayout.setVisibility(View.GONE);
                    emptyView.setNoMessageText("没有相关数据！");
                    emptyView.setMyManager(R.drawable.icon_intelligent_recommendation2);
                }
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
            if (isLoading) {
                isLoading = false;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserVisible(true);
    }

    /**
     * 监听是否对用户可见
     *
     * @param isVisibleToUser
     */
    public void setUserVisible(boolean isVisibleToUser) {
        if (mActionButton == null) {
            return;
        }
        if (isVisibleToUser) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mActionButton, "scaleX", 0f, 1f);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mActionButton, "scaleY", 0f, 1f);
            AnimatorSet set = new AnimatorSet();
            set.play(animator).with(animator1);
            set.setDuration(500);
            set.start();
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mActionButton, "scaleX", 1f, 0f);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mActionButton, "scaleY", 1f, 0f);
            AnimatorSet set = new AnimatorSet();
            set.play(animator).with(animator1);
            set.setDuration(500);
            set.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setUserVisible(false);
    }

    @Override
    public void dialogClick(int type) {
        if (type == 4) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(intent);
        }
    }
}
