package com.lanou.chenfengyao.musicdemo.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.media.MediaPlayer.*;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.lanou.chenfengyao.musicdemo.model.MusicProvider;
import com.lanou.chenfengyao.musicdemo.model.MusicProviderSource;
import com.lanou.chenfengyao.musicdemo.utils.MediaIDHelper;

import java.io.IOException;

/**
 * Created by ChenFengYao on 16/7/5.
 * PlayBack的实现类,使用的是MediaPlayer
 * 该类只负责播放,暂停等功能,具体播放什么不确定
 * 关于音频焦点
 * Android是多任务系统，Audio系统是竞争资源。Android2.2之前，
 * 没有内建的机制来解决多个程序竞争Audio的问题，
 * 2.2引入了称作AudioFocus的机制来管理对Audio资源的竞争的管理与协调。
 */

//TODO WifiLock相关的代码 都没加入,广播接收者也没有加入
public class LocalPlayback implements PlayBack, AudioManager.OnAudioFocusChangeListener
        , OnCompletionListener, OnErrorListener, OnPreparedListener, OnSeekCompleteListener {
    //静态常量
    // 没有获得音频焦点,并且不响(音量很低)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    //没有获得焦点,但是可以响
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    //获得了全部的音频焦点
    private static final int AUDIO_FOCUSED = 2;

    //以0.2的音量播放
    public static final float VOLUME_DUCK = 0.2f;
    //设置音量是正常的
    public static final float VOLUME_NORMAL = 1.0f;

    //用来播放的
    private MediaPlayer mMediaPlayer;
    //音乐数据的提供者
    private final MusicProvider mMusicProvider;
    private Context mContext;
    //播放的状态
    private int mState;
    //回调
    private Callback mCallback;

    //当前的进度信息
    private volatile int mCurrentPosition;
    //当前播放的item mediaId
    private volatile String mCurrentMediaId;

    //该类提供访问控制音量和钤声模式的操作。
    private final AudioManager mAudioManager;
    //音频焦点的状态
    private int mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;

    private boolean mPlayOnFocusGain;

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
        //将状态设置为停止
        mState = PlaybackStateCompat.STATE_STOPPED;
        if (notifyListeners && mCallback != null) {
            //通知回调状态改变
            mCallback.onPlaybackStatusChanged(mState);
        }
        //记录下当前的进度信息
        mCurrentPosition = getCurrentStreamPosition();
        //释放AudioFocus
        giveUpAudioFocus();
        //释放所占用的资源
        relaxResources(true);
    }

    /**
     * 释放掉播放音频所需要的资源
     *
     * @param releaseMediaPlayer 是否MediaPlayer也要被释放掉
     */
    private void relaxResources(boolean releaseMediaPlayer) {
        if (releaseMediaPlayer && mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.reset();
            mMediaPlayer = null;
        }
    }


    @Override
    public void setState(int state) {
        this.mState = state;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        //还保持着播放焦点 或者正在播放
        return mPlayOnFocusGain ||
                (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    //获得当前的位置信息
    @Override
    public int getCurrentStreamPosition() {
        return mMediaPlayer != null ?
                mMediaPlayer.getCurrentPosition() : mCurrentPosition;
    }

    @Override
    public void setCurrentStreamPosition(int pos) {
        this.mCurrentPosition = pos;
    }

    //更新音频播放的位置
    @Override
    public void updateLastKnownStreamPosition() {
        if (mMediaPlayer != null) {
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
        }
    }

    //播放
    @Override
    public void play(MediaSessionCompat.QueueItem item) {
        mPlayOnFocusGain = true;//标记获得播放焦点
        tryToGetAudioFocus();//获取音频焦点
        //获得mediaId
        String mediaId = item.getDescription().getMediaId();
        boolean mediaHasChanged = !TextUtils.equals(mediaId, mCurrentMediaId);
        //如果当前播放的item改变了
        if (mediaHasChanged) {//重置当前的播放信息
            mCurrentPosition = 0;
            mCurrentMediaId = mediaId;
        }
        //如果是从暂停回来继续播放的
        if (mState == PlaybackStateCompat.STATE_PAUSED &&
                !mediaHasChanged &&//播放的还是上次的音频
                mMediaPlayer != null) {
            //根据当前的音频焦点状态来设置MediaPlayer的状态
            configMediaPlayerState();
        } else { //当前的状态是停止 就重新设置资源,并开始异步加载
            mState = PlaybackStateCompat.STATE_STOPPED;
            //释放一次资源,但是MediaPlayer就不要释放了
            relaxResources(false);
            //从MusicProvider里获得歌曲的内容
//            MediaMetadataCompat track
//                    = mMusicProvider.getMusic(
//                    MediaIDHelper.extractMusicIDFromMediaID(item.getDescription().getMediaId()));
            //TODO 这个方法没搞明白
//            String source = track.getString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE);

            createMediaPlayerIfNeeded();//创建MediaPlayer
            mState = PlaybackStateCompat.STATE_BUFFERING;

            //设置音频播放的类型是音乐(还有闹铃,通话什么的 都不是我们需要的)
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                //设置资源
//                mMediaPlayer.setDataSource(source);
                mMediaPlayer.prepareAsync();//异步加载
                if (mCallback != null) {
                    //更新状态
                    mCallback.onPlaybackStatusChanged(mState);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mCallback != null) {
                    //发送错误信息
                    mCallback.onError(e.getMessage());
                }
            }

        }
    }

    //暂停
    @Override
    public void pause() {
        if (mState == PlaybackStateCompat.STATE_PLAYING) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();//暂停
                //记录下暂停时的进度
                mCurrentPosition = mMediaPlayer.getCurrentPosition();
            }
            //释放资源
            relaxResources(false);
            giveUpAudioFocus();//释放音频焦点
        }
        mState = PlaybackStateCompat.STATE_PAUSED;
        if (mCallback != null) {
            mCallback.onPlaybackStatusChanged(mState);
        }
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer == null) {
            mCurrentPosition = position;
        } else {
            if (mMediaPlayer.isPlaying()) {
                mState = PlaybackStateCompat.STATE_BUFFERING;
            }
            mMediaPlayer.seekTo(position);
            if (mCallback != null) {
                mCallback.onPlaybackStatusChanged(mState);
            }
        }
    }

    @Override
    public void setCurrentMediaId(String mediaId) {
        this.mCurrentMediaId = mediaId;
    }

    @Override
    public String getCurrentMediaId() {
        return mCurrentMediaId;
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    /**
     * 当音频焦点改变时的回调 由AudioManager调用的
     * @param focusChange 音频焦点如何发生了变化
     *                    AUDIOFOCUS_GAIN:你已获得了音频焦点．
     *                    AUDIOFOCUS_LOSS:你已经丢失了音频焦点比较长的时间了．你必须停止所有的音频播放．因为预料到你可能很长时间也不能再获音频焦点，所以这里是清理你的资源的好地方．比如，你必须释放MediaPlayer．
     *                    AUDIOFOCUS_LOSS_TRANSIENT:你临时性的丢掉了音频焦点，很快就会重新获得．
     *                    你必须停止所有的音频播放，但是可以保留你的资源，
     *                    因为你可能很快就能重新获得焦点．
     *                    AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:你临时性的丢掉了音频焦点，
     *                    但是你被允许继续以低音量播放，而不是完全停止．
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
            //获得音频焦点
            mAudioFocus = AUDIO_FOCUSED;
        } else if(focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
            //失去了焦点,如果能小声放,就继续播放,不能就暂停
            boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
            mAudioFocus = canDuck ? AUDIO_NO_FOCUS_CAN_DUCK : AUDIO_NO_FOCUS_NO_DUCK;
            //如果正在播放,需要通过调用configMediaPlayerState来重置MediaPlayer
            if(mState == PlaybackStateCompat.STATE_PLAYING && !canDuck){
                //如果我们既没有抢占焦点,也不能小声播放,我们先把信息保存了
                //让我们正在播放,所以我们重置一次来再次获得焦点
                mPlayOnFocusGain = true;
            }

        }else {
            Log.d("LocalPlayback", "focusChange:" + focusChange);
        }
        configMediaPlayerState();
    }

    //当前的歌曲播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mCallback!=null){
            //回调CallBack的完成方法,具体播放什么歌曲由callback来决定
            mCallback.onCompletion();
        }
    }


    //当MediaPlayer发生了错误,需要通知callback 并重置mediaPlayer
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if(mCallback != null){
            mCallback.onError("发生错误,what:"+what+",extra:"+extra);
        }
        return true;
    }

    //当加载完成
    @Override
    public void onPrepared(MediaPlayer mp) {
        configMediaPlayerState();
    }


    //当定位结束
    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mCurrentPosition = mp.getCurrentPosition();
        if(mState == PlaybackStateCompat.STATE_BUFFERING){
            mp.start();
            mState = PlaybackStateCompat.STATE_PLAYING;
        }
        changeCallbackStatus();
    }


    //更新callback的状态
    private void changeCallbackStatus(){
        if(mCallback != null){
            mCallback.onPlaybackStatusChanged(mState);
        }
    }

    /**
     * 尝试获取系统的 audio focus.
     */
    private void tryToGetAudioFocus() {
        //如果当前并没有获得过焦点
        if (mAudioFocus != AUDIO_FOCUSED) {
            //请求AudioFocus
            int result = mAudioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocus = AUDIO_FOCUSED;
            }
        }
    }

    /**
     * 释放掉音频焦点
     */
    private void giveUpAudioFocus() {
        //如果当前的状态是获得了全部的音频焦点
        if (mAudioFocus == AUDIO_FOCUSED) {
            //abandonAudoFocus 使用Audio结束,归还AudioFocus
            if (mAudioManager.abandonAudioFocus(this)
                    == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                //成功归还了AudioFocus资源之后,将状态改为初始值
                mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
            }
        }
    }

    /**
     * 重新配置的MediaPlayer根据audio focus的设置来启动/重启它。
     * 该方法启动/重新启动MediaPlayer会根据音频焦点状态。
     * 因此，如果我们获得了音频焦点，就正常播放;
     * 如果我们没有焦点,它要么使MediaPlayer的暂停或将其设置为低量，
     * 取决于什么是当前的焦点设置。
     */
    private void configMediaPlayerState() {
        if (mAudioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            //没有抢到音频焦点,并且不能播放,就暂停
            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                pause();
            }
        } else {  //没有焦点(失去了焦点),但是可以播放:
            if (mAudioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {
                //将音量调低,小声播放
                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK);
            } else {
                if (mMediaPlayer != null) {
                    //又抢到了焦点
                    //可以正常播放了
                    mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL);
                }
            }
            // 当正在播放时丢失了焦点, 需要重置小声播放一下.
            if (mPlayOnFocusGain) {
                if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    if (mCurrentPosition == mMediaPlayer.getCurrentPosition()) {
                        //如果进度信息没有问题
                        mMediaPlayer.start();
                        mState = PlaybackStateCompat.STATE_PLAYING;
                    } else {
                        //更新一下进度信息,播放
                        mMediaPlayer.seekTo(mCurrentPosition);
                        mState = PlaybackStateCompat.STATE_BUFFERING;
                    }
                }
                //标记为在失去焦点的状态下播放
                mPlayOnFocusGain = false;
            }
        }
        if (mCallback != null) {
            //让回调改变状态
            mCallback.onPlaybackStatusChanged(mState);
        }
    }

    /**
     * 来确保MediaPlayer是存在的
     * 如果不存在就创建个新的
     */
    private void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            //确保MediaPlayer在播放的时候把持得一个wake-lock
            //如果不这样做的话,当我们的音乐正在播放的时候,很有可能导致我们音乐也听了
            mMediaPlayer.setWakeMode(mContext.getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);

            //将一些监听都设置上
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
        } else {
            //如果MediaPlayer已经存在的话,就重置一次
            mMediaPlayer.reset();
        }
    }
}
