package com.wgllss.ssmusic.features_system.music

import android.app.*
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK
import android.graphics.Bitmap
import android.graphics.Color
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.units.NavigationUtils
import com.wgllss.ssmusic.core.units.SdkIntUtils
import com.wgllss.ssmusic.features_system.services.MusicService

open class MusicComponent : LifecycleOwner {

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

    private val mLastPlayedTime: Long = 0

    protected var musicTitle = ""
    protected var musicAuthor = ""
    protected var musicPic = ""


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

        musicService.startForeground(hashCode(), buildNotification())
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

    protected open fun isPlaying(): Boolean = false

    protected open fun recentlyPlayed() = isPlaying() || System.currentTimeMillis() - mLastPlayedTime < IDLE_DELAY


    protected fun updateNotification() {
        val newNotifyMode: Int
        if (isPlaying()) {
            newNotifyMode = NOTIFY_MODE_FOREGROUND
        } else if (recentlyPlayed()) {
            newNotifyMode = NOTIFY_MODE_BACKGROUND
        } else {
            newNotifyMode = NOTIFY_MODE_NONE
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
        if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
            musicService.startForeground(notificationId, buildNotification())
        } else if (newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            mNotificationManager.notify(notificationId, buildNotification())
        }
        mNotifyMode = newNotifyMode
    }

    protected open fun buildNotification(): Notification {
//        val albumName: String = getAlbumName()
//        val artistName: String = getArtistName()
        val isPlaying = isPlaying()
//        val text = if (TextUtils.isEmpty(albumName)) artistName else "$artistName - $albumName"
        val playButtonResId: Int = if (isPlaying) R.drawable.ic_baseline_pause_36 else R.drawable.ic_baseline_play_arrow_36
        val nowPlayingIntent: Intent = NavigationUtils.getNowPlayingIntent(musicService)
        nowPlayingIntent.setFlags(FLAG_ACTIVITY_MULTIPLE_TASK)
        val clickIntent = PendingIntent.getActivity(musicService, 0, nowPlayingIntent, PendingIntent.FLAG_MUTABLE)
//        var artwork: Bitmap
//        artwork = Glide.with(musicService).asBitmap().load(musicPic).into(SimpleTarget<Bitmap>() {
//            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//            }
//        })
//        if (artwork == null) {
//            artwork = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_empty_music2)
//        }
        if (mNotificationPostTime == 0L) {
            mNotificationPostTime = System.currentTimeMillis()
        }
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(musicService, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_my_music_folder)
//            .setLargeIcon(artwork)
            .setContentIntent(clickIntent)
            .setContentTitle(musicTitle)
            .setContentText(musicAuthor)
            .setWhen(mNotificationPostTime)
            .addAction(R.drawable.ic_baseline_skip_previous_36, "", retrievePlaybackAction(PREVIOUS_ACTION))
            .addAction(playButtonResId, "", retrievePlaybackAction(TOGGLEPAUSE_ACTION))
            .addAction(R.drawable.ic_baseline_skip_next_36, "", retrievePlaybackAction(NEXT_ACTION))
        if (SdkIntUtils.isJellyBeanMR1()) builder.setShowWhen(false)
        if (SdkIntUtils.isLollipop()) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC)
            val style = androidx.media.app.NotificationCompat.MediaStyle()
//                .setMediaSession(mSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2, 3)
            builder.setStyle(style)
        }
//        if (artwork != null && SdkIntUtils.isLollipop()) {
//        builder.color = Palette.from(artwork).generate().getVibrantColor(Color.parseColor("#403f4d"))
//        }
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
        return PendingIntent.getService(musicService, 0, intent, FLAG_MUTABLE)
    }
}