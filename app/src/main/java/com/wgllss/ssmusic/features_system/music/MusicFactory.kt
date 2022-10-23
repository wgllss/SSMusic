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

    //正常播放
    private val music_status_paly_numal = 0

    //播放下一曲
    private val music_status_paly_next = 1

    //播放上一曲
    private val music_status_paly_previous = 2

    //暂停
    private val music_status_paly_pause = 3

    //停止
    private val music_status_paly_stop = 4

    //继续播放
    private val music_status_paly_resume = 5


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
                        logE("it.pause ---${it.pause}")
                        if (it.pause)
                            musicPlay.get().onPause()
                        else
                            musicPlay.get().onResume()
                    }
                    is PlayerEvent.PlayNext -> {
                        appViewModel.get().playNext()
                    }
                    is PlayerEvent.PlayPrevious -> {
                        appViewModel.get().playPrevious()
                    }
                    else -> {

                    }
                }
            }
        }
        appViewModel.get().currentPposition.observeForever {
            logE("播放：position:${it}")
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
        when (it.musicType) {
            music_status_paly_numal -> {//从搜索结果 过来正常播放
                it.run {
                    if (currentUrl.isNullOrEmpty()) {
                        currentUrl = url
                        currentUrl?.let {
                            musicPlay.get().apply {
                                setSource(it)
                                setVolume(100)
                                prePared()
                                setOnCompleteListener(object : OnPlayCompleteListener {
                                    override fun onComplete() {
                                        appViewModel.get().playNext()
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
                }
            }
            music_status_paly_next -> {//播放下一首

            }
        }
        it.run {
            LiveEventBus.get(MusicEvent::class.java).post(MusicEvent.ChangeMusic(pic, title, author))
        }
    }
}