package com.lanou.chenfengyao.musicdemo.ui;


import android.media.MediaPlayer;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import com.lanou.chenfengyao.musicdemo.R;
import com.lanou.chenfengyao.musicdemo.base.BaseAty;
import com.lanou.chenfengyao.musicdemo.utils.BindContent;

import java.io.IOException;

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

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MainActivity", "e:" + e);
        }

    }

}
