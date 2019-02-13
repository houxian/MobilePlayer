package com.android.mobileplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.mobileplayer.R;
import com.android.mobileplayer.bean.MediaItem;
import com.android.mobileplayer.utils.LogUtil;
import com.android.mobileplayer.utils.Utils;
import com.android.mobileplayer.view.VideoView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private final int VIDEO_ISSHOW = 2;
    /**
     * 显示网络速度
     */
    private static final int SHOW_SPEED = 3;

    private  Utils utils;
    /**
     * 监听电量变化的广播
     */
    private MyReceiver receiver;

    /**
     * 播放列表
     */
    ArrayList<MediaItem> mediaItems;
    /**
     * 播放列表中当前的位置
     */
    int nposition;

    /**
     * 手势识别器对象
     */
    private GestureDetector gestureDetector;

    /**
     * 控制面板是否显示
     */
    private boolean bShowMedioContral = false;

    /**
     * 控制面板是否全屏显示
     */
    private boolean bfullScreen = true;

    /**
     * 屏幕的宽
     */
    private int screenWidth = 0;

    /**
     * 屏幕的高
     */
    private int screenHeight = 0;

    /**
     * 真实视频的宽
     */
    private int videoWidth;
    /**
     * 真实视频的高
     */
    private int videoHeight;

    private AudioManager audioManager;
    private  int currentVoice = 0;
    private  int maxVoice;

    /**
     * 是否是静音
     */
    private boolean isMute = false;

    private float startY;
    private float startX;
    /**
     * 屏幕的高
     */
    private float touchRang;

    /**
     * 当一按下的音量
     */
    private int mVol;

    private Vibrator vibrator;

    boolean isNetUri;

    private TextView tv_buffer_netspeed;
    private LinearLayout ll_buffer;
    private TextView tv_laoding_netspeed;
    private LinearLayout ll_loading;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_SPEED://显示网速
                    //1.得到网络速度
                    String netSpeed = utils.getNetSpeed(SystemVideoActivity.this);

                    //显示网络速
                    tv_laoding_netspeed.setText("玩命加载中..."+netSpeed);
                    tv_buffer_netspeed.setText("缓存中..."+netSpeed);

                    //2.每两秒更新一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);

                    break;
                case VIDEO_PROGRESS:
                    int currentPosition = videoview.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);
                    removeMessages(VIDEO_PROGRESS);
                    sendEmptyMessageDelayed(VIDEO_PROGRESS,1000);
                    //更新文本播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    //设置系统时间
                    tvSystemTime.setText(getSysteTime());
                    //缓存进度的更新
                    if (isNetUri) {
                        //只有网络资源才有缓存效果
                        int buffer = videoview.getBufferPercentage();//0~100
                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        //本地视频没有缓冲效果
                        seekbarVideo.setSecondaryProgress(0);
                    }
                    break;
                case VIDEO_ISSHOW:
                    showVideoContrl(false);
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

    private void showSwichPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提醒您");
        builder.setMessage("当您播放视频，有声音没有画面的时候，请切换万能播放器播放");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startVitamioPlayer();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }
    /**
     * a,把数据按照原样传入VtaimoVideoPlayer播放器
     b,关闭系统播放器
     */
    private void startVitamioPlayer() {

        if(videoview != null){
            videoview.stopPlayback();
        }


        Intent intent = new Intent(this,VitamioVideoActivity.class);
        if(mediaItems != null && mediaItems.size() > 0){

            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("videoid", nposition);

        }else if(uri != null){
            intent.setData(uri);
        }
        startActivity(intent);

        finish();//关闭页面
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video);

        findViews();
        setListener();
        getData();
        initData();
        setData();
        //实例化手势识别器
        gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                playstartAndPause();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {

                setFullScrrenContral();
                return super.onDoubleTap(e);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(bShowMedioContral==true){
                    showVideoContrl(false);
                    handler.removeMessages(VIDEO_ISSHOW);
                }else{
                    showVideoContrl(true);
                    handler.sendEmptyMessageDelayed(VIDEO_ISSHOW,4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
    }
    private void setFullScrrenContral(){
        if (bfullScreen){
            setVideoType(true); //全屏
        }else {
            setVideoType(false); // 非全屏
        }
    }
    private void setVideoType(boolean bvideoType){

        if(!bvideoType){ //全屏
            bfullScreen = true;
            videoview.setVideoSize(screenWidth,screenHeight);
            btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
        }else{  // 非全屏
            bfullScreen = false;
            //1.设置视频画面的大小
            //视频真实的宽和高
            int mVideoWidth = videoWidth;
            int mVideoHeight = videoHeight;

            //屏幕的宽和高
            int width = screenWidth;
            int height = screenHeight;

            // for compatibility, we adjust size based on aspect ratio
            if (mVideoWidth * height < width * mVideoHeight) {
                //Log.i("@@@", "image too wide, correcting");
                width = height * mVideoWidth / mVideoHeight;
            } else if (mVideoWidth * height > width * mVideoHeight) {
                //Log.i("@@@", "image too tall, correcting");
                height = width * mVideoHeight / mVideoWidth;
            }

            videoview.setVideoSize(width, height);
            btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
        }

    }

    /*
     *
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
     */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // if (lp.screenBrightness <= 0.1) {
        // return;
        // }
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        }
        getWindow().setAttributes(lp);
    }
    /**
     * 事件解析
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指按下
                //1.按下记录值
                startY = event.getY();
                startX = event.getX();
                mVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight
                handler.removeMessages(VIDEO_ISSHOW);

                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //2.移动的记录相关值
                float endY = event.getY();
                float endX = event.getX();
                float distanceY = startY - endY;

                if(endX < screenWidth/2){
                    //左边屏幕-调节亮度
                    final double FLING_MIN_DISTANCE = 0.5;
                    final double FLING_MIN_VELOCITY = 0.5;
                    if (distanceY > FLING_MIN_DISTANCE
                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {

                        setBrightness(20);
                    }
                    if (distanceY < FLING_MIN_DISTANCE
                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                        setBrightness(-20);
                    }
                }else{
                    //右边屏幕-调节声音
                    //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                    float delta = (distanceY / touchRang) * maxVoice;
                    //最终声音 = 原来的 + 改变声音；
                    int voice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);
                    if (delta != 0) {
                        isMute = false;
                        updateVoice(voice, isMute,0);
                    }

                }


//                startY = event.getY();//不要加
                break;
            case MotionEvent.ACTION_UP://手指离开
                handler.sendEmptyMessageDelayed(VIDEO_ISSHOW, 4000);
                break;
        }
        return super.onTouchEvent(event);
    }
    /**
     * 监听物理健，实现声音的调节大小
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVoice--;
            updateVoice(currentVoice, false,1);
            handler.removeMessages(VIDEO_ISSHOW);
            handler.sendEmptyMessageDelayed(VIDEO_ISSHOW, 4000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            updateVoice(currentVoice, false,1);
            handler.removeMessages(VIDEO_ISSHOW);
            handler.sendEmptyMessageDelayed(VIDEO_ISSHOW, 4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void getData() {
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>)getIntent().getSerializableExtra("videolist");
        nposition = getIntent().getIntExtra("videoid",0);

    }
    private void setData() {

        if(mediaItems!=null&&mediaItems.size()>0){
            MediaItem item = mediaItems.get(nposition);
            tvName.setText(item.getName());
            videoview.setVideoPath(item.getData());
            isNetUri = utils.isNetUri(item.getData());
        }else if(uri!=null) {
            tvName.setText(uri.toString());
            isNetUri = utils.isNetUri(uri.toString());
            videoview.setVideoURI(uri);
        }else{
            LogUtil.e("没有数据无法播放");
        }
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
        seekbarVoice.setOnSeekBarChangeListener( new MyVoiceeekBarChangeListener());
        //监听视频播放卡-系统的api
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoview.setOnInfoListener(new MyOnInfoListener());
        }
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
        tv_buffer_netspeed = (TextView) findViewById(R.id.tv_buffer_netspeed);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        tv_laoding_netspeed = (TextView) findViewById(R.id.tv_laoding_netspeed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        btnVoice.setOnClickListener( this );
        btnSwichPlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSiwchScreen.setOnClickListener( this );
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVoice = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekbarVoice.setMax(maxVoice);
        seekbarVoice.setProgress(currentVoice);

        //开始更新网络速度
        handler.sendEmptyMessage(SHOW_SPEED);
    }
    private void initData() {
        utils = new Utils();
        //注册电量广播
        receiver = new MyReceiver();
        IntentFilter intentFiler = new IntentFilter();
        //当电量变化的时候发这个广播
        intentFiler.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFiler);
        showVideoContrl(false);

        DisplayMetrics  displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
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
            isMute = !isMute;
            updateVoice(currentVoice,isMute,0);
        } else if ( v == btnSwichPlayer ) {
            showSwichPlayerDialog();
        } else if ( v == btnExit ) {
            finish();
        } else if ( v == btnVideoPre ) {
            playPreVideo();
        } else if ( v == btnVideoStartPause ) { //暂停操作
            playstartAndPause();
        } else if ( v == btnVideoNext ) {
            playNextVideo();
        } else if ( v == btnVideoSiwchScreen ) {
            setFullScrrenContral();
        }
        handler.removeMessages(VIDEO_ISSHOW);
        handler.sendEmptyMessageDelayed(VIDEO_ISSHOW,4000);
    }

    /**
     * 播放和暂停
     */
    private void playstartAndPause(){

        if(videoview.isPlaying()){
            videoview.pause();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        }else{
            videoview.start();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }
    /**
     * 播放 上一个视频
     */
    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放上一个
            nposition--;
            if (nposition >=0) {
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(nposition);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放下一个
            nposition++;
            if (nposition < mediaItems.size()) {
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(nposition);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
        }

    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {
                setEnable(false);
            } else if (mediaItems.size() == 2) {
                if (nposition == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);

                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);

                } else if (nposition == mediaItems.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);

                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);

                }
            } else {
                if (nposition == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                } else if (nposition == mediaItems.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                } else {
                    setEnable(true);
                }
            }
        } else if (uri != null) {
            //两个按钮设置灰色
            setEnable(false);
        }
    }

    private void setEnable(boolean isEnable) {
        if (isEnable) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        } else {
            //两个按钮设置灰色
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }

    }


    // voideo  进度 seek bar
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
            handler.removeMessages(VIDEO_ISSHOW);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(VIDEO_ISSHOW,4000);
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
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            ll_loading.setVisibility(View.GONE);

        }
    }
    class MyErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoActivity.this,"視頻播放失敗。",Toast.LENGTH_LONG).show();
            //1.播放的视频格式不支持--跳转到万能播放器继续播放
            startVitamioPlayer();
            //2.播放网络视频的时候，网络中断---1.如果网络确实断了，可以提示用于网络断了；2.网络断断续续的，重新播放
            //3.播放的时候本地文件中间有空白---下载做完成
            return true;
        }
    }
    class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频卡了，拖动卡
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;

                case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频卡结束了，拖动卡结束了
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }
    class MyCompletionListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            playNextVideo();
        }
    }

    private void showVideoContrl(boolean bshow){
        if(!bshow){
            llBottom.setVisibility(View.GONE);
            llTop.setVisibility(View.GONE);
            bShowMedioContral = false;
        }else {
            llBottom.setVisibility(View.VISIBLE);
            llTop.setVisibility(View.VISIBLE);
            bShowMedioContral = true;
        }

    }

    private void updateVoice(int progress,boolean isMute,int bshowDef){
        if(isMute==true){
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,bshowDef);
            seekbarVoice.setProgress(0);
        }else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,bshowDef);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }

    }


    private class MyVoiceeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        //手指滑动的时候
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (fromUser==true){
                if (progress > 0) {
                    isMute = false;
                } else {
                    isMute = true;
                }
                updateVoice(progress,false,0);
            }

        }
        // 手指触碰
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(VIDEO_ISSHOW);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(VIDEO_ISSHOW,4000);
        }
    }
}