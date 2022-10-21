package com.wgllss.ssmusic.features_system.music.impl

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import com.wgllss.ssmusic.features_system.music.IMusicPlay
import com.wgllss.ssmusic.features_system.music.OnPlayCompleteListener
import com.wgllss.ssmusic.features_system.music.impl.mediaplayer.AudioFocusManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class MediaPlayerImpl @Inject constructor(@ApplicationContext val context: Context) : IMusicPlay {

    private lateinit var mediaPlayer: MediaPlayer

    private val STATE_IDLE = 0
    private val STATE_PREPARING = 1
    private val STATE_PLAYING = 2
    private val STATE_PAUSE = 3
    private var state: Int = STATE_IDLE

    private val audioFocusManager by lazy { AudioFocusManager(context, this) }

    override fun onCreate() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setOnPreparedListener {
            if (isPreparing()) {
                start()
            }
        }
    }

    override fun start() {
        if (!isPreparing() && !isPausing()) {
            return
        }
        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start()
            state = STATE_PLAYING
        }
    }

    override fun onPause() {
        if (isPreparing()) {
            stopPlayer()
        } else if (isPlaying()) {
            pausePlayer()
        } else if (isPausing()) {
            start()
        } else {
            //todo 播放暂停音乐
//            play(getPlayPosition())
        }
    }

    fun pausePlayer() {
        pausePlayer(true)
    }

    fun pausePlayer(abandonAudioFocus: Boolean) {
        if (!isPlaying()) {
            return
        }
        mediaPlayer.pause()
        state = STATE_PAUSE
        if (abandonAudioFocus) {
            audioFocusManager.abandonAudioFocus()
        }
    }

    override fun onResume() {
//        mediaPlayer.r
    }

    override fun playNext(nextUrl: String) {
//        mediaPlayer.
    }

    override fun playPrevious(previousUrl: String) {
//        mediaPlayer.
    }

    override fun prePared() {
//        try {
//            mediaPlayer.prepare()
//            start()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    override fun setSource(url: String) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            state = STATE_PREPARING
//            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setOnCompleteListener(listener: OnPlayCompleteListener) {
        mediaPlayer.setOnCompletionListener {
            listener.onComplete()
        }
    }

    override fun setPlayCircle(isCircle: Boolean) {
//        mediaPlayer
    }

    override fun setVolume(volume: Int) {
        mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
    }

    override fun seek(secds: Int, seekingfinished: Boolean, showTime: Boolean) {
//        mediaPlayer.seekTo(/)
    }

    override fun isPlaying() = state == STATE_PLAYING

    fun isPausing(): Boolean = state == STATE_PAUSE

    fun isPreparing(): Boolean = state == STATE_PREPARING

    fun isIdle(): Boolean = state == STATE_IDLE


    fun stopPlayer() {
        if (isIdle()) {
            return
        }
        pausePlayer()
        mediaPlayer.reset()
        state = STATE_IDLE
    }

    override fun onDestroy() {
        stopPlayer()
    }
}