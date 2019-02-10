package com.android.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * 基类
 */
public abstract class BasePager {

    public  final Context context;
    public View rootview;
    public  boolean isInitData;

    public BasePager(Context context) {
        this.context = context;
         rootview = initView();
    }

    /**
     *
     * @return
     */
    public abstract  View initView();

    public void initData(){ }
}
