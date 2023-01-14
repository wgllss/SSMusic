package com.wgllss.ssmusic.features_system.music

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.units.SdkIntUtils
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.music.extensions.*
import com.wgllss.ssmusic.features_system.music.notifications.NotificationListener
import com.wgllss.ssmusic.features_system.music.notifications.SSPlayerNotificationManager
import com.wgllss.ssmusic.features_system.services.MusicService
import com.wgllss.ssmusic.features_ui.page.playing.activity.NotificationTargetActivity
import kotlinx.coroutines.*

open class MusicComponent(val context: Context) : LifecycleOwner, MediaSessionConnector.PlaybackPreparer {

    private val mLifecycleRegistry by lazy { LifecycleRegistry(this) }

    private lateinit var notificationManager: SSPlayerNotificationManager

    private var isForegroundService = false
    protected lateinit var musicService: MusicService
    private var currentMediaMetadataCompat: MediaMetadataCompat? = null

    private val serviceJob by lazy { SupervisorJob() }
    protected val serviceScope by lazy { CoroutineScope(Dispatchers.Main + serviceJob) }
    private val playerListener by lazy { PlayerEventListener() }

    private val uAmpAudioAttributes by lazy {
        AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
    }

    val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(uAmpAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
        }
    }

    val mediaSession by lazy {
        MediaSessionCompat(context, context.getString(R.string.app_name))
            .apply {
                setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
                val pendingFlags = if (SdkIntUtils.isLollipop()) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                val sessionIntent = Intent(context, NotificationTargetActivity::class.java)
                val sessionActivityPendingIntent = PendingIntent.getActivity(context, 0, sessionIntent, pendingFlags)
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }
    }

    private val mediaSessionConnector by lazy {
        MediaSessionConnector(mediaSession).apply {
            setPlaybackPreparer(this@MusicComponent)
            setQueueNavigator(SSQueueNavigator(mediaSession))
        }
    }

    override fun getLifecycle() = mLifecycleRegistry

    open fun onCreate(musicService: MusicService) {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        this.musicService = musicService
        serviceScope.launch {
            notificationManager = SSPlayerNotificationManager(musicService, mediaSession, PlayerNotificationListener())
            notificationManager.showNotificationForPlayer(exoPlayer)
            mediaSessionConnector.setPlayer(exoPlayer)
            exoPlayer.clearMediaItems()
        }
    }

    open fun onStart() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    open fun onStop() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        exoPlayer.stop(true)
    }

    open fun onDestory() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        mediaSession.run {
            isActive = false
            release()
        }
        // Cancel coroutines when the service is going away.
        serviceJob.cancel()
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    fun onGetRoot() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    open fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>) {

    }

    override fun onCommand(player: Player, command: String, extras: Bundle?, cb: ResultReceiver?) = false

    override fun getSupportedPrepareActions(): Long =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PLAY_FROM_URI or
                PlaybackStateCompat.ACTION_PREPARE_FROM_URI or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS

    override fun onPrepare(playWhenReady: Boolean) {
    }

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
        WLog.e(this, "onPrepareFromSearch query: $query playWhenReady: $playWhenReady  extras:$extras ")
    }

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
        extras?.run {
            preparePlay(
                getString(Constants.MEDIA_ID_KEY) ?: "",
                getString(Constants.MEDIA_TITLE_KEY) ?: "",
                getString(Constants.MEDIA_AUTHOR_KEY) ?: "",
                getString(Constants.MEDIA_ARTNETWORK_URL_KEY) ?: "",
                getString(Constants.MEDIA_URL_KEY) ?: ""
            )
        }
    }

    protected open fun preparePlay(mediaId: String, musicTitle: String, author: String, pic: String, url: String) {
        val mediaMetadataCompat = MediaMetadataCompat.Builder().apply {
            id = mediaId
            title = musicTitle
            mediaUri = url
            artist = author
            albumArtUri = pic
            downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED
        }.build().apply {
            description.extras?.putAll(bundle)
        }
        currentMediaMetadataCompat = mediaMetadataCompat
        exoPlayer.stop()
        exoPlayer.playWhenReady = true
        exoPlayer.setMediaItem(mediaMetadataCompat.toMediaItem())
        exoPlayer.prepare()
    }

    private inner class PlayerEventListener : Player.Listener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING, Player.STATE_READY -> {
                    if (playbackState == Player.STATE_READY) {
                        if (playWhenReady) {
                        }
                        WLog.e(this@MusicComponent, "onPlayerStateChanged duration: ${exoPlayer.duration}")
                        setPlaybackState(PlaybackStateCompat.STATE_PLAYING)
                    }
                    notificationManager.showNotificationForPlayer(exoPlayer)
                }
                Player.STATE_ENDED -> {
                    WLog.e(this@MusicComponent, "单曲播放结束，可以下一首")
                    playNext()
                }
                else -> {
                    WLog.e(this@MusicComponent, "hideNotification ")
//                    notificationManager.hideNotification()
                }
            }
        }

        private fun setPlaybackState(playbackState: Int) {
            val speed = exoPlayer.playbackParameters?.speed ?: 1.0f
            mediaSession.setPlaybackState(PlaybackStateCompat.Builder().apply {
                setState(playbackState, exoPlayer.contentPosition, speed)
                    .setActions(supportedPrepareActions)
                setExtras(Bundle().apply {
                    putLong(METADATA_KEY_DURATION, exoPlayer.duration)
                })
            }.build())
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            playNext()
            error?.message?.let {
                WLog.e(this@MusicComponent, it)
            }
        }
    }

    private inner class SSQueueNavigator(mediaSession: MediaSessionCompat) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat = currentMediaMetadataCompat?.description ?: MediaDescriptionCompat.Builder().build()

        override fun onSkipToNext(player: Player) = playNext()

        override fun onSkipToPrevious(player: Player) = playPrevious()

        override fun getSupportedQueueNavigatorActions(player: Player) = PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    }

    /**
     * Listen for notification events.
     */
    private inner class PlayerNotificationListener : NotificationListener {
        override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(musicService, Intent(musicService, musicService.javaClass))
                musicService.startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            musicService.stopForeground(true)
            isForegroundService = false
            musicService.stopSelf()
        }
    }

    protected open fun playNext() {

    }

    protected open fun playPrevious() {

    }
}