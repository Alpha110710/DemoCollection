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

    /**
     * 当指定的客户端浏览的时候,来获取根信息
     * 此方法应当验证客户端的包名是否有权限来浏览媒体信息在返回root id之前
     * 如果客户端不允许访问这些信息的时候 应当返回null
     * @param clientPackageName 想要访问来浏览媒体信息的Application的包名
     * @param clientUid 想要浏览媒体的应用的UID
     * @param rootHints 一个可选的Bandle参数,发送给媒体浏览服务,当连接并检索媒体id信息的时候
     *                  如果没有 就是null,bundle里的内容可能会影响当浏览时的返回信息
     * @return 所浏览的内容Root或是null
     */
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    /**
     * 用来获取一个media item 的信息
     * 实现必须调用child集合的result.sendResult
     * 如果 load这个children 是一个耗时操作,那么必须要放到子线程中执行
     * result.detach可以在该函数返回前被调用,
     * result.sendResult,在装载完成后调用
     * @param parentId
     * @param result
     */
    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
    }
}
