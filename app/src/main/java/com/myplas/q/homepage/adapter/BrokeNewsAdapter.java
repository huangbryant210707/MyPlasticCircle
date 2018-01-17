package com.myplas.q.homepage.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.myplas.q.R;
import com.myplas.q.common.utils.TextUtils;
import com.myplas.q.common.view.ProgressImageView;
import com.yanzhenjie.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄双
 * @date 2018/1/17 0017
 */

public class BrokeNewsAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity context;
    private List<String> list;
    private SparseArray<View> sparseArray;

    private int color;

    public BrokeNewsAdapter(Activity context, List<String> list) {
        this.list = list;
        this.context = context;
        sparseArray = new SparseArray<>();
        color = ContextCompat.getColor(context, R.color.color_red);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (sparseArray.get(position) == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_brokenews_layout, parent, false);
            viewHolder.imageView = (ProgressImageView) convertView.findViewById(R.id.item_img);

            convertView.setTag(viewHolder);
            sparseArray.put(position, convertView);
        } else {
            convertView = sparseArray.get(position);
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == list.size()) {
            viewHolder.imageView.setImageResource(R.drawable.upload);
            viewHolder.imageView.setOnClickListener(this);
        } else {
            viewHolder.imageView.setUseProgress(true);
            Glide.with(context).load(list.get(position)).into(viewHolder.imageView);
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (list.size() < 10) {
            Album.album(context)
                    .toolBarColor(color)
                    .statusBarColor(color)
                    .title("选取图片")
                    .columnCount(3)
                    .camera(true)
                    .selectCount(10 - list.size())
                    .start(100);
        } else {
            TextUtils.toast(context, "最多只能上传10张图片！");
        }
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    /**
     * 改变上传进度
     *
     * @param type
     * @param value
     */
    public void setProgress(int type, int value) {
        if (sparseArray.get(type) != null) {
            ViewHolder viewHolder = (ViewHolder) sparseArray.get(type).getTag();
            viewHolder.imageView.setProgress(value);
        }
    }

    class ViewHolder {
        ProgressImageView imageView;
    }
}