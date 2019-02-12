package com.android.mobileplayer.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends FragmentActivity {

    private FrameLayout fl_main_content;
    private RadioGroup radioGroup;

    private ArrayList<BasePager> basePagers;
    private int nposition =0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static boolean mPermissionReqProcessed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        radioGroup = (RadioGroup)findViewById(R.id.rg_bottom_tag);
        if(SDK_INT>23){
            applypermission();
        }else {
            initView();
        }

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

    private void initView(){

        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));
        basePagers.add(new AudioPager(this));
        basePagers.add(new NetAudioPager(this));
        basePagers.add(new NetVideoPager(this));
        //设置 radioGroup 的checked 监听
        radioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        radioGroup.check(R.id.rb_video);
    }
    /**
     * 获取权限
     */
    public void applypermission() {

        if (getApplicationContext()
                .checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
            mPermissionReqProcessed = false;
        } else {
            mPermissionReqProcessed = true;
            initView();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mPermissionReqProcessed = true;
                initView();
            } else {
                finish();
            }
        }

    }

}