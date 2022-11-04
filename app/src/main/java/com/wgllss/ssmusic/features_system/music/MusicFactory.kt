package com.wgllss.ssmusic.features_system.music

import android.content.Intent
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.data.livedatabus.MusicBeanEvent
import com.wgllss.ssmusic.data.livedatabus.MusicEvent
import com.wgllss.ssmusic.data.livedatabus.PlayerEvent
import com.wgllss.ssmusic.dl.annotations.BindMediaPlayer
import com.wgllss.ssmusic.dl.annotations.BindWlMusic
import com.wgllss.ssmusic.features_system.app.AppViewModel
import dagger.Lazy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 音乐播放工厂，处理多音乐功能，
 * musicPlay:主持音乐各种操作
 * appViewModel:主持提供各种数据
 */
class MusicFactory @Inject constructor(@BindWlMusic private val musicPlay: Lazy<IMusicPlay>, private val appViewModel: Lazy<AppViewModel>) : MusicLifcycle() {

    private lateinit var playerProgress: MusicEvent.PlayerProgress
    private lateinit var playerLoadding: MusicEvent.PlayerLoadding
    private lateinit var playerStart: MusicEvent.PlayerStart
    private lateinit var playerPause: MusicEvent.PlayerPause

    private var jobc: Job? = null
    private var jobPlay: Job? = null
    private var currentUrl: String? = null

    override fun onCreate() {
        super.onCreate()
        jobc = GlobalScope.launch {
            musicPlay.get().onCreate()
            LiveEventBus.get(MusicBeanEvent::class.java).observeForever {
                jobPlay = GlobalScope.launch {
                    onMusicDo(it)
                }
            }
            LiveEventBus.get(PlayerEvent::class.java).observeForever {
                when (it) {
                    is PlayerEvent.PlayEvent -> {
                        if (it.pause) musicPlay.get().onPause() else musicPlay.get().onResume()
                    }
                    is PlayerEvent.PlayNext -> {
                        appViewModel.get().playNext()
                    }
                    is PlayerEvent.PlayPrevious -> {
                        appViewModel.get().playPrevious()
                    }
                    is PlayerEvent.SeekEvent -> {
                        musicPlay.get().seek(it.position, it.seekingfinished, it.showTime)
                    }
                    else -> {

                    }
                }
            }
        }
        appViewModel.get().currentPposition.observeForever {
            appViewModel.get().getDetail(it)
        }
    }

    fun handlerIntent(intent: Intent) {
    }

    override fun onDestory() {
        super.onDestory()
        jobc?.cancel()
        jobPlay?.cancel()
        musicPlay.get().onDestroy()
    }

    //处理音乐播放
    fun onMusicDo(it: MusicBeanEvent) {
        it.run {
            if (currentUrl.isNullOrEmpty()) {
                currentUrl = url
                currentUrl?.let {
                    musicPlay.get().apply {
                        setSource(it)
                        setVolume(100)
                        prePared()
                        setOnPreparedListener(object : OnPreparedListener {
                            override fun onPrepared() {
                                musicPlay.get().start()
                            }
                        })
                        setOnCompleteListener(object : OnPlayCompleteListener {
                            override fun onComplete() {
                                appViewModel.get().playNext()
                            }
                        })
                        setOnLoadListener(object : OnLoadListener {
                            override fun onLoad(load: Boolean) {
                                if (!this@MusicFactory::playerLoadding.isInitialized) {
                                    playerLoadding = MusicEvent.PlayerLoadding(load)
                                } else {
                                    playerLoadding.loadding = load
                                }
                                LiveEventBus.get(MusicEvent::class.java).post(playerLoadding)
                            }
                        })
                        setOnPlayInfoListener(object : OnPlayInfoListener {
                            override fun onPlayInfo(currSecs: Int, totalSecs: Int) {
                                if (totalSecs > 0) {
                                    if (!this@MusicFactory::playerProgress.isInitialized) {
                                        playerProgress = MusicEvent.PlayerProgress(currSecs, totalSecs)
                                    } else {
                                        playerProgress!!.totalSecs = totalSecs
                                        playerProgress!!.currSecs = currSecs
                                    }
                                    LiveEventBus.get(MusicEvent::class.java).post(playerProgress)
                                }
                            }
                        })
                        setOnPauseResumeListener(object : OnPauseResumeListener {
                            override fun onPause(pause: Boolean) {
                                if (!this@MusicFactory::playerStart.isInitialized) {
                                    playerStart = MusicEvent.PlayerStart
                                }
                                if (!this@MusicFactory::playerPause.isInitialized) {
                                    playerPause = MusicEvent.PlayerPause
                                }
                                LiveEventBus.get(MusicEvent::class.java).post(if (pause) playerStart else playerPause)
                            }
                        })
                    }
                }
                appViewModel.get().addToPlayList(it)
            } else {
                if (currentUrl == url) {
                    if (musicPlay.get().isPlaying()) {
                        return@run
                    } else {
                        musicPlay.get().playNext(url)
                    }
                } else {
                    currentUrl = url
                    currentUrl?.run {
                        musicPlay.get().playNext(this)
                    }
                    appViewModel.get().addToPlayList(it)
                }
            }
            it.run {
                LiveEventBus.get(MusicEvent::class.java).post(MusicEvent.ChangeMusic(pic, title, author))
            }
        }
    }
}