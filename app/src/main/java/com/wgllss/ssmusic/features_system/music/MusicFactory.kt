package com.wgllss.ssmusic.features_system.music

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.UUIDHelp
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.data.MusicBean
import com.wgllss.ssmusic.data.livedatabus.MusicBeanEvent
import com.wgllss.ssmusic.dl.annotations.BindMediaPlayer
import com.wgllss.ssmusic.dl.annotations.BindWlMusic
import com.wgllss.ssmusic.features_system.app.AppViewModel
import com.wgllss.ssmusic.features_system.room.SSDataBase
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
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
class MusicFactory @Inject constructor(@BindWlMusic private val musicPlay: Lazy<IMusicPlay>, private val appViewModel: Lazy<AppViewModel>, private val mSSDataBaseL: Lazy<SSDataBase>) : MusicLifcycle() {

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


    private var job: Job? = null
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
        job?.cancel()
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
    }

    private fun setStore(it: MusicBeanEvent) {
        it.run {
            when (it.playFrom) {
//                0 -> {//搜索过来播放
//                    appViewModel.get().addToPlayList(it)
//                }
                1 -> {//播放列表点击播放
                    MMKVHelp.setPlayID(it.uuid)
                }
                else -> {
                }
            }
        }
    }
}