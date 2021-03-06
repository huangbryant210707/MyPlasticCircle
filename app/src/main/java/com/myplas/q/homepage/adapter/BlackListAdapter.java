package com.myplas.q.homepage.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myplas.q.R;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.common.view.CommonDialog;
import com.myplas.q.homepage.activity.BlackListDetailActivity;
import com.myplas.q.homepage.beans.BlackListsBean;

import java.util.List;

/**
 * @author 黄双
 * @date 2018/1/11 0011
 */

public class BlackListAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<BlackListsBean.BlacklistsBean> blacklists;
    private CommonDialog.DialogShowInterface listener;

    public BlackListAdapter(Context context, List<BlackListsBean.BlacklistsBean> blacklists) {
        this.context = context;
        this.blacklists = blacklists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout_blacklist, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        view.setTag(viewType);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.tvNumber.setText(blacklists.get(position).getPv());
            viewHolder.tvTitle.setText(blacklists.get(position).getSubject());
            viewHolder.tvTime.setText(blacklists.get(position).getCreated_at());

            Glide.with(context)
                    .load(blacklists.get(position).getIllustration())
                    .placeholder(R.drawable.ic_waiting)
                    .into(viewHolder.imageView);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return blacklists != null ? blacklists.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setList(List<BlackListsBean.BlacklistsBean> list) {
        this.blacklists = list;
    }

    public void setlistener(CommonDialog.DialogShowInterface listener) {
        this.listener = listener;
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        private ImageView imageView;
        private TextView tvTitle, tvTime, tvNumber;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.blacklist_img);
            tvTitle = (TextView) itemView.findViewById(R.id.blacklist_title);
            tvNumber = (TextView) itemView.findViewById(R.id.blacklist_num);
            tvTime = (TextView) itemView.findViewById(R.id.blacklist_time);
        }

        @Override
        public void onClick(View v) {
            if (TextUtils.isLogin(context, listener)) {
                int position = (int) v.getTag();
                Intent intent = new Intent(context, BlackListDetailActivity.class);
                intent.putExtra("id", blacklists.get(position).getId());
                context.startActivity(intent);
            }
        }
    }
}
