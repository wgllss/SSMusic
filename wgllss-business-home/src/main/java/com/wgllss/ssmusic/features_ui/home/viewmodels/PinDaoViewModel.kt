package com.wgllss.ssmusic.features_ui.home.viewmodels

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.core.units.UUIDHelp
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.netbean.pindao.PinDaoSideBean
import com.wgllss.ssmusic.datasource.netbean.rank.KRankBean
import com.wgllss.ssmusic.datasource.repository.KRepository
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.activation.ActivationUtils
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.music.extensions.id
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class PinDaoViewModel : BaseViewModel() {
    private val musicServiceConnectionL by lazy { MusicServiceConnection.getInstance(AppGlobals.sApplication) }
    private val transportControls by lazy { musicServiceConnectionL.transportControls }
    private val kuGouRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicRepositor
    private val musicRepositoryL by lazy { MusicRepository.getInstance(AppGlobals.sApplication) }
    val nowPlay by lazy { MutableLiveData<Boolean>() }

    val list by lazy { MutableLiveData<MutableList<MusicItemBean>>() }
    private val map by lazy { MutableLiveData<MutableMap<String, MutableList<MusicItemBean>>>() }
    val listSides by lazy { MutableLiveData<MutableList<PinDaoSideBean>>() }

    override fun start() {
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.pingDao().onEach {
                map.postValue(it.map)
                listSides.postValue(it.sides)
                list.postValue(it.map["-1"])
            }
        }
    }

    fun clickItem(dataID: String) {
        list.value = map.value!![dataID]
    }

    fun playPinDaoDetail(item: MusicItemBean) {
        if (ActivationUtils.isUnUsed()) {
            errorMsgLiveData.value = "亲！请您先激活吧"
            return
        }
        val nowPlaying = musicServiceConnectionL.nowPlaying.value
        val id = UUIDHelp.getMusicUUID(item.musicName, item.author, item.dataSourceType)
        nowPlaying?.id?.takeIf {
            it.isNotEmpty() && it.toLong() == id
        }?.let {
            nowPlay.postValue(true)
            return
        }
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.playPinDaoDetail(item.detailUrl).onEach {
                WLog.e(this@PinDaoViewModel, "lrc :${it.musicLrcStr}")
                it.musicLrcStr?.takeIf {
                    it.isNotEmpty()
                }?.let { lrc ->
                    LrcHelp.saveLrc(it.lrcId.toString(), lrc)
                }
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