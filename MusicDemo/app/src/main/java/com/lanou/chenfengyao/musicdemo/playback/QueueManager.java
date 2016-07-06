package com.lanou.chenfengyao.musicdemo.playback;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.lanou.chenfengyao.musicdemo.model.MusicProvider;
import com.lanou.chenfengyao.musicdemo.utils.MediaIDHelper;
import com.lanou.chenfengyao.musicdemo.utils.QueueHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ChenFengYao on 16/7/6.
 * 数据提供者的队列,里面有当前正在播放的队列和正在播放的索引
 * 依赖MusicProvider 来提供音乐的内容
 * TODO 没写完
 */
public class QueueManager {
    private MusicProvider mMusicProvider;
    private MetadataUpdateListener mMetadataUpdateListener;
    private Resources mResources;

    //当前正在播放的队列 和 索引
    private List<MediaSessionCompat.QueueItem> mPlayingQueue;
    private int mCurrentIndex;

    public QueueManager(@NonNull MusicProvider mMusicProvider,
                        @NonNull MetadataUpdateListener mMetadataUpdateListener,
                        @NonNull Resources mResources) {
        this.mMusicProvider = mMusicProvider;
        this.mMetadataUpdateListener = mMetadataUpdateListener;
        this.mResources = mResources;
        //获得一个线程安全的List
        mPlayingQueue = Collections.synchronizedList(new ArrayList<MediaSessionCompat.QueueItem>());
        mCurrentIndex = 0;
    }

    public boolean isSameBrowsingCategory(@NonNull String mediaId) {
        String[] newBrowseHierarchy = MediaIDHelper.getHierarchy(mediaId);
        MediaSessionCompat.QueueItem current = getCurrentMusic();
        if (current == null) {
            return false;
        }
        String[] currentBrowseHierarchy = MediaIDHelper.getHierarchy(
                current.getDescription().getMediaId());

        return Arrays.equals(newBrowseHierarchy, currentBrowseHierarchy);
    }

    //获取当前的音乐
    public MediaSessionCompat.QueueItem getCurrentMusic() {
        if (!QueueHelper.isIndexPlayable(mCurrentIndex, mPlayingQueue)) {
            return null;
        }
        return mPlayingQueue.get(mCurrentIndex);
    }

    //数据更新的监听接口
    public interface MetadataUpdateListener {
        void onMetadataChanged(MediaMetadataCompat metadata);
        void onMetadataRetrieveError();
        void onCurrentQueueIndexUpdated(int queueIndex);
        void onQueueUpdated(String title, List<MediaSessionCompat.QueueItem> newQueue);
    }
}
