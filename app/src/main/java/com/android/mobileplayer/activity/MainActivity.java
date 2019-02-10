package com.android.mobileplayer.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import com.android.mobileplayer.R;
import com.android.mobileplayer.base.BasePager;
import com.android.mobileplayer.fragment.ReplaceFragment;
import com.android.mobileplayer.pager.AudioPager;
import com.android.mobileplayer.pager.NetAudioPager;
import com.android.mobileplayer.pager.NetVideoPager;
import com.android.mobileplayer.pager.VideoPager;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private FrameLayout fl_main_content;
    private RadioGroup radioGroup;

    private ArrayList<BasePager> basePagers;
    private int nposition =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        radioGroup = (RadioGroup)findViewById(R.id.rg_bottom_tag);


        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));
        basePagers.add(new AudioPager(this));
        basePagers.add(new NetAudioPager(this));
        basePagers.add(new NetVideoPager(this));
        //设置 radioGroup 的checked 监听
        radioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        radioGroup.check(R.id.rb_video);
        applypermission();

    }

    class MyOnCheckedChangeListener implements  RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_video:
                    nposition = 0;
                    break;
                case R.id.rb_net_video:
                    nposition = 2;
                    break;
                case R.id.rb_audio:
                    nposition = 1;
                    break;
                case R.id.rb_netaudio:
                    nposition = 3;
                    break;
                default:
                    nposition = 0;
            }
           setFragment();

        }
    }

    private void setFragment() {
        //1.得到FragmentManger
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3.替换
        ft.replace(R.id.fl_main_content,new ReplaceFragment(getBasePager()));
        //4.提交事务
        ft.commit();
    }

    /**
     *
     * @return
     */
    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(nposition);
        if(basePager!=null&&!basePager.isInitData){
            basePager.initData();
            basePager.isInitData = true;
        }
        return  basePager;
    }
    public void applypermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //检查是否已经给了权限
            int checkpermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkpermission != PackageManager.PERMISSION_GRANTED) {//没有给权限
                Log.e("permission", "动态申请");
                //参数分别是当前活动，权限字符串数组，requestcode
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
}
