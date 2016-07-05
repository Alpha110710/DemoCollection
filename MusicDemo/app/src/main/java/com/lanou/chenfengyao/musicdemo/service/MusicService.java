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

/**
 * 这个类提供了通过服务MediaBrowser的。
 * 它暴露了媒体库的浏览客户端，通过onGetRoot和onLoadChildren方法。
 * 它还创建一个MediaSession并公开它通过其MediaSession.Token，
 * 它允许客户机创建连接到将MediaController和远程发送控制命令到MediaSession。
 * 这是需要与媒体会话交互，如Android汽车的用户界面非常有用。
 * 你可以（应该）也使用相同的服务从您的应用程序的用户界面，
 * 这给了无缝的播放体验给用户相同的服务。
 *
 * 想要实现一个MediaBrowserService你需要
 * 1 继承MediaBrowserServiceCompat并实现里面的两个抽象方法
 * 2 设置一个回调{android.media.session.MediaSession#setCallback(android.media.session.MediaSession.Callback)}
 *    这个回调将会接收所有的用户控制,例如播放,暂停等
 * 3 处理所有音乐播放使用任何你喜欢的方式(例如android.media.MediaPlayer)
 * 4 更新playbackState，“正在播放”元数据和队列，利用MediaSession的正确方法
 * 5 在清单文件中注册服务 并添加action android.media.browser.MediaBrowserService
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
