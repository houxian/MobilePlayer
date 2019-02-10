package com.android.mobileplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.mobileplayer.R;

public class SystemVideoActivity extends Activity {

   private VideoView videoview;
   private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video);
        videoview = (VideoView) findViewById(R.id.videoview);

        videoview.setOnPreparedListener(new MyPreparedListener());
        videoview.setOnErrorListener(new MyErrorListener());
        videoview.setOnCompletionListener(new MyCompletionListener());
        uri = getIntent().getData();
        if(uri!=null){
            videoview.setVideoURI(uri);
        }
        videoview.setMediaController(new MediaController(this));
    }

    class MyPreparedListener implements MediaPlayer.OnPreparedListener{
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoview.start();
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
