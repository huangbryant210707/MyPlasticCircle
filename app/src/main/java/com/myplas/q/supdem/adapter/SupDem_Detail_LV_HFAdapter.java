package com.myplas.q.supdem.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myplas.q.R;
import com.myplas.q.common.view.MyListview;
import com.myplas.q.supdem.BaseInterFace;
import com.myplas.q.supdem.Beans.ReplyBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编写： 黄双
 * 电话：15378412400
 * 邮箱：15378412400@163.com
 * 时间：2017/3/20 17:12
 */
public class SupDem_Detail_LV_HFAdapter extends RecyclerView.Adapter {
    Context context;
    BaseInterFace mBaseInterFace;
    List<ReplyBean.DataBean> list;
    Map<Integer, viewHolder> mHolderMap;
    SupDem_Detail_LV_HF_Child_Adapter mChildAdapter;

    public SupDem_Detail_LV_HFAdapter(Context context, BaseInterFace baseInterFace) {
        this.context = context;
        mHolderMap = new HashMap<>();
        this.mBaseInterFace = baseInterFace;
    }

    public void setList(List<ReplyBean.DataBean> list) {
        this.list = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout_supdem_lv_hf, parent, false);
        viewHolder viewHolder = new viewHolder(view, viewType);
        viewHolder.setIsRecyclable(false);
        mHolderMap.put(viewType, viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            viewHolder viewHolder = mHolderMap.get(position);
            viewHolder.mTVContent.setText(list.get(position).getContent());
            viewHolder.mTVTime.setText(list.get(position).getInput_time());
            viewHolder.mTVCompany.setText(list.get(position).getC_name()
                    + "  " + list.get(position).getName());

            Glide.with(context)
                    .load(list.get(position).getThumb())
                    .placeholder(R.drawable.contact_image_defaul_female)
                    .into(viewHolder.mIVHead);
            viewHolder.mIVStart.setImageResource(list.get(position).getIs_pass().equals("0")
                    ? R.drawable.icon_identity
                    : R.drawable.icon_identity_hl);

            List<ReplyBean.DataBean.ReplayBean> childList = list.get(position).getReplay();

            if (childList != null && childList.size() != 0) {
                mChildAdapter.setList(childList);
                viewHolder.mRecyclerView.setAdapter(mChildAdapter);
            }
            viewHolder.mIVCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call(list.get(position).getMobile());
                }
            });

            /*一级回复的点击事件*/
            viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBaseInterFace != null) {
                        mBaseInterFace.onItemClick("2"
                                , list.get(position).getName()
                                , list.get(position).getId()
                                , list.get(position).getPur_id()
                                , list.get(position).getUser_id());
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return (list != null) ? (list.size()) : (0);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void call(String tel) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    class viewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLayout;
        MyListview mRecyclerView;
        ImageView mIVHead, mIVStart, mIVCall;
        TextView mTVCompany, mTVContent, mTVTime;

        public viewHolder(View convertView, int position) {
            super(convertView);
            mTVTime = (TextView) convertView.findViewById(R.id.item_time);
            mIVCall = (ImageView) convertView.findViewById(R.id.item_call);
            mIVHead = (ImageView) convertView.findViewById(R.id.item_head);
            mLayout = (LinearLayout) convertView.findViewById(R.id.item_ll);
            mIVStart = (ImageView) convertView.findViewById(R.id.item_start);
            mTVContent = (TextView) convertView.findViewById(R.id.item_pirce);
            mTVCompany = (TextView) convertView.findViewById(R.id.item_cmpany);
            mRecyclerView = (MyListview) convertView.findViewById(R.id.item_rv);

            mChildAdapter = new SupDem_Detail_LV_HF_Child_Adapter(context, mBaseInterFace);
            List<ReplyBean.DataBean.ReplayBean> childList = list.get(position).getReplay();
//            LinearLayoutManager manager = new LinearLayoutManager(context);
//            mRecyclerView.setLayoutManager(manager);
        }
    }
}
