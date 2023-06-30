package com.wgllss.ssmusic.features_ui.page.detail.viewmodel

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.wgllss.core.ex.logE
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.core.units.UUIDHelp
import com.wgllss.ssmusic.data.MVPlayData
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.netbean.sheet.KSheetDetailDto
import com.wgllss.ssmusic.datasource.netbean.sheet.KSheetSongBean
import com.wgllss.ssmusic.datasource.repository.KRepository
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.music.extensions.id
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class SongSheetViewModel : BaseViewModel() {
    private val musicServiceConnectionL by lazy { MusicServiceConnection.getInstance(AppGlobals.sApplication) }
    private val kRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicReposito
    val nowPlay by lazy { MutableLiveData<Boolean>() }
    private val musicRepositoryL by lazy { MusicRepository.getInstance(AppGlobals.sApplication) }
    private val transportControls by lazy { musicServiceConnectionL.transportControls }
    val songSheetDetail by lazy { MutableLiveData<KSheetDetailDto>() }
    val liveDataMV by lazy { MutableLiveData<MVPlayData>() }

    override fun start() {
    }

    fun kSongSheetDetail(encodeID: String) {
        flowAsyncWorkOnViewModelScopeLaunch {
            kRepository.kSongSheetDetail1(encodeID)
                .onEach {
                    songSheetDetail.postValue(it)
                }
        }
    }

//    fun kSongRankDetail(encodeID: String) {
//        flowAsyncWorkOnViewModelScopeLaunch {
//            kuGouRepository.kSongRankDetail(encodeID)
//                .onEach {
//                    songSheetDetail.postValue(it)
//                }
//        }
//    }

    fun doPlay(item: MusicItemBean) {
        if (item.privilege == 10 && item.mvhash.isNotEmpty()) {
            playMv(item)
        } else {
            getMusicInfo(item)
        }
    }

    private fun playMv(item: MusicItemBean) {
        flowAsyncWorkOnViewModelScopeLaunch {
            val mvUrl = "https://www.kugou.com/mvweb/html/mv_${item.mvhash}.html"
            kRepository.getMvData(mvUrl).onEach {
                val data = MVPlayData(if (it.mvdata.rq != null && it.mvdata.rq.downurl != null) it.mvdata.rq.downurl else it.mvdata.le.downurl, item.musicName)
                liveDataMV.postValue(data)
            }
        }
    }

    private fun getMusicInfo(musicItemBean: MusicItemBean) {
        val nowPlaying = musicServiceConnectionL.nowPlaying.value
        val id = UUIDHelp.getMusicUUID(musicItemBean.musicName, musicItemBean.author)
        nowPlaying?.id?.takeIf {
            it.isNotEmpty() && it.toLong() == id
        }?.let {
            nowPlay.postValue(true)
            return
        }
        flowAsyncWorkOnViewModelScopeLaunch {
            kRepository.getMusicInfo(musicItemBean)
                .onEach {
                    logE("lrc-11111->${it.musicLrcStr}")
                    it.musicLrcStr?.takeIf {
                        it.isNotEmpty()
                    }?.let { lrc ->
                        LrcHelp.saveLrc(id.toString(), lrc)
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