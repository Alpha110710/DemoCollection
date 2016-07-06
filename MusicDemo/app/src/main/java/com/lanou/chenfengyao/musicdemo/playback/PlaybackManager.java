package com.lanou.chenfengyao.musicdemo.playback;

/**
 * Created by ChenFengYao on 16/7/5.
 * 管理 服务，队列管理器和playBack实现之间的相互作用
 */
public class PlaybackManager implements PlayBack.Callback{
    @Override
    public void onCompletion() {

    }

    @Override
    public void onPlaybackStatusChanged(int state) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void setCurrentMediaId(String mediaId) {

    }
}
