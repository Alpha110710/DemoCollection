package com.lanou.chenfengyao.musicdemo;


import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import com.lanou.chenfengyao.musicdemo.base.BaseAty;
import com.lanou.chenfengyao.musicdemo.utils.BindContent;

@BindContent(value = R.layout.activity_main)
public class MainActivity extends BaseAty {
    String url ="http://yinyueshiting.baidu.com/data2/music/121949513/121949513.mp3?xcode=f2f5e0ebae97740439ce60c9b3abe9ef";
    public static String EXTRA_START_FULLSCREEN;
    public static String EXTRA_CURRENT_MEDIA_DESCRIPTION;
    private MediaBrowserCompat mMediaBrowser;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        MyThread myThread = new MyThread();
        Log.d("MainActivity", "myThread:" + myThread);
        myThread.start();
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            Log.d("MyThread", Thread.currentThread().getName());
        }
    }

}
