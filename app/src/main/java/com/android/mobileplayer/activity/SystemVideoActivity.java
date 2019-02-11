package com.android.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.mobileplayer.R;
import com.android.mobileplayer.utils.LogUtil;
import com.android.mobileplayer.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemVideoActivity extends Activity implements View.OnClickListener {

   private VideoView videoview;
   private Uri uri;
   private LinearLayout llTop;
   private TextView tvName;
   private ImageView ivBattery;
   private TextView tvSystemTime;
   private Button btnVoice;
   private SeekBar seekbarVoice;
   private Button btnSwichPlayer;
   private LinearLayout llBottom;
   private TextView tvCurrentTime;
   private SeekBar seekbarVideo;
   private TextView tvDuration;
   private Button btnExit;
   private Button btnVideoPre;
   private Button btnVideoStartPause;
   private Button btnVideoNext;
   private Button btnVideoSiwchScreen;
   private final  int VIDEO_PROGRESS =1;
   private  Utils utils;
    /**
     * 监听电量变化的广播
     */
    private MyReceiver receiver;

   private Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what){
               case VIDEO_PROGRESS:
                   int currentPosition = videoview.getCurrentPosition();
                   seekbarVideo.setProgress(currentPosition);
                   removeMessages(VIDEO_PROGRESS);
                   sendEmptyMessageDelayed(VIDEO_PROGRESS,1000);
                   //更新文本播放进度
                   tvCurrentTime.setText(utils.stringForTime(currentPosition));

                   //设置系统时间
                   tvSystemTime.setText(getSysteTime());
                   break;
               default:
                   break;

           }
       }
   };
    /**
     * 得到系统时间
     *
     * @return
     */
    public String getSysteTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video);

        findViews();

        setListener();

        uri = getIntent().getData();
        if(uri!=null){
            videoview.setVideoURI(uri);
        }
        initData();
    }
    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//0~100;
            setBattery(level);//主线程
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }
    /**
     * 设置监听
     */
    private void setListener(){
        videoview.setOnPreparedListener(new MyPreparedListener());
        videoview.setOnErrorListener(new MyErrorListener());
        videoview.setOnCompletionListener(new MyCompletionListener());
        seekbarVideo.setOnSeekBarChangeListener( new MyVideoSeekBarChangeListener());
    }
    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-02-11 19:11:02 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvSystemTime = (TextView)findViewById( R.id.tv_system_time );
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        btnSwichPlayer = (Button)findViewById( R.id.btn_swich_player );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSiwchScreen = (Button)findViewById( R.id.btn_video_siwch_screen );

        btnVoice.setOnClickListener( this );
        btnSwichPlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSiwchScreen.setOnClickListener( this );
    }
    private void initData() {
        utils = new Utils();
        //注册电量广播
        receiver = new MyReceiver();
        IntentFilter intentFiler = new IntentFilter();
        //当电量变化的时候发这个广播
        intentFiler.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFiler);
    }

    @Override
    protected void onDestroy() {
        //移除所有的消息
        handler.removeCallbacksAndMessages(null);

        //释放资源的时候，先释放子类，在释放父类
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        LogUtil.e("onDestroy--");
        super.onDestroy();
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-02-11 19:11:02 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnVoice ) {
            // Handle clicks for btnVoice
        } else if ( v == btnSwichPlayer ) {
            // Handle clicks for btnSwichPlayer
        } else if ( v == btnExit ) {
            // Handle clicks for btnExit
        } else if ( v == btnVideoPre ) {
            // Handle clicks for btnVideoPre
        } else if ( v == btnVideoStartPause ) { //暂停操作
            // Handle clicks for btnVideoStartPause
            if(videoview.isPlaying()){
                videoview.pause();
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
            }else{
                videoview.start();
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
            }

        } else if ( v == btnVideoNext ) {
            // Handle clicks for btnVideoNext
        } else if ( v == btnVideoSiwchScreen ) {
            // Handle clicks for btnVideoSiwchScreen
        }
    }
    // voideo seek bar
    class  MyVideoSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        //手指滑动的时候
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (fromUser==true){
                videoview.seekTo(progress);
            }

        }
        // 手指触碰
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    class MyPreparedListener implements MediaPlayer.OnPreparedListener{
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoview.start();
            int  duration = videoview.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));
            //发送消息
            handler.sendEmptyMessage(VIDEO_PROGRESS);
        }
    }
    class MyErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoActivity.this,"視頻播放失敗。",Toast.LENGTH_LONG).show();
            return true;
        }
    }
    class MyCompletionListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(SystemVideoActivity.this,"視頻播放完成！",Toast.LENGTH_LONG).show();

        }
    }

}
