package com.wgllss.ssmusic.features_system.music.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Util
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.SdkIntUtils
import kotlinx.coroutines.*

open class SSPlayerNotificationManager(private val context: Context, private val mediaSession: MediaSessionCompat, private val notificationsListener: NotificationListener) {

    companion object {
        private const val CHANNEL_ID = "ssmusic_channel_00001"
        private const val ACTION_PLAY = "com.wgllss.ssmusic.play"
        private const val ACTION_PAUSE = "com.wgllss.ssmusic.pause"
        private const val ACTION_PREVIOUS = "com.wgllss.ssmusic.prev"
        private const val ACTION_NEXT = "com.wgllss.ssmusic.next"
        private const val ACTION_CONTENT = "com.wgllss.ssmusic.content.intent"
        private const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

        // Internal messages.
        private const val MSG_START_OR_UPDATE_NOTIFICATION = 0
        private const val MSG_UPDATE_NOTIFICATION_BITMAP = 1
    }


    private val notificationManagerCompat by lazy { NotificationManagerCompat.from(context) }
    private var player: Player? = null
    private val playerListener: Player.Listener by lazy { PlayerListener() }
    private val currentNotificationTag = 0

    // This fails the nullness checker because handleMessage() is 'called' while `this` is still
    // @UnderInitialization. No tasks are scheduled on mainHandler before the constructor completes,
    // so this is safe and we can suppress the warning.
    private val mainHandler by lazy { Util.createHandler(Looper.getMainLooper(), Handler.Callback { msg: Message -> this.handleMessage(msg) }) }


    private var postTime: Long = -1L
    private val notificationId = hashCode()
    private var isNotificationStarted = false
    private var notificationTag = 0
    private val serviceJob by lazy { SupervisorJob() }
    private val serviceScope by lazy { CoroutineScope(Dispatchers.Main + serviceJob) }
    private val mediaLargeBitmapAdapter by lazy { MediaLargeBitmapAdapter() }
    private var currentIconUri: String? = null
    private var currentBitmap: Bitmap? = null
    private var instanceId = 0
    private var builder: NotificationCompat.Builder? = null
    private val intentFilter by lazy { IntentFilter() }
    private val notificationBroadcastReceiver by lazy { NotificationBroadcastReceiver() }

    @Volatile
    private var isNotificationChange = true


    init {
        instanceId++
    }

    private fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_START_OR_UPDATE_NOTIFICATION -> if (player != null) {
                startOrUpdateNotification(player!!,  /* bitmap= */null)
            }
            MSG_UPDATE_NOTIFICATION_BITMAP -> if (player != null && isNotificationStarted && currentNotificationTag == msg.arg1) {
                startOrUpdateNotification(player!!, msg.obj as Bitmap)
            }
            else -> return false
        }
        return true
    }

    fun hideNotification() {
        showNotificationForPlayer(null)
    }

    fun showNotificationForPlayer(player: Player?) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper())
        Assertions.checkArgument(player == null || player.applicationLooper == Looper.getMainLooper())
        if (this.player === player) {
            return
        }
        if (this.player != null) {
            this.player?.removeListener(playerListener)
            if (player == null) {
                stopNotification( /* dismissedByUser= */false)
            }
        }
        this.player = player
        if (player != null) {
            player.addListener(playerListener)
            postStartOrUpdateNotification()
        }
    }

    private fun createNotificationChannel() {
        if (!SdkIntUtils.isOreo()) return
        val name: CharSequence = "wgllss"
        val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW).apply {
            description = "play back status "
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManagerCompat.createNotificationChannel(mChannel)
    }

    private fun createNotification(player: Player, ongoing: Boolean, bitmap: Bitmap?): NotificationCompat.Builder? {
        if (player.playbackState == Player.STATE_IDLE && player.currentTimeline.isEmpty) {
            return null
        }
        if (mediaSession.controller == null || mediaSession.controller.metadata == null || mediaSession.controller.playbackState == null) {
            return null
        }
        val artistName = mediaSession.controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        val trackName = mediaSession.controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        val artworkUrl = mediaSession.controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
        logE("artworkUrl $artworkUrl")

        val isPlaying = shouldShowPauseButton(player)
        val playButtonResId: Int = if (isPlaying) R.drawable.ic_baseline_pause_36 else R.drawable.ic_baseline_play_arrow_36
        val togglePausePendingIntent = if (isPlaying) retrievePlaybackAction(ACTION_PAUSE) else retrievePlaybackAction(ACTION_PLAY)

        if (postTime == -1L) {
            postTime = System.currentTimeMillis()
        }
        createNotificationChannel()
        if (builder == null || isNotificationChange != isPlaying) {
            isNotificationChange = isPlaying
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
            builder?.apply {
                addAction(R.drawable.ic_baseline_skip_previous_36, "", retrievePlaybackAction(ACTION_PREVIOUS))
                addAction(playButtonResId, "", togglePausePendingIntent)
                addAction(R.drawable.ic_baseline_skip_next_36, "", retrievePlaybackAction(ACTION_NEXT))
            }
        }


        var largeIcon = bitmap
        if (largeIcon == null && artworkUrl != null) {
            largeIcon = mediaLargeBitmapAdapter.getCurrentLargeIcon(artworkUrl, LoadLargeIconBitMapCall(++notificationTag))
        }

        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowCancelButton(true)
            .setShowActionsInCompactView(0, 1, 2, 3)
            .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))

        builder?.apply {
            setStyle(style)
            setSmallIcon(R.drawable.loading_logo)
            setLargeIcon(largeIcon)
            setOngoing(ongoing)
//            setContentIntent(retrievePlaybackAction(ACTION_CONTENT))
            setContentIntent(mediaSession.controller.sessionActivity)
            setContentTitle(trackName)
            setContentText(artistName)
//            setSubText(albumName)
            setColorized(true)
            setShowWhen(false)
            setWhen(postTime)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            priority = NotificationCompat.PRIORITY_LOW
            setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
        }
        if (SdkIntUtils.isLollipop() && player.isPlaying && !player.isPlayingAd && !player.isCurrentMediaItemDynamic && player.playbackParameters.speed == 1f) {
            builder?.apply {
                setWhen(System.currentTimeMillis() - player.contentPosition)
                setShowWhen(true)
                setUsesChronometer(true)
            }
            largeIcon?.let {
                builder?.color = Palette.from(it).generate().getVibrantColor(Color.parseColor("#403f4d"))
            }
        } else {
            builder?.apply {
                setShowWhen(false)
                setUsesChronometer(false)
                setWhen(postTime)
            }
        }
        builder?.setOnlyAlertOnce(true)
        return builder
    }

    private fun shouldShowPauseButton(player: Player): Boolean {
        return player.playbackState != Player.STATE_ENDED && player.playbackState != Player.STATE_IDLE && player.playWhenReady
    }

    private fun getOngoing(player: Player): Boolean {
        val playbackState = player.playbackState
        return ((playbackState == Player.STATE_BUFFERING || playbackState == Player.STATE_READY)
                && player.playWhenReady)
    }


    private fun startOrUpdateNotification(player: Player, bitmap: Bitmap?) {
        val ongoing: Boolean = getOngoing(player)
        builder = createNotification(player, ongoing, bitmap)
        if (builder == null) {
            logE("111")
            stopNotification( /* dismissedByUser= */false)
            return
        }
        val notification: Notification = builder!!.build()
        notificationManagerCompat.notify(notificationId, notification)
        if (!isNotificationStarted) {
            intentFilter.apply {
                takeUnless {
                    it.hasAction(ACTION_PLAY)
                }?.apply {
                    addAction(ACTION_PLAY)
                }
                takeUnless {
                    it.hasAction(ACTION_PAUSE)
                }?.apply {
                    addAction(ACTION_PAUSE)
                }
                takeUnless {
                    it.hasAction(ACTION_PREVIOUS)
                }?.apply {
                    addAction(ACTION_PREVIOUS)
                }
                takeUnless {
                    it.hasAction(ACTION_NEXT)
                }?.apply {
                    addAction(ACTION_NEXT)
                }
                takeUnless {
                    it.hasAction(ACTION_CONTENT)
                }?.apply {
                    addAction(ACTION_CONTENT)
                }
            }
            context.registerReceiver(notificationBroadcastReceiver, intentFilter)
        }
        notificationsListener?.onNotificationPosted(notificationId, notification, ongoing || !isNotificationStarted)
        isNotificationStarted = true
    }

    private fun stopNotification(dismissedByUser: Boolean) {
        if (isNotificationStarted) {
            isNotificationStarted = false
            this.player?.removeListener(playerListener)
            mainHandler.removeMessages(MSG_START_OR_UPDATE_NOTIFICATION)
            notificationManagerCompat.cancel(notificationId)
            context.unregisterReceiver(notificationBroadcastReceiver)
            notificationsListener?.onNotificationCancelled(notificationId, dismissedByUser)
        }
    }

    private fun postStartOrUpdateNotification() {
        if (!mainHandler.hasMessages(MSG_START_OR_UPDATE_NOTIFICATION)) {
            mainHandler.sendEmptyMessage(MSG_START_OR_UPDATE_NOTIFICATION)
        }
    }

    private fun postUpdateNotificationBitmap(bitmap: Bitmap, notificationTag: Int) {
        if (notificationTag == this.notificationTag)
            mainHandler.obtainMessage(MSG_UPDATE_NOTIFICATION_BITMAP, notificationTag, C.INDEX_UNSET, bitmap).sendToTarget()
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {
        val intent = Intent(action).setPackage(context.packageName)
        val pendingFlags = if (SdkIntUtils.isLollipop())
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getBroadcast(context, instanceId, intent, pendingFlags);
    }


    private inner class MediaLargeBitmapAdapter() {

        fun getCurrentLargeIcon(bitmapUrl: String, loadLargeIconBitMapCall: LoadLargeIconBitMapCall): Bitmap? {
            return if (currentIconUri == null || currentIconUri != bitmapUrl) {
                currentIconUri = bitmapUrl
                serviceScope.launch {
                    currentBitmap = bitmapUrl?.let {
                        resolveUriAsBitmap(it)
                    }
                    currentBitmap?.let {
                        loadLargeIconBitMapCall.onBitmap(it)
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

        private val glideOptions = RequestOptions().fallback(R.drawable.loading_logo).diskCacheStrategy(DiskCacheStrategy.DATA)
    }

    inner class LoadLargeIconBitMapCall(private val notificationTag: Int) {

        fun onBitmap(bitmap: Bitmap) {
            bitmap?.let { postUpdateNotificationBitmap(bitmap, notificationTag) }
        }
    }


    private inner class PlayerListener : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED, Player.EVENT_PLAY_WHEN_READY_CHANGED, Player.EVENT_IS_PLAYING_CHANGED, Player.EVENT_TIMELINE_CHANGED, Player.EVENT_PLAYBACK_PARAMETERS_CHANGED,
                    Player.EVENT_POSITION_DISCONTINUITY, Player.EVENT_REPEAT_MODE_CHANGED, Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED, Player.EVENT_MEDIA_METADATA_CHANGED
                )
            ) {
                postStartOrUpdateNotification()
            }
        }
    }

    private inner class NotificationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action ?: "") {
                ACTION_PLAY -> {
                    if (player!!.playbackState == Player.STATE_IDLE) {
                        player?.prepare()
                    } else if (player!!.playbackState == Player.STATE_ENDED) {
                        player?.seekToDefaultPosition(player!!.currentMediaItemIndex)
                    }
                    player?.play()
                }
                ACTION_PAUSE -> {
                    player?.pause()
                }
                ACTION_PREVIOUS -> {
                    notificationsListener?.onNotificationPrev()
                }
                ACTION_NEXT -> {
                    notificationsListener?.onNotificationActionNext()
                }
                ACTION_CONTENT -> {

                }
                else -> {

                }
            }
        }
    }
}