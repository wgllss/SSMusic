package com.wgllss.ssmusic.features_system.music

import android.app.*
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.os.PowerManager.WakeLock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.media.MediaBrowserServiceCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.NavigationUtils
import com.wgllss.ssmusic.core.units.SdkIntUtils
import com.wgllss.ssmusic.features_system.services.MusicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class MusicComponent(val context: Context) : LifecycleOwner, MediaSessionConnector.PlaybackPreparer {

    private val mLifecycleRegistry by lazy { LifecycleRegistry(this) }

    companion object {
        private const val CHANNEL_ID = "ssmusic_channel_01"
        private const val SHUTDOWN = "com.wgllss.ssmusic.shutdown"
        const val PREVIOUS_ACTION = "com.wgllss.ssmusic.previous"//前一首
        const val TOGGLEPAUSE_ACTION = "com.wgllss.ssmusic.togglepause"//播放 暂停
        const val NEXT_ACTION = "com.wgllss.ssmusic.next"// 下一首
        private const val NOTIFY_MODE_NONE = 0 //默认
        private const val NOTIFY_MODE_FOREGROUND = 1 // 前台服务
        private const val NOTIFY_MODE_BACKGROUND = 2 //后台服务
        private const val IDLE_DELAY = 5 * 60 * 1000
    }

    protected lateinit var musicService: MusicService
    private lateinit var mNotificationManager: NotificationManagerCompat
    private var mNotificationPostTime: Long = 0
    private var mNotifyMode: Int = NOTIFY_MODE_NONE


    // Initialize the wake lock
    lateinit var powerManager: PowerManager
    lateinit var mWakeLock: WakeLock
    lateinit var mAlarmManager: AlarmManager
    lateinit var mShutdownIntent: PendingIntent
    private var mShutdownScheduled = false

    protected var mLastPlayedTime: Long = 0

    protected var musicTitle = ""
    protected var musicAuthor = ""
    protected var musicPic = ""

    val mediaSession by lazy {
        MediaSessionCompat(context, context.getString(R.string.app_name))
            .apply {
                setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
//            setCallback(MediaSessionCallback(this, this@RealSongPlayer, songsRepository, queueDao))
//            setPlaybackState(stateBuilder.build())

                val sessionIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                val sessionActivityPendingIntent = PendingIntent.getActivity(context, 0, sessionIntent, 0)
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }
    }


    val mediaSessionConnector by lazy {
        MediaSessionConnector(mediaSession).apply {
            setPlaybackPreparer(this@MusicComponent)
//            setQueueNavigator(UampQueueNavigator(mediaSession))
        }
    }


    override fun getLifecycle() = mLifecycleRegistry

    open fun onCreate(musicService: MusicService) {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        this.musicService = musicService
        mNotificationManager = NotificationManagerCompat.from(musicService)
        createNotificationChannel()
        powerManager = musicService.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name)
        mWakeLock.setReferenceCounted(false)

        val shutdownIntent = Intent(musicService, MusicService::class.java)
        shutdownIntent.action = SHUTDOWN
        mAlarmManager = musicService.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mShutdownIntent = PendingIntent.getService(musicService, 0, shutdownIntent, FLAG_MUTABLE)

        scheduleDelayedShutdown()

//        mNotificationManager.notify(hashCode(), buildNotification())
//        musicService.startForeground(hashCode(), buildNotification())
    }

    open fun onStart() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    open fun onResume() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    open fun onPause() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    open fun onStop() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    open fun onDestory() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        mediaSession.run {
            isActive = false
            release()
        }
    }

    open fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>) {

    }

    private fun createNotificationChannel() {
        if (SdkIntUtils.isOreo()) {
            val name: CharSequence = "wgllss"
            val importance = NotificationManager.IMPORTANCE_LOW
            val manager = musicService?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            manager.createNotificationChannel(mChannel)
        }
    }

    private fun cancelNotification() {
        musicService.stopForeground(true)
        mNotificationManager?.cancel(hashCode())
        mNotificationPostTime = 0
        mNotifyMode = NOTIFY_MODE_NONE
    }

    private fun scheduleDelayedShutdown() {
        mAlarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + IDLE_DELAY] = mShutdownIntent
        mShutdownScheduled = true
    }

    private fun cancelShutdown() {
        if (mShutdownScheduled) {
            mAlarmManager.cancel(mShutdownIntent)
            mShutdownScheduled = false
        }
    }

    protected open fun isPlaying(): Boolean = false

    protected open fun recentlyPlayed(): Boolean {
        logE("isPlaying()：${isPlaying()}")
        return isPlaying() || System.currentTimeMillis() - mLastPlayedTime < IDLE_DELAY
    }


    protected fun updateNotification() {
        logE("updateNotification")
        val newNotifyMode = if (isPlaying()) {
            logE("updateNotification  if (isPlaying()) {")
            NOTIFY_MODE_FOREGROUND
        } else if (recentlyPlayed()) {
            logE("updateNotification   } else if (recentlyPlayed()) {")
            NOTIFY_MODE_BACKGROUND
        } else {
            logE("updateNotification   NOTIFY_MODE_NONE")
            NOTIFY_MODE_NONE
        }
        val notificationId = hashCode()
        if (mNotifyMode != newNotifyMode) {
            if (mNotifyMode == NOTIFY_MODE_FOREGROUND) {
                if (SdkIntUtils.isLollipop()) musicService.stopForeground(newNotifyMode == NOTIFY_MODE_NONE) else musicService.stopForeground(newNotifyMode == NOTIFY_MODE_NONE || newNotifyMode == NOTIFY_MODE_BACKGROUND)
            } else if (newNotifyMode == NOTIFY_MODE_NONE) {
                mNotificationManager.cancel(notificationId)
                mNotificationPostTime = 0
            }
        }
        starNotification(notificationId, newNotifyMode)
    }

    private fun starNotification(notificationId: Int, newNotifyMode: Int) {
        GlobalScope.launch {
            flow {
                musicPic?.let {
                    val futureBitmap = Glide.with(musicService).asBitmap().load(musicPic).submit();
                    val artwork: Bitmap = futureBitmap.get()
                    emit(artwork)
                }
            }.flowOn(Dispatchers.IO)
                .onEach {
                    if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
                        musicService.startForeground(notificationId, buildNotification(it))
                    } else {
                        mNotificationManager.notify(notificationId, buildNotification(it))
                    }
                    mNotifyMode = newNotifyMode
                }
                .catch {
                    it.printStackTrace()
                }.collect()
        }
    }

    protected open fun buildNotification(artwork: Bitmap): Notification {
        val playButtonResId: Int = if (isPlaying()) R.drawable.ic_baseline_pause_36 else R.drawable.ic_baseline_play_arrow_36
        val nowPlayingIntent: Intent = NavigationUtils.getNowPlayingIntent(musicService)
        val clickIntent = PendingIntent.getActivity(musicService, 0, nowPlayingIntent, FLAG_MUTABLE)

        if (mNotificationPostTime == 0L) {
            mNotificationPostTime = System.currentTimeMillis()
        }
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(musicService, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_my_music_folder)
            .setOngoing(true)
            .setLargeIcon(artwork)
            .setContentIntent(clickIntent)
            .setContentTitle(musicTitle)
            .setContentText(musicAuthor)
            .setWhen(mNotificationPostTime)
            .addAction(R.drawable.ic_baseline_skip_previous_36, "", retrievePlaybackAction(PREVIOUS_ACTION))
            .addAction(playButtonResId, "", retrievePlaybackAction(TOGGLEPAUSE_ACTION))
            .addAction(R.drawable.ic_baseline_skip_next_36, "", retrievePlaybackAction(NEXT_ACTION))
        if (SdkIntUtils.isJellyBeanMR1()) builder.setShowWhen(false)
        if (SdkIntUtils.isLollipop()) {
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            val style = androidx.media.app.NotificationCompat.MediaStyle()
//                .setMediaSession(mSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2, 3)
            builder.setStyle(style)
        }
        if (artwork != null && SdkIntUtils.isLollipop()) {
            builder.color = Palette.from(artwork).generate().getVibrantColor(Color.parseColor("#403f4d"))
        }
        if (SdkIntUtils.isOreo()) {
            builder.setColorized(true)
        }
        val n = builder.build()
//        if (mActivateXTrackSelector) {
//            addXTrackSelector(n)
//        }
        return n
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {
        val serviceName = ComponentName(musicService, MusicService::class.java)
        val intent = Intent(action)
        intent.component = serviceName
        val mFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getService(musicService, 0, intent, mFlag)
    }

    inner class IntentReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            handleCommandIntent(intent)
        }
    }

    open fun handleCommandIntent(intent: Intent?) {

    }

    open fun play() {

    }

    override fun onCommand(player: Player, command: String, extras: Bundle?, cb: ResultReceiver?) = false

    override fun getSupportedPrepareActions(): Long =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH

    override fun onPrepare(playWhenReady: Boolean) {
        logE("onPrepare playWhenReady: $playWhenReady  ")
    }

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        logE("onPrepareFromMediaId mediaId: $mediaId playWhenReady: $playWhenReady  extras:${Thread.currentThread().name}")
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
        logE("onPrepareFromSearch query: $query playWhenReady: $playWhenReady  extras:$extras ")
    }

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
        logE("onPrepareFromUri uri: $uri playWhenReady: $playWhenReady  extras:$extras")
    }


    fun onGetRoot() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
}