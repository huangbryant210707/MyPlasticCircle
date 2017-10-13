package com.myplas.q.release;

import android.support.v4.view.ViewPager;

/**
 * 作者:huangshuang
 * 事件 2017/10/12 0012.
 * 邮箱： 15378412400@163.com
 */

public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
    private OnPageChangeListener onPageChangeListener;

    public MyOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface OnPageChangeListener {
        void onPageSelected(int position);
    }
}
