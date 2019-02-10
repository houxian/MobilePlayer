package com.android.mobileplayer.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.android.mobileplayer.base.BasePager;
import com.android.mobileplayer.utils.LogUtil;

/**
 * NetVideoPager by houxianyong
 */
public class NetVideoPager extends BasePager {

    private TextView textView;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("网络视频页面被初始化");
        textView = new TextView(context);
        textView.setText("");
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频页面数据被初始化");
        textView.setText("网络视频页面");
    }
}
