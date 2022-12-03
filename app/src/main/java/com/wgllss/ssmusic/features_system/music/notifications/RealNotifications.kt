package com.wgllss.ssmusic.features_system.music.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v4.media.MediaMetadataCompat.*
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.SdkIntUtils
import com.wgllss.ssmusic.features_system.music.extensions.isPlaying
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject

class RealNotifications @Inject constructor(@ApplicationContext val context: Context, private val NotificationManagerCompatL: Lazy<NotificationManagerCompat>) : NotificationsListener {
    private var postTime: Long = -1L
    private val notificationId = hashCode()
    private lateinit var notificationsListener: NotificationListener
    private var isNotificationStarted = false

    private var notificationTag = 0
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    val mediaLargeBitmapAdapter = MediaLargeBitmapAdapter()
    var currentIconUri: String? = null
    var currentBitmap: Bitmap? = null
    private var instanceId = 0

    private val notificationBroadcastReceiver by lazy {
        NotificationBroadcastReceiver()
    }

    private val intentFilter by lazy { IntentFilter() }

    companion object {
        private const val CHANNEL_ID = "ssmusic_channel_00001"
        const val ACTION_PLAY = "com.wgllss.ssmusic.play"

        /** The action which pauses playback.  */
        const val ACTION_PAUSE = "com.wgllss.ssmusic.pause"

        /** The action which skips to the previous media item.  */
        const val ACTION_PREVIOUS = "com.wgllss.ssmusic.prev"

        /** The action which skips to the next media item.  */
        const val ACTION_NEXT = "com.wgllss.ssmusic.next"
    }

    init {
        instanceId++
        intentFilter.addAction(ACTION_PLAY)
        intentFilter.addAction(ACTION_PAUSE)
        intentFilter.addAction(ACTION_PREVIOUS)
        intentFilter.addAction(ACTION_NEXT)

    }


    override fun updateNotification(mediaSession: MediaSessionCompat, largeIcon: Bitmap?) {
        GlobalScope.launch {
            val notification = buildNotification(mediaSession, largeIcon)
            NotificationManagerCompatL.get().notify(notificationId, notification)
            if (!isNotificationStarted) {
                context.registerReceiver(notificationBroadcastReceiver, intentFilter)
            }
            notificationsListener?.onNotificationPosted(notificationId, notification, !isNotificationStarted)
            isNotificationStarted = true
        }
    }

    private fun buildNotification(mediaSession: MediaSessionCompat, bitmap: Bitmap?): Notification {
        if (mediaSession.controller == null || mediaSession.controller.metadata == null || mediaSession.controller.playbackState == null) {
            return getEmptyNotification()
        }

        val artistName = mediaSession.controller.metadata.getString(METADATA_KEY_ARTIST)
        val trackName = mediaSession.controller.metadata.getString(METADATA_KEY_TITLE)
        val artworkUrl = mediaSession.controller.metadata.getString(METADATA_KEY_ALBUM_ART_URI)
        logE("artworkUrl $artworkUrl")


        val isPlaying = mediaSession.isPlaying()
        val playButtonResId: Int = if (isPlaying) R.drawable.ic_baseline_pause_36 else R.drawable.ic_baseline_play_arrow_36
        val togglepausePendingIntent = if (isPlaying) retrievePlaybackAction(ACTION_PAUSE) else retrievePlaybackAction(ACTION_PLAY)
//        val nowPlayingIntent: Intent = NavigationUtils.getNowPlayingIntent(context)
//        val clickIntent = PendingIntent.getActivity(context, 0, nowPlayingIntent, PendingIntent.FLAG_MUTABLE)
        if (postTime == -1L) {
            postTime = System.currentTimeMillis()
        }
        createNotificationChannel()
        var largeIcon = bitmap
        if (largeIcon == null && artworkUrl != null) {
            largeIcon = mediaLargeBitmapAdapter.getCurrentLargeIcon(mediaSession, artworkUrl, LoadLargeIconBitMapCall(++notificationTag))
        }

        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowCancelButton(true)
            .setShowActionsInCompactView(0, 1, 2, 3)
            .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setStyle(style)
            setSmallIcon(R.drawable.loading_logo)
            setLargeIcon(largeIcon)
            setContentIntent(mediaSession.controller.sessionActivity)
            setContentTitle(trackName)
            setContentText(artistName)
//            setSubText(albumName)
            setColorized(true)
            setShowWhen(false)
            setOngoing(true)
            setWhen(postTime)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .addAction(R.drawable.ic_baseline_skip_previous_36, "", retrievePlaybackAction(ACTION_PREVIOUS))
                .addAction(playButtonResId, "", togglepausePendingIntent)
                .addAction(R.drawable.ic_baseline_skip_next_36, "", retrievePlaybackAction(ACTION_NEXT))
        }
        if (largeIcon != null && SdkIntUtils.isLollipop()) {
            builder.setShowWhen(true)
            builder.color = Palette.from(largeIcon).generate().getVibrantColor(Color.parseColor("#403f4d"))
        }
        return builder.build()
    }

    override fun setNotificationListener(notificationsListener: NotificationListener) {
        this.notificationsListener = notificationsListener
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
        NotificationManagerCompatL.get().createNotificationChannel(mChannel)
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {
        val intent = Intent(action).setPackage(context.packageName)
        val pendingFlags = if (SdkIntUtils.isLollipop()) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getBroadcast(context, instanceId, intent, pendingFlags);
    }

    inner class MediaLargeBitmapAdapter() {


        fun getCurrentLargeIcon(mediaSession: MediaSessionCompat, bitmapUrl: String, loadLargeIconBitMapCall: LoadLargeIconBitMapCall): Bitmap? {
            return if (currentIconUri == null || currentIconUri != bitmapUrl) {
                currentIconUri = bitmapUrl
                serviceScope.launch {
                    currentBitmap = bitmapUrl?.let {
                        resolveUriAsBitmap(it)
                    }
                    currentBitmap?.let {
                        loadLargeIconBitMapCall.onBitmap(mediaSession, it)
                    }
                }
                null
            } else currentBitmap
        }

        private suspend fun resolveUriAsBitmap(uri: String): Bitmap? {
            return withContext(Dispatchers.IO) {
                // Block on downloading artwork.
                Glide.with(context).applyDefaultRequestOptions(glideOptions)
                    .asBitmap()
                    .load(uri)
                    .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                    .get()
            }
        }

        private val glideOptions = RequestOptions()
            .fallback(R.drawable.loading_logo)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    }

    inner class LoadLargeIconBitMapCall(val notificationTag: Int) {


        fun onBitmap(mediaSession: MediaSessionCompat, bitmap: Bitmap) {
            bitmap?.let { postUpdateNotificationBitmap(mediaSession, bitmap, notificationTag) }
        }
    }

    private fun postUpdateNotificationBitmap(mediaSession: MediaSessionCompat, bitmap: Bitmap, notificationTag: Int) {
        if (notificationTag == this.notificationTag)
            updateNotification(mediaSession, bitmap)
    }

    private inner class NotificationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String = intent?.action ?: ""
            logE("NotificationBroadcastReceiver action $action")
            if (ACTION_PLAY == action) {
                notificationsListener?.onActionPlay()
            } else if (ACTION_PAUSE == action) {
                notificationsListener?.onActionPause()
            } else if (ACTION_PREVIOUS == action) {
                notificationsListener?.onActionPrev()
            } else if (ACTION_NEXT == action) {
                notificationsListener?.onActionNext()
            }
        }
    }
}