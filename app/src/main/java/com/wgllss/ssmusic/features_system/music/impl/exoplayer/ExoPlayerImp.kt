package com.wgllss.ssmusic.features_system.music.impl.exoplayer

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.features_system.music.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExoPlayerImp @Inject constructor(@ApplicationContext val context: Context) : IMusicPlay {
    private val playerListener = PlayerEventListener()


    private val uAmpAudioAttributes by lazy {
        AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
    }

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(uAmpAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
//            addListener(playerListener)
        }
////            .setMediaSourceFactory(createMediaSourceFactory())
//
//        SimpleExoPlayer.Builder(context).build().apply {
//            setAudioAttributes(uAmpAudioAttributes, true)
//            setHandleAudioBecomingNoisy(true)
//            addListener(playerListener)
//        }
    }

    private lateinit var currentPlayer: Player
    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()
    private var currentMediaItemIndex: Int = 0

    override fun onCreate() {
    }

    override fun start() {
//        exoPlayer.play()
    }

    override fun onPause() {
    }

    override fun onResume() {
    }

    override fun playNext(nextUrl: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(nextUrl)))
    }

    override fun playPrevious(previousUrl: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(previousUrl)))
    }

    override fun prePared() {
        exoPlayer.prepare()
    }

    override fun setSource(url: String) {
        logE("getPlayUrl mediaId setSource $url")
        exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
    }

    override fun setOnCompleteListener(listener: OnPlayCompleteListener) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        logE("onPlaybackStateChanged STATE_ENDED")
                        listener.onComplete()
                    }
                }
            }
        })
    }

    override fun setOnPlayInfoListener(listener: OnPlayInfoListener) {
    }

    override fun setOnLoadListener(listener: OnLoadListener) {
    }

    override fun setOnPreparedListener(listener: OnPreparedListener) {
        listener?.onPrepared()
    }

    override fun setOnPauseResumeListener(listener: OnPauseResumeListener) {
    }

    override fun setPlayCircle(isCircle: Boolean) {
    }

    override fun setVolume(volume: Int) {
    }

    override fun seek(secds: Int, seekingfinished: Boolean, showTime: Boolean) {
    }

    override fun isPlaying(): Boolean = false

    override fun onDestroy() {

    }

    override fun onStop() {
    }

    /**
     * Listen for events from ExoPlayer.
     */
    private inner class PlayerEventListener : Player.Listener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {

                }
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
//                    notificationManager.showNotificationForPlayer(currentPlayer)
//                    if (playbackState == Player.STATE_READY) {
//
//                        // When playing/paused save the current media item in persistent
//                        // storage so that playback can be resumed between device reboots.
//                        // Search for "media resumption" for more information.
//                        saveRecentSongToStorage()
//
//                        if (!playWhenReady) {
//                            // If playback is paused we remove the foreground state which allows the
//                            // notification to be dismissed. An alternative would be to provide a
//                            // "close" button in the notification which stops playback and clears
//                            // the notification.
//                            stopForeground(false)
//                            isForegroundService = false
//                        }
//                    }
                }
                else -> {
//                    notificationManager.hideNotification()
                }
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_POSITION_DISCONTINUITY)
                || events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)
                || events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED)
            ) {
//                currentMediaItemIndex = if (currentPlaylistItems.isNotEmpty()) {
//                    Util.constrainValue(
//                        player.currentMediaItemIndex,
//                        /* min= */ 0,
//                        /* max= */ currentPlaylistItems.size - 1
//                    )
            } else 0
        }

        override fun onPlayerError(error: PlaybackException) {
//        if (error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS
//            || error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND
//        ) {
//            message = R.string.error_media_not_found;
//        }
//        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun switchToPlayer(previousPlayer: Player?, newPlayer: Player) {
        if (previousPlayer == newPlayer) {
            return
        }
        currentPlayer = newPlayer
        if (previousPlayer != null) {
            val playbackState = previousPlayer.playbackState
            if (currentPlaylistItems.isEmpty()) {
                // We are joining a playback session. Loading the session from the new player is
                // not supported, so we stop playback.
                currentPlayer.clearMediaItems()
                currentPlayer.stop()
            } else if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                preparePlaylist(
                    metadataList = currentPlaylistItems,
                    itemToPlay = currentPlaylistItems[currentMediaItemIndex],
                    playWhenReady = previousPlayer.playWhenReady,
                    playbackStartPositionMs = previousPlayer.currentPosition
                )
            }
        }
//        mediaSessionConnector.setPlayer(newPlayer)
        previousPlayer?.stop(/* reset= */true)
    }

    /**
     * Load the supplied list of songs and the song to play into the current player.
     */
    private fun preparePlaylist(
        metadataList: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playWhenReady: Boolean,
        playbackStartPositionMs: Long
    ) {
        // Since the playlist was probably based on some ordering (such as tracks
        // on an album), find which window index to play first so that the song the
        // user actually wants to hear plays first.
        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOf(itemToPlay)
        currentPlaylistItems = metadataList

        currentPlayer.playWhenReady = playWhenReady
        currentPlayer.stop()
        // Set playlist and prepare.
//        currentPlayer.setMediaItems(
//            metadataList.map { it.toMediaItem() }, initialWindowIndex, playbackStartPositionMs
//        )
        currentPlayer.prepare()

        currentPlayer.next()
    }
}