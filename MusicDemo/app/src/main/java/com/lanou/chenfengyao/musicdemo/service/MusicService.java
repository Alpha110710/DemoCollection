package com.lanou.chenfengyao.musicdemo.service;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;

import java.util.List;

/**
 * Created by ChenFengYao on 16/7/4.
 * 播放音乐的服务
 * SDK翻译
 * 媒体浏览服务的基类
 * 媒体浏览服务，使应用程序可以浏览由应用程序提供的媒体内容，并要求应用程序开始播放它.
 * 它们也可以用于控制已由MediaSession的方式播放的内容。
 */
public class MusicService extends MediaBrowserServiceCompat{

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }
}
