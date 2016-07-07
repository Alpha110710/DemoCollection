package com.lanou.chenfengyao.musicdemo.playback;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.lanou.chenfengyao.musicdemo.R;
import com.lanou.chenfengyao.musicdemo.model.MusicProvider;
import com.lanou.chenfengyao.musicdemo.utils.MediaIDHelper;

/**
 * Created by ChenFengYao on 16/7/5.
 * 管理 服务，队列管理器和playBack实现之间的相互作用,应该是类似P层的类
 */
public class PlaybackManager implements PlayBack.Callback {

    // Action to thumbs up a media item
    private static final String CUSTOM_ACTION_THUMBS_UP = "com.example.android.uamp.THUMBS_UP";

    private MusicProvider mMusicProvider;
    private QueueManager mQueueManager;
    private Resources mResources;
    private PlayBack mPlayback;
    private PlaybackServiceCallback mServiceCallback;
    private MediaSessionCallback mMediaSessionCallback;

    public PlaybackManager(PlaybackServiceCallback mServiceCallback,
                           Resources mResources,
                           MusicProvider mMusicProvider,
                           QueueManager mQueueManager,
                           PlayBack mPlayback) {
        this.mServiceCallback = mServiceCallback;
        this.mResources = mResources;
        this.mMusicProvider = mMusicProvider;
        this.mQueueManager = mQueueManager;
        this.mPlayback = mPlayback;
        this.mPlayback.setCallback(this);
        mMediaSessionCallback = new MediaSessionCallback();
    }

    public PlayBack getPlayback(){
        return mPlayback;
    }

    public MediaSessionCompat.Callback getMediaSessionCallback() {
        return mMediaSessionCallback;
    }

    /**
     * 请求播放音乐
     */
    public void handlePlayRequest() {
        MediaSessionCompat.QueueItem currentMusic =
                mQueueManager.getCurrentMusic();
        Log.d("PlaybackManager", "currentMusic == null:" + (currentMusic == null));
        if (currentMusic != null) {
            mServiceCallback.onPlaybackStart();
            //开始播放
            mPlayback.play(currentMusic);
        }
    }

    /**
     * 处理暂停音乐的请求
     */
    public void handlePauseRequest() {
        if (mPlayback.isPlaying()) {
            mPlayback.pause();
            mServiceCallback.onPlaybackStop();
        }
    }

    /**
     * 处理停止播放的请求
     *
     * @param withError 导致停止的错误信息,将会显示在客户端上
     */
    public void handleStopRequest(String withError) {
        mPlayback.stop(true);
        mServiceCallback.onPlaybackStop();
        updatePlaybackState(withError);
    }

    /**
     * 更新当前mediaPlayer的状态, 也可以用来显示错误信息
     *
     * @param error 如果不为空 就是要显示错误信息
     */
    public void updatePlaybackState(String error) {
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mPlayback != null && mPlayback.isConnected()) {
            position = mPlayback.getCurrentStreamPosition();
        }

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());

        setCustomAction(stateBuilder);
        int state = mPlayback.getState();//得到当前的状态

        // 如果错误信息不为空
        if (error != null) {
            stateBuilder.setErrorMessage(error);
            state = PlaybackStateCompat.STATE_ERROR;//状态是错误
        }
        //设置当前的状态
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());

        // 获取当前播放的歌曲
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            stateBuilder.setActiveQueueItemId(currentMusic.getQueueId());
        }

        mServiceCallback.onPlaybackStateUpdated(stateBuilder.build());

        if (state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_PAUSED) {
            mServiceCallback.onNotificationRequired();
        }
    }

    //设置当前的状态
    private void setCustomAction(PlaybackStateCompat.Builder stateBuilder) {
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic == null) {
            return;
        }

        String mediaId = currentMusic.getDescription().getMediaId();
        if (mediaId == null) {
            return;
        }
        String musicId = MediaIDHelper.extractMusicIDFromMediaID(mediaId);
        int favoriteIcon = mMusicProvider.isFavorite(musicId) ?
                R.drawable.ic_star_on : R.drawable.ic_star_off;
        Bundle customActionExtras = new Bundle();
        stateBuilder.addCustomAction(new PlaybackStateCompat.CustomAction.Builder(
                CUSTOM_ACTION_THUMBS_UP, mResources.getString(R.string.favorite), favoriteIcon)
                .setExtras(customActionExtras)
                .build());
    }

    //设置都有什么可用行为
    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (mPlayback.isPlaying()) {//如果正在播放,就加上暂停
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }


    //Playback.Callback 的实现
    @Override
    public void onCompletion() {
        // 音乐播放完成,开始播放下一曲
        if (mQueueManager.skipQueuePosition(1)) {
            handlePlayRequest();
            mQueueManager.updateMetadata();
        } else {
            //如果没有下一曲 就停止
            handleStopRequest(null);
        }
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState(null);
    }

    @Override
    public void onError(String error) {
        updatePlaybackState(error);
    }

    @Override
    public void setCurrentMediaId(String mediaId) {
        mQueueManager.setQueueFromMusic(mediaId);
    }

    /**
     * 切换不同的PlayBack实体, 如果可能的话 尽量保存playback的状态
     *
     * @param playback 要切换的playback
     */
    public void switchToPlayback(PlayBack playback, boolean resumePlaying) {
        if (playback == null) {
            throw new IllegalArgumentException("Playback cannot be null");
        }
        // 保存旧的
        int oldState = mPlayback.getState();
        int pos = mPlayback.getCurrentStreamPosition();
        String currentMediaId = mPlayback.getCurrentMediaId();
        mPlayback.stop(false);
        playback.setCallback(this);
        playback.setCurrentStreamPosition(pos < 0 ? 0 : pos);
        playback.setCurrentMediaId(currentMediaId);
        playback.start();
        //替换新的实现
        mPlayback = playback;
        switch (oldState) {
            case PlaybackStateCompat.STATE_BUFFERING:
            case PlaybackStateCompat.STATE_CONNECTING:
            case PlaybackStateCompat.STATE_PAUSED:
                mPlayback.pause();
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
                if (resumePlaying && currentMusic != null) {
                    mPlayback.play(currentMusic);
                } else if (!resumePlaying) {
                    mPlayback.pause();
                } else {
                    mPlayback.stop(true);
                }
                break;
            case PlaybackStateCompat.STATE_NONE:
                break;
        }


    }


    //当播放等信息传来时的回调
    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            if (mQueueManager.getCurrentMusic() == null) {
                mQueueManager.setRandomQueue();
            }
            handlePlayRequest();
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
            mQueueManager.setCurrentQueueItem(queueId);
            handlePlayRequest();
            mQueueManager.updateMetadata();
        }

        @Override
        public void onSeekTo(long position) {
            mPlayback.seekTo((int) position);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            mQueueManager.setQueueFromMusic(mediaId);
            handlePlayRequest();
        }

        @Override
        public void onPause() {
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            handleStopRequest(null);
        }

        @Override
        public void onSkipToNext() {
            if (mQueueManager.skipQueuePosition(1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }
            mQueueManager.updateMetadata();
        }

        @Override
        public void onSkipToPrevious() {
            if (mQueueManager.skipQueuePosition(-1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }
            mQueueManager.updateMetadata();
        }

        @Override
        public void onCustomAction(@NonNull String action, Bundle extras) {
            if (CUSTOM_ACTION_THUMBS_UP.equals(action)) {
                MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
                if (currentMusic != null) {
                    String mediaId = currentMusic.getDescription().getMediaId();
                    if (mediaId != null) {
                        String musicId = MediaIDHelper.extractMusicIDFromMediaID(mediaId);
                        mMusicProvider.setFavorite(musicId, !mMusicProvider.isFavorite(musicId));
                    }
                }
                // 回调状态需要更新
                updatePlaybackState(null);
            }
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            mPlayback.setState(PlaybackStateCompat.STATE_CONNECTING);
            boolean successSearch = mQueueManager.setQueueFromSearch(query, extras);
            if (successSearch) {
                handlePlayRequest();
                mQueueManager.updateMetadata();
            } else {
                updatePlaybackState("找不到该音乐");
            }
        }
    }


    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);
    }
}

