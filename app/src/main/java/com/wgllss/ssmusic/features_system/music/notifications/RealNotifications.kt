package com.wgllss.ssmusic.features_system.music.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import androidx.palette.graphics.Palette
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.units.NavigationUtils
import com.wgllss.ssmusic.core.units.SdkIntUtils
import com.wgllss.ssmusic.features_system.music.MusicComponent
import com.wgllss.ssmusic.features_system.music.extensions.isPlaying
import com.wgllss.ssmusic.features_system.services.MusicService
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RealNotifications @Inject constructor(@ApplicationContext val context: Context, private val notificationManagerL: Lazy<NotificationManager>) : NotificationsListener {
    private var postTime: Long = -1L
    private val notificationId = hashCode()

    companion object {
        private const val CHANNEL_ID = "ssmusic_channel_00001"
        private const val SHUTDOWN = "com.wgllss.ssmusic.shutdown"
        const val PREVIOUS_ACTION = "com.wgllss.ssmusic.previous"//前一首
        const val TOGGLEPAUSE_ACTION = "com.wgllss.ssmusic.togglepause"//播放 暂停
        const val NEXT_ACTION = "com.wgllss.ssmusic.next"// 下一首
        private const val NOTIFY_MODE_NONE = 0 //默认
        private const val NOTIFY_MODE_FOREGROUND = 1 // 前台服务
        private const val NOTIFY_MODE_BACKGROUND = 2 //后台服务
        private const val IDLE_DELAY = 5 * 60 * 1000
    }


    override fun updateNotification(mediaSession: MediaSessionCompat) {
        GlobalScope.launch {
            notificationManagerL.get().notify(notificationId, buildNotification(mediaSession))
        }
    }

    override fun buildNotification(mediaSession: MediaSessionCompat): Notification {
        if (mediaSession.controller == null || mediaSession.controller.metadata == null || mediaSession.controller.playbackState == null) {
            return getEmptyNotification()
        }
        val artistName = mediaSession.controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        val trackName = mediaSession.controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        val artwork = mediaSession.controller.metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)

        val isPlaying = mediaSession.isPlaying()
        val playButtonResId: Int = if (isPlaying) R.drawable.ic_baseline_pause_36 else R.drawable.ic_baseline_play_arrow_36
        val nowPlayingIntent: Intent = NavigationUtils.getNowPlayingIntent(context)
        val clickIntent = PendingIntent.getActivity(context, 0, nowPlayingIntent, PendingIntent.FLAG_MUTABLE)
        if (postTime == -1L) {
            postTime = System.currentTimeMillis()
        }
        createNotificationChannel()
        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowCancelButton(true)
            .setShowActionsInCompactView(0, 1, 2)
            .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setStyle(style)
            setSmallIcon(R.drawable.ic_my_music_folder)
            setLargeIcon(artwork)
            setContentIntent(clickIntent)
            setContentTitle(trackName)
            setContentText(artistName)
//            setSubText(albumName)
            setColorized(true)
            setShowWhen(false)
            setOngoing(true)
            setWhen(postTime)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .addAction(R.drawable.ic_baseline_skip_previous_36, "", retrievePlaybackAction(MusicComponent.PREVIOUS_ACTION))
                .addAction(playButtonResId, "", retrievePlaybackAction(MusicComponent.TOGGLEPAUSE_ACTION))
                .addAction(R.drawable.ic_baseline_skip_next_36, "", retrievePlaybackAction(MusicComponent.NEXT_ACTION))
        }
        if (artwork != null && SdkIntUtils.isLollipop()) {
            builder.color = Palette.from(artwork).generate().getVibrantColor(Color.parseColor("#403f4d"))
        }
        return builder.build()
    }

    private fun getEmptyNotification(): Notification {
        createNotificationChannel()
        return NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_my_music_folder)
            setContentTitle("品音乐")
            setColorized(true)
            setShowWhen(false)
            setWhen(postTime)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }.build()
    }

    private fun createNotificationChannel() {
        if (!SdkIntUtils.isOreo()) return
        val name: CharSequence = "wgllss"
        val mChannel = NotificationChannel(CHANNEL_ID, name, IMPORTANCE_LOW).apply {
            description = "play back status "
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManagerL.get().createNotificationChannel(mChannel)
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {
        val serviceName = ComponentName(context, MusicService::class.java)
        val intent = Intent(action)
        intent.component = serviceName
        val mFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getService(context, 0, intent, mFlag)
    }
}