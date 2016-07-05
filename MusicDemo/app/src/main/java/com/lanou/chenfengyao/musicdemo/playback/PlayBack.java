package com.lanou.chenfengyao.musicdemo.playback;

import static android.support.v4.media.session.MediaSessionCompat.QueueItem;

/**
 * Created by ChenFengYao on 16/7/5.
 * 本地或远程的播放接口
 * 有例如播放 暂停等方法
 */
public interface PlayBack {
    /**
     * 设置或装载资源
     */
    void start();

    /**
     * 停止播放. 接触所有资源的分配.
     *
     * @param notifyListeners 如果为true,并且回调Callback被设置,
     *                        callback.onPlaybackStatusChanged
     *                        将会在状态改变的时候被调用
     */
    void stop(boolean notifyListeners);

    /**
     * 设置最新的播放状态.
     */
    void setState(int state);

    /**
     * 获得当前的状态
     */
    int getState();

    /**
     * @return 返回是否可以被使用.
     */
    boolean isConnected();

    /**
     * @return 是否正在播放或者假如我们得到音频焦点的时候它是否在播放
     */
    boolean isPlaying();

    /**
     * @return 当前播放item的positon
     */
    int getCurrentStreamPosition();

    /**
     * 设置当前播放的位置
     *
     * @param pos position 在当前的 stream中
     */
    void setCurrentStreamPosition(int pos);

    /**
     * 更新最后的位置
     */
    void updateLastKnownStreamPosition();

    /**
     * @param item 要播放的item
     */
    void play(QueueItem item);

    /**
     * 暂停正在播放的item
     */
    void pause();

    /**
     * 定位到给定的位置
     */
    void seekTo(int position);

    /**
     * 设置当前的mediaId.
     * 该方法只会在由一个playback切换到另一的时候会被调用
     *
     * @param mediaId 设置当前的mediaId.
     */
    void setCurrentMediaId(String mediaId);

    /**
     * @return 正在被处理的MediaID.
     */
    String getCurrentMediaId();

    interface Callback {
        /**
         * 当正在播放的音乐完成时.
         */
        void onCompletion();

        /**
         * 当Playback 状态改变时调用
         * 实现类可以用该回调来更新
         * playback的状态在media活动时.
         */
        void onPlaybackStatusChanged(int state);

        /**
         * @param error 错误信息
         */
        void onError(String error);

        /**
         * @param mediaId 是当前正在播放的
         */
        void setCurrentMediaId(String mediaId);
    }

    /**
     * @param callback 设置回调接口
     */
    void setCallback(Callback callback);
}
