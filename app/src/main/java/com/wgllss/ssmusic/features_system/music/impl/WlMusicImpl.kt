package com.wgllss.ssmusic.features_system.music.impl

import com.jeremyliao.liveeventbus.LiveEventBus
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.data.livedatabus.MusicEvent
import com.wgllss.ssmusic.features_system.music.IMusicPlay
import com.wgllss.ssmusic.features_system.music.OnPlayCompleteListener
import com.ywl5320.libmusic.WlMusic
import javax.inject.Inject

class WlMusicImpl @Inject constructor() : IMusicPlay {

    private val wlMusic by lazy { WlMusic.getInstance() }

    override fun onCreate() {
//        wlMusic.isPlaying
    }

    override fun start() {
        wlMusic.start()
        LiveEventBus.get(MusicEvent::class.java).post(MusicEvent.PlayerPause)
    }

    override fun onPause() {
        logE("onPause- isPlaying()-> ${isPlaying()}")
        if (isPlaying()) {
            wlMusic.pause()
        }
    }

    override fun onResume() {
        wlMusic.resume()
    }


    override fun playNext(nextUrl: String) {
        wlMusic.playNext(nextUrl)
    }

    override fun playPrevious(previousUrl: String) {
        wlMusic.playNext(previousUrl)
    }

    override fun prePared() {
        setPlayCircle(true)
        wlMusic.setOnPreparedListener {
            start()
        }

        wlMusic.setOnErrorListener { code, msg ->
            WLog.e(this@WlMusicImpl, "code:${code} msg:${msg}")
        }

        wlMusic.setOnLoadListener { load ->
            WLog.e(this@WlMusicImpl, "load:${load} ")
        }

        wlMusic.setOnInfoListener {
//            WLog.e(this@WlMusicImpl, "setOnCompleteListener")
        }

        wlMusic.setOnPauseResumeListener { pause ->
            WLog.e(this@WlMusicImpl, "pause ${pause}")
            if (pause)
                LiveEventBus.get(MusicEvent::class.java).post(MusicEvent.PlayerStart)
            else
                LiveEventBus.get(MusicEvent::class.java).post(MusicEvent.PlayerPause)


        }

        wlMusic.setOnVolumeDBListener {
            WLog.e(this@WlMusicImpl, "setOnVolumeDBListener")
        }
        wlMusic.prePared()
    }

    override fun setOnCompleteListener(listener: OnPlayCompleteListener) {
        wlMusic.setOnCompleteListener {
            listener?.onComplete()
            //播放进度发送
//            WLog.e(this@WlMusicImpl, "setOnInfoListener")
        }
    }

    override fun setSource(url: String) {
        wlMusic.source = url
    }

    override fun setPlayCircle(isCircle: Boolean) {
        wlMusic.isPlayCircle = isCircle
    }

    override fun setVolume(volume: Int) {
        wlMusic.volume = volume
    }

    override fun seek(secds: Int, seekingfinished: Boolean, showTime: Boolean) {
    }

    override fun isPlaying() = wlMusic.isPlaying

    override fun onDestroy() {
        wlMusic.stop()
    }
}