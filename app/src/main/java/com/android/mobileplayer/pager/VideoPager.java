package com.android.mobileplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.mobileplayer.R;
import com.android.mobileplayer.activity.SystemVideoActivity;
import com.android.mobileplayer.adapter.VideoPagerAdapter;
import com.android.mobileplayer.base.BasePager;
import com.android.mobileplayer.bean.MediaItem;
import com.android.mobileplayer.utils.LogUtil;

import java.util.ArrayList;

/**
 * VideoPager by houxianyong
 */

public class VideoPager extends BasePager {


    private ListView listView;
    private TextView tv_novido;
    private ProgressBar pb_loading;
    private ArrayList<MediaItem> mediaItemArrayList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaItemArrayList!=null&&mediaItemArrayList.size()>0){
                //有数据
                VideoPagerAdapter adapter = new VideoPagerAdapter(context,mediaItemArrayList);
                listView.setAdapter(adapter);
                tv_novido.setVisibility(View.GONE);

            }else{
                //没有数据
                tv_novido.setVisibility(View.VISIBLE);

            }
            pb_loading.setVisibility(View.GONE);
        }
    };
    /**
     * 构造函数
     * @param context
     */
    public VideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("本地视频页面被初始化");
        View view = View.inflate(context, R.layout.vido_pager,null);
        listView  = (ListView)view.findViewById(R.id.listView);
        tv_novido  = (TextView) view.findViewById(R.id.tv_novido);
        pb_loading  = (ProgressBar) view.findViewById(R.id.pb_loading);

        listView.setOnItemClickListener(new MyInteClickListener());
        return view;
    }

     class MyInteClickListener implements AdapterView.OnItemClickListener{

         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

             MediaItem item = mediaItemArrayList.get(position);
             Intent intent = new Intent(context, SystemVideoActivity.class);
             intent.setDataAndType(Uri.parse(item.getData()),"video/*");
             context.startActivity(intent);
         }
     }

    @Override
    public void initData() {
        super.initData();
        // 加载本地数据
        getDataFromLocal();

    }

    /**
     *  获取本地数据
     */
    private void getDataFromLocal() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                mediaItemArrayList = new ArrayList<>();
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs ={
                        MediaStore.Video.Media.DISPLAY_NAME, //文件名称
                        MediaStore.Video.Media.DURATION,    //视频总长
                        MediaStore.Video.Media.SIZE,        //文件大小
                        MediaStore.Video.Media.DATA,       //文件路径
                        MediaStore.Video.Media.ARTIST    //歌曲的演唱者

                };
                Cursor cursor = contentResolver.query(uri,objs,null,null,null);
                if(cursor!=null){
                    while (cursor.moveToNext()){
                        MediaItem item = new MediaItem();
                        mediaItemArrayList.add(item);
                        String name =   cursor.getString(0); //视频名称
                        item.setName(name);
                        long duration = cursor.getLong(1); //视频总长
                        item.setDuration(duration);
                        long size = cursor.getLong(2);  //文件大小
                        item.setSize(size);
                        String data = cursor.getString(3); //文件路径
                        item.setData(data);
                        String artist = cursor.getString(4); //歌曲的演唱者。
                        item.setArtist(artist);
                    }
                    cursor.close();
                }
                //Handler发消息
                handler.sendEmptyMessage(10);

            }
        }.start();
    }
}
