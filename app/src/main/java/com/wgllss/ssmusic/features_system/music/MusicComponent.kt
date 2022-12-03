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
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.Util
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.data.MusicBean
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_DURATION_KEY
import com.wgllss.ssmusic.features_system.music.extensions.*
import com.wgllss.ssmusic.features_system.music.notifications.SSNotificationManager
import com.wgllss.ssmusic.features_system.services.MusicService


open class MusicComponent(val context: Context) : LifecycleOwner, MediaSessionConnector.PlaybackPreparer {

    private val mLifecycleRegistry by lazy { LifecycleRegistry(this) }

    private lateinit var notificationManager: SSNotificationManager
    private var isForegroundService = false

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
    private var currentPlaylistItems = mutableListOf<MediaMetadataCompat>()
    private var currentMediaItemIndex: Int = 0

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
            addListener(PlayerEventListener())
        }
    }

    val mediaSession by lazy {
        MediaSessionCompat(context, context.getString(R.string.app_name))
            .apply {
                setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
                val sessionIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                val sessionActivityPendingIntent = PendingIntent.getActivity(context, 0, sessionIntent, 0)
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

        notificationManager = SSNotificationManager(
            musicService,
            mediaSession.sessionToken,
            PlayerNotificationListener()
        )

        mediaSessionConnector.setPlayer(exoPlayer)
        exoPlayer.clearMediaItems()

        notificationManager.showNotificationForPlayer(exoPlayer)
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

    fun onGetRoot() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    open fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>) {

    }

    open fun handleCommandIntent(intent: Intent?) {

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
        logE("onPrepareFromSearch query: $query playWhenReady: $playWhenReady  extras:$extras ")
    }

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
        extras?.run {
            preparePlaylist(
                getString(Constants.MEDIA_ID_KEY) ?: "",
                getString(Constants.MEDIA_TITLE_KEY) ?: "",
                getString(Constants.MEDIA_AUTHOR_KEY) ?: "",
                getString(Constants.MEDIA_ARTNETWORK_URL_KEY) ?: "",
                getString(Constants.MEDIA_URL_KEY) ?: ""
            )
        }
    }

    protected open fun preparePlaylist(mediaId: String, musicTitle: String, author: String, pic: String, url: String) {
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
        currentPlaylistItems = mutableListOf(mediaMetadataCompat)
        exoPlayer.stop()
        exoPlayer.playWhenReady = true
        exoPlayer.setMediaItem(mediaMetadataCompat.toMediaItem())
        exoPlayer.prepare()
    }

    private inner class PlayerEventListener : Player.Listener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING, Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(exoPlayer)
                    if (playbackState == Player.STATE_READY) {
                        if (!playWhenReady) {

                        }
                        setPlaybackState(PlaybackStateCompat.STATE_PLAYING)
                    }
                }
                Player.STATE_ENDED -> {
                    logE("单曲播放结束，可以下一首")
                    playNext()
                }
                else -> notificationManager.hideNotification()
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_POSITION_DISCONTINUITY) || events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) || events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED)) {
                currentMediaItemIndex = if (currentPlaylistItems.isNotEmpty()) {
                    Util.constrainValue(player.currentMediaItemIndex, 0, currentPlaylistItems.size - 1)
                } else 0
            }
        }

        private fun setPlaybackState(playbackState: Int) {
            val speed = exoPlayer.playbackParameters?.speed ?: 1.0f
            mediaSession.setPlaybackState(PlaybackStateCompat.Builder().apply {
                setState(playbackState, exoPlayer.contentPosition, speed)
                    .setActions(supportedPrepareActions)
                setExtras(Bundle().apply {
                    putLong(MEDIA_DURATION_KEY, exoPlayer.duration)
                })
            }.build())
        }
    }

    private inner class SSQueueNavigator(mediaSession: MediaSessionCompat) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat = if (windowIndex < currentPlaylistItems.size) currentPlaylistItems[windowIndex].description
        else MediaDescriptionCompat.Builder().build()

        override fun onSkipToNext(player: Player) {
            playNext()
        }

        override fun onSkipToPrevious(player: Player) {
            playPrevious()
        }

        override fun getSupportedQueueNavigatorActions(player: Player) =
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    }

    protected open fun playNext() {

    }

    protected open fun playPrevious() {

    }


    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    musicService,
                    Intent(musicService, musicService.javaClass)
                )

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
}