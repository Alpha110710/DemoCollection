package com.lanou.chenfengyao.musicdemo.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.lanou.chenfengyao.musicdemo.ui.MainActivity;
import com.lanou.chenfengyao.musicdemo.MediaNotificationManager;
import com.lanou.chenfengyao.musicdemo.PackageValidator;
import com.lanou.chenfengyao.musicdemo.R;
import com.lanou.chenfengyao.musicdemo.model.MusicProvider;
import com.lanou.chenfengyao.musicdemo.playback.CastPlayback;
import com.lanou.chenfengyao.musicdemo.playback.LocalPlayback;
import com.lanou.chenfengyao.musicdemo.playback.PlayBack;
import com.lanou.chenfengyao.musicdemo.playback.PlaybackManager;
import com.lanou.chenfengyao.musicdemo.playback.QueueManager;
import com.lanou.chenfengyao.musicdemo.utils.LogHelper;
import static com.lanou.chenfengyao.musicdemo.utils.MediaIDHelper.*;

import java.lang.ref.WeakReference;
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
public class GMusicService extends MediaBrowserServiceCompat implements PlaybackManager.PlaybackServiceCallback {

    private static final String TAG = LogHelper.makeLogTag(GMusicService.class);

    // Extra on MediaSession that contains the Cast device name currently connected to
    public static final String EXTRA_CONNECTED_CAST = "com.example.android.uamp.CAST_NAME";
    // The action of the incoming Intent indicating that it contains a command
    // to be executed (see {@link #onStartCommand})
    public static final String ACTION_CMD = "com.example.android.uamp.ACTION_CMD";
    // The key in the extras of the incoming Intent indicating the command that
    // should be executed (see {@link #onStartCommand})
    public static final String CMD_NAME = "CMD_NAME";
    // A value of a CMD_NAME key in the extras of the incoming Intent that
    // indicates that the music playback should be paused (see {@link #onStartCommand})
    public static final String CMD_PAUSE = "CMD_PAUSE";
    // A value of a CMD_NAME key that indicates that the music playback should switch
    // to local playback from cast playback.
    public static final String CMD_STOP_CASTING = "CMD_STOP_CASTING";
    // Delay stopSelf by using a handler.
    private static final int STOP_DELAY = 30000;

    private MusicProvider mMusicProvider;
    private PlaybackManager mPlaybackManager;

    private MediaSessionCompat mSession;
    private MediaNotificationManager mMediaNotificationManager;
    private Bundle mSessionExtras;
    private final DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);
    private MediaRouter mMediaRouter;
    private PackageValidator mPackageValidator;

    private boolean mIsConnectedToCar;
    private BroadcastReceiver mCarConnectionReceiver;


    /**
     * Consumer responsible for switching the Playback instances depending on whether
     * it is connected to a remote player.
     */
    private final VideoCastConsumerImpl mCastConsumer = new VideoCastConsumerImpl() {

        @Override
        public void onApplicationConnected(ApplicationMetadata appMetadata, String sessionId,
                                           boolean wasLaunched) {
            // In case we are casting, send the device name as an extra on MediaSession metadata.
            mSessionExtras.putString(EXTRA_CONNECTED_CAST,
                    VideoCastManager.getInstance().getDeviceName());
            mSession.setExtras(mSessionExtras);
            // Now we can switch to CastPlayback
            PlayBack playback = new CastPlayback(mMusicProvider);
            mMediaRouter.setMediaSessionCompat(mSession);
            mPlaybackManager.switchToPlayback(playback, true);
        }

        @Override
        public void onDisconnectionReason(int reason) {
            LogHelper.d(TAG, "onDisconnectionReason");
            // This is our final chance to update the underlying stream position
            // In onDisconnected(), the underlying CastPlayback#mVideoCastConsumer
            // is disconnected and hence we update our local value of stream position
            // to the latest position.
            mPlaybackManager.getPlayback().updateLastKnownStreamPosition();
        }

        @Override
        public void onDisconnected() {
            LogHelper.d(TAG, "onDisconnected");
            mSessionExtras.remove(EXTRA_CONNECTED_CAST);
            mSession.setExtras(mSessionExtras);
            PlayBack playback = new LocalPlayback(mMusicProvider,GMusicService.this );
            mMediaRouter.setMediaSessionCompat(null);
            mPlaybackManager.switchToPlayback(playback, false);
        }
    };

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
        LogHelper.d(TAG, "OnGetRoot: clientPackageName=" + clientPackageName,
                "; clientUid=" + clientUid + " ; rootHints=", rootHints);
        // To ensure you are not allowing any arbitrary app to browse your app's contents, you
        // need to check the origin:
        if (!mPackageValidator.isCallerAllowed(this, clientPackageName, clientUid)) {
            // If the request comes from an untrusted package, return null. No further calls will
            // be made to other media browsing methods.
            LogHelper.w(TAG, "OnGetRoot: IGNORING request from untrusted package "
                    + clientPackageName);
            return null;
        }

        return new BrowserRoot(MEDIA_ID_ROOT, null);

    }

    /**
     * 用来获取一个media item 的信息
     * 实现必须调用child集合的result.sendResult
     * 如果 load这个children 是一个耗时操作,那么必须要放到子线程中执行
     * result.detach可以在该函数返回前被调用,
     * result.sendResult,在装载完成后调用
     * @param parentMediaId
     * @param result
     */
    @Override
    public void onLoadChildren(@NonNull final String parentMediaId, @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {

        LogHelper.d(TAG, "OnLoadChildren: parentMediaId=", parentMediaId);
        if (mMusicProvider.isInitialized()) {
            // if music library is ready, return immediately
            result.sendResult(mMusicProvider.getChildren(parentMediaId, getResources()));
        } else {
            // otherwise, only return results when the music library is retrieved
            result.detach();
            mMusicProvider.retrieveMediaAsync(new MusicProvider.Callback() {
                @Override
                public void onMusicCatalogReady(boolean success) {
                    result.sendResult(mMusicProvider.getChildren(parentMediaId, getResources()));
                }
            });
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicProvider = new MusicProvider();

        // To make the app more responsive, fetch and cache catalog information now.
        // This can help improve the response time in the method
        // {@link #onLoadChildren(String, Result<List<MediaItem>>) onLoadChildren()}.
        mMusicProvider.retrieveMediaAsync(null /* Callback */);

        mPackageValidator = new PackageValidator(this);

        QueueManager queueManager = new QueueManager(mMusicProvider, getResources(),
                new QueueManager.MetadataUpdateListener() {
                    @Override
                    public void onMetadataChanged(MediaMetadataCompat metadata) {
                        mSession.setMetadata(metadata);
                    }

                    @Override
                    public void onMetadataRetrieveError() {
                        mPlaybackManager.updatePlaybackState(
                                getString(R.string.error_no_metadata));
                    }

                    @Override
                    public void onCurrentQueueIndexUpdated(int queueIndex) {
                        mPlaybackManager.handlePlayRequest();
                    }

                    @Override
                    public void onQueueUpdated(String title,
                                               List<MediaSessionCompat.QueueItem> newQueue) {
                        mSession.setQueue(newQueue);
                        mSession.setQueueTitle(title);
                    }
                });

        LocalPlayback playback = new LocalPlayback(mMusicProvider,this);
        mPlaybackManager = new PlaybackManager(this, getResources(), mMusicProvider, queueManager,
                playback);

        // Start a new MediaSession
        mSession = new MediaSessionCompat(this, "GMusicService");
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(mPlaybackManager.getMediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Context context = getApplicationContext();
        //TODO 跳转回正在播放的Aty
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 99 /*request code*/,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setSessionActivity(pi);

        mSessionExtras = new Bundle();
        mSession.setExtras(mSessionExtras);

        mPlaybackManager.updatePlaybackState(null);

        try {
            mMediaNotificationManager = new MediaNotificationManager(this);
        } catch (RemoteException e) {
            throw new IllegalStateException("Could not create a MediaNotificationManager", e);
        }
        VideoCastManager.getInstance().addVideoCastConsumer(mCastConsumer);
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());

    }

    /**
     * (non-Javadoc)
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        if (startIntent != null) {
            String action = startIntent.getAction();
            String command = startIntent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (CMD_PAUSE.equals(command)) {
                    mPlaybackManager.handlePauseRequest();
                } else if (CMD_STOP_CASTING.equals(command)) {
                    VideoCastManager.getInstance().disconnect();
                }
            } else {
                // Try to handle the intent as a media button event wrapped by MediaButtonReceiver
                MediaButtonReceiver.handleIntent(mSession, startIntent);
            }
        }
        // Reset the delay handler to enqueue a message to stop the service if
        // nothing is playing.
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        return START_STICKY;
    }

    /**
     * (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        LogHelper.d(TAG, "onDestroy");
        // Service is being killed, so make sure we release our resources
        mPlaybackManager.handleStopRequest(null);
        mMediaNotificationManager.stopNotification();
        VideoCastManager.getInstance().removeVideoCastConsumer(mCastConsumer);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mSession.release();
    }

    @Override
    public void onPlaybackStart() {
        if (!mSession.isActive()) {
            mSession.setActive(true);
        }

        mDelayedStopHandler.removeCallbacksAndMessages(null);

        // The service needs to continue running even after the bound client (usually a
        // MediaController) disconnects, otherwise the music playback will stop.
        // Calling startService(Intent) will keep the service running until it is explicitly killed.
        startService(new Intent(getApplicationContext(), GMusicService.class));
    }

    @Override
    public void onNotificationRequired() {
        mMediaNotificationManager.startNotification();
    }

    @Override
    public void onPlaybackStop() {
        // Reset the delayed stop handler, so after STOP_DELAY it will be executed again,
        // potentially stopping the service.
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        stopForeground(true);
    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
        mSession.setPlaybackState(newState);
    }


    /**
     * A simple handler that stops the service if playback is not active (playing)
     */
    private static class DelayedStopHandler extends Handler {
        private final WeakReference<GMusicService> mWeakReference;

        private DelayedStopHandler(GMusicService service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            GMusicService service = mWeakReference.get();
            if (service != null && service.mPlaybackManager.getPlayback() != null) {
                if (service.mPlaybackManager.getPlayback().isPlaying()) {
                    LogHelper.d(TAG, "Ignoring delayed stop since the media player is in use.");
                    return;
                }
                LogHelper.d(TAG, "Stopping service with delay handler.");
                service.stopSelf();
            }
        }
    }
}
