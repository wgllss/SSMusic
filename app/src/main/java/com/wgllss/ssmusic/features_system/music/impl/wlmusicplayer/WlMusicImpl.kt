package com.wgllss.ssmusic.features_system.music.impl.wlmusicplayer
//
//import com.jeremyliao.liveeventbus.LiveEventBus
//import com.wgllss.ssmusic.core.ex.logE
//import com.wgllss.ssmusic.core.units.WLog
//import com.wgllss.ssmusic.data.livedatabus.MusicEvent
//import com.wgllss.ssmusic.features_system.music.*
//import com.ywl5320.libmusic.WlMusic
//import javax.inject.Inject
//
//class WlMusicImpl @Inject constructor() : IMusicPlay {
//
//    private val wlMusic by lazy { WlMusic.getInstance() }
//
//    override fun onCreate() {
//    }
//
//    override fun start() {
//        wlMusic.start()
//        LiveEventBus.get(MusicEvent::class.java).post(MusicEvent.PlayerPause)
//    }
//
//    override fun onPause() {
//        if (isPlaying()) {
//            wlMusic.pause()
//        }
//    }
//
//    override fun onResume() {
//        wlMusic.resume()
//    }
//
//
//    override fun playNext(nextUrl: String) {
//        wlMusic.playNext(nextUrl)
//    }
//
//    override fun playPrevious(previousUrl: String) {
//        wlMusic.playNext(previousUrl)
//    }
//
//    override fun prePared() {
//        wlMusic.prePared()
//    }
//
//    override fun setOnPreparedListener(listener: OnPreparedListener) = wlMusic.setOnPreparedListener {
//        listener.onPrepared()
//    }
//
//    override fun setOnPauseResumeListener(listener: OnPauseResumeListener) = wlMusic.setOnPauseResumeListener {
//        listener.onPause(it)
//    }
//
//    override fun setOnCompleteListener(listener: OnPlayCompleteListener) = wlMusic.setOnCompleteListener {
//        listener?.onComplete()
//    }
//
//    override fun setOnPlayInfoListener(listener: OnPlayInfoListener) = wlMusic.setOnInfoListener {
//        listener.onPlayInfo(it.currSecs, it.totalSecs)
//    }
//
//
//    override fun setOnLoadListener(listener: OnLoadListener) = wlMusic.setOnLoadListener {
//        listener.onLoad(it)
//    }
//
//    override fun setSource(url: String) {
//        wlMusic.source = url
//    }
//
//    override fun setPlayCircle(isCircle: Boolean) {
//        wlMusic.isPlayCircle = isCircle
//    }
//
//    override fun setVolume(volume: Int) {
//        wlMusic.volume = volume
//    }
//
//    override fun seek(secds: Int, seekingfinished: Boolean, showTime: Boolean) = wlMusic.seek(secds, seekingfinished, showTime)
//
//    override fun isPlaying() = wlMusic.isPlaying
//
//    override fun onDestroy() {
//        wlMusic.stop()
//    }
//
//    override fun onStop() = wlMusic.stop()
//}