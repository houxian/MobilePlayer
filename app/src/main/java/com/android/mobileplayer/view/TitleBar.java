package com.android.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.mobileplayer.R;

/**
 * 自定义标题栏
 */

public class TitleBar extends LinearLayout implements  View.OnClickListener{


    private View tv_search;
    private View rl_game;
    private View iv_record;
    private Context context;

    /**
     * 代码示例化中使用
     * @param context
     */
    public TitleBar(Context context) {
        super(context,null);
    }

    /**
     * 当布局文件使用该类的时候 ，Android
     * @param context
     * @param attrs
     */
    public TitleBar(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
         tv_search = getChildAt(1);
         rl_game =  getChildAt(2);
         iv_record = getChildAt(3);

        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search:
                Toast.makeText(context,"搜索",Toast.LENGTH_LONG).show();
                break;
            case R.id.rl_game:
                Toast.makeText(context,"游戏",Toast.LENGTH_LONG).show();
                break;
            case R.id.iv_record:
                Toast.makeText(context,"播放历史",Toast.LENGTH_LONG).show();
                break;

        }

    }
}
