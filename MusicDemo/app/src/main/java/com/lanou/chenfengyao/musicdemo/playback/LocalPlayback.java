package com.lanou.chenfengyao.musicdemo.playback;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.media.session.MediaSessionCompat;
import android.media.MediaPlayer.*;
import android.support.v4.media.session.PlaybackStateCompat;

import com.lanou.chenfengyao.musicdemo.model.MusicProvider;

/**
 * Created by ChenFengYao on 16/7/5.
 * PlayBack的实现类,使用的是MediaPlayer
 */
public class LocalPlayback implements PlayBack, AudioManager.OnAudioFocusChangeListener
        , OnCompletionListener, OnErrorListener, OnPreparedListener, OnSeekCompleteListener {
    //用来播放的
    private MediaPlayer mediaPlayer;
    //音乐数据的提供者
    private final MusicProvider mMusicProvider;
    private Context mContext;
    //播放的状态
    private int mState;

    //该类提供访问控制音量和钤声模式的操作。
    private final AudioManager mAudioManager;

    //构造方法
    public LocalPlayback(MusicProvider mMusicProvider, Context context) {
        this.mMusicProvider = mMusicProvider;
        this.mContext = context;
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //这是默认的播放状态，并且表示没有媒体已被添加，或播放器已经复位，并没有要播放的内容。
        this.mState = PlaybackStateCompat.STATE_NONE;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop(boolean notifyListeners) {

    }

    @Override
    public void setState(int state) {

    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getCurrentStreamPosition() {
        return 0;
    }

    @Override
    public void setCurrentStreamPosition(int pos) {

    }

    @Override
    public void updateLastKnownStreamPosition() {

    }

    @Override
    public void play(MediaSessionCompat.QueueItem item) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void seekTo(int position) {

    }

    @Override
    public void setCurrentMediaId(String mediaId) {

    }

    @Override
    public String getCurrentMediaId() {
        return null;
    }

    @Override
    public void setCallback(Callback callback) {

    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
}
