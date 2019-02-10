package com.android.mobileplayer.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.android.mobileplayer.base.BasePager;
import com.android.mobileplayer.utils.LogUtil;

/**
 * NetAudioPager by houxianyong
 */

public class NetAudioPager extends BasePager {

    private TextView textView;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("网络音乐页面被初始化");
        textView = new TextView(context);
        textView.setText("");
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音乐页面数据被初始化");
        textView.setText("网络音乐页面");
    }
}