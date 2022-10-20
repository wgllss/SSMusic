package com.wgllss.ssmusic.features_system.music.impl

import android.media.MediaPlayer
import com.wgllss.ssmusic.features_system.music.IMusicPlay
import com.wgllss.ssmusic.features_system.music.OnPlayCompleteListener
import javax.inject.Inject

class MediaPlayerImpl @Inject constructor(): IMusicPlay {

    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate() {
        mediaPlayer = MediaPlayer()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun onPause() {
        mediaPlayer.pause()
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
//        mediaPlayer.prepare()
    }

    override fun setSource(url: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
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

    override fun isPlaying() = mediaPlayer.isPlaying

    override fun onDestroy() {
        if (isPlaying()) {
            mediaPlayer.pause()
        }
        mediaPlayer.release()
    }
}