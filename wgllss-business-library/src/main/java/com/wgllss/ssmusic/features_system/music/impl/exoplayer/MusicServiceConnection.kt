package com.wgllss.ssmusic.features_system.music.impl.exoplayer

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.LogTimer
import com.wgllss.core.units.WLog
import com.wgllss.ssmusic.features_system.music.extensions.*
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection.MediaBrowserConnectionCallback
import com.wgllss.ssmusic.features_system.services.MusicService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class that manages a connection to a [MediaBrowserServiceCompat] instance, typically a
 * [MusicService] or one of its subclasses.
 *
 * Typically it's best to construct/inject dependencies either using DI or, as UAMP does,
 * using [InjectorUtils] in the app module. There are a few difficulties for that here:
 * - [MediaBrowserCompat] is a final class, so mocking it directly is difficult.
 * - A [MediaBrowserConnectionCallback] is a parameter into the construction of
 *   a [MediaBrowserCompat], and provides callbacks to this class.
 * - [MediaBrowserCompat.ConnectionCallback.onConnected] is the best place to construct
 *   a [MediaControllerCompat] that will be used to control the [MediaSessionCompat].
 *
 *  Because of these reasons, rather than constructing additional classes, this is treated as
 *  a black box (which is why there's very little logic here).
 *
 *  This is also why the parameters to construct a [MusicServiceConnection] are simple
 *  parameters, rather than private properties. They're only required to build the
 *  [MediaBrowserConnectionCallback] and [MediaBrowserCompat] objects.
 */
@Singleton
class MusicServiceConnection @Inject constructor(@ApplicationContext context: Context) {
    val isConnected by lazy {
        MutableLiveData<Boolean>().apply { postValue(false) }
    }
//    val networkFailure = MutableLiveData<Boolean>()
//        .apply { postValue(false) }

    val rootMediaId by lazy { mediaBrowser.root }

    val playbackState by lazy {
        MutableLiveData<PlaybackStateCompat>()
            .apply { postValue(EMPTY_PLAYBACK_STATE) }
    }
    val nowPlaying by lazy {
        MutableLiveData<MediaMetadataCompat>()
            .apply { postValue(NOTHING_PLAYING) }
    }

//    val queueData: MutableLiveData<QueueData>

    val transportControls by lazy {
        mediaController.transportControls
    }

    private val mediaBrowserConnectionCallback by lazy { MediaBrowserConnectionCallback(context) }
    private val mediaBrowser by lazy {
        MediaBrowserCompat(context, ComponentName(context, MusicService::class.java), mediaBrowserConnectionCallback, null).apply {
            LogTimer.LogE(this, "MusicService connect")
            connect()
        }
    }

    fun startConnect() {
        mediaBrowser
    }

    private lateinit var mediaController: MediaControllerCompat

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        LogTimer.LogE(this, "mediaBrowser.subscribe")
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    fun sendCommand(command: String, parameters: Bundle?) =
        sendCommand(command, parameters) { _, _ -> }

    fun sendCommand(
        command: String,
        parameters: Bundle?,
        resultCallback: ((Int, Bundle?) -> Unit)
    ) = if (mediaBrowser.isConnected) {
        mediaController.sendCommand(command, parameters, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                resultCallback(resultCode, resultData)
            }
        })
        true
    } else {
        false
    }

    private inner class MediaBrowserConnectionCallback(private val context: Context) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            LogTimer.LogE(this@MusicServiceConnection, "MusicService   onConnected() ")
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }

            isConnected.postValue(true)
        }

        override fun onConnectionSuspended() {
            isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        private var stateL = PlaybackStateCompat.STATE_NONE
        private var mediaID = ""
        private var duration = 0L

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.state.takeIf {
                it != stateL
            }?.let {
                stateL = it
                WLog.e(this@MusicServiceConnection, "onPlaybackStateChanged333: state ${state?.state} position:${state} state extras ${state?.extras}")
                playbackState.postValue(state)
            }
        }

        override fun onMetadataChanged(it: MediaMetadataCompat?) {
            it?.takeIf {
                mediaID != it.id && duration != it.duration
            }?.let {
                mediaID = it.id ?: ""
                duration = it.duration
                nowPlaying.postValue(it)
            }
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        }

        override fun onExtrasChanged(extras: Bundle?) {
            super.onExtrasChanged(extras)
            WLog.e(this@MusicServiceConnection, "onExtrasChanged extras $extras")
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()