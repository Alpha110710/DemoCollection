package com.lanou.chenfengyao.musicdemo;


import android.content.ComponentName;
import android.media.MediaPlayer;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;

import com.lanou.chenfengyao.musicdemo.base.BaseAty;
import com.lanou.chenfengyao.musicdemo.model.MusicProvider;
import com.lanou.chenfengyao.musicdemo.model.RemoteJSONSource;
import com.lanou.chenfengyao.musicdemo.playback.LocalPlayback;
import com.lanou.chenfengyao.musicdemo.playback.PlaybackManager;
import com.lanou.chenfengyao.musicdemo.playback.QueueManager;
import com.lanou.chenfengyao.musicdemo.service.MusicService;
import com.lanou.chenfengyao.musicdemo.utils.BindContent;
import com.lanou.chenfengyao.musicdemo.utils.LogHelper;

import java.io.IOException;
import java.util.List;

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
//        MediaPlayer mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(url);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("MainActivity", "e:" + e);
//        }
    }

}
