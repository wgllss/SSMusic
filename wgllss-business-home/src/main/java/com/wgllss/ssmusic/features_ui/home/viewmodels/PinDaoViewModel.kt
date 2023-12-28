package com.wgllss.ssmusic.features_ui.home.viewmodels

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.core.units.UUIDHelp
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.netbean.rank.KRankBean
import com.wgllss.ssmusic.datasource.repository.KRepository
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.music.extensions.id
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class PinDaoViewModel : BaseViewModel() {
    private val musicServiceConnectionL by lazy { MusicServiceConnection.getInstance(AppGlobals.sApplication) }
    private val transportControls by lazy { musicServiceConnectionL.transportControls }
    private val kuGouRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicRepositor
    private val musicRepositoryL by lazy { MusicRepository.getInstance(AppGlobals.sApplication) }
    val nowPlay by lazy { MutableLiveData<Boolean>() }
    val list by lazy { MutableLiveData<MutableList<MusicItemBean>>() }

    override fun start() {
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.pingDao().onEach {
                list.postValue(it)
            }
        }
    }

    fun playPinDaoDetail(item: MusicItemBean) {
        val nowPlaying = musicServiceConnectionL.nowPlaying.value
        val id = UUIDHelp.getMusicUUID(item.musicName, item.author)
        nowPlaying?.id?.takeIf {
            it.isNotEmpty() && it.toLong() == id
        }?.let {
            nowPlay.postValue(true)
            return
        }
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.playPinDaoDetail(item.detailUrl).onEach {
                WLog.e(this@PinDaoViewModel, "lrc ####---222--:${it.musicLrcStr}")
                transportControls.prepareFromUri(it.url.toUri(), Bundle().apply {
                    putString(Constants.MEDIA_ID_KEY, it.id.toString())
                    putString(Constants.MEDIA_TITLE_KEY, it.title)
                    putString(Constants.MEDIA_AUTHOR_KEY, it.author)
                    putString(Constants.MEDIA_ARTNETWORK_URL_KEY, it.pic)
                    putString(Constants.MEDIA_URL_KEY, it.url)
                })
                nowPlay.postValue(true)
                musicRepositoryL.addToPlayList(it).collect()
            }
        }
    }
}