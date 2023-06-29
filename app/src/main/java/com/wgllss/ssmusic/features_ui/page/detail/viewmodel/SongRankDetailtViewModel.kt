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
import com.wgllss.ssmusic.datasource.netbean.sheet.KRankSheetDetailDto
import com.wgllss.ssmusic.datasource.netbean.sheet.KSheetSongBean
import com.wgllss.ssmusic.datasource.repository.KRepository
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.music.extensions.id
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class SongRankDetailtViewModel : BaseViewModel() {
    private val musicServiceConnectionL by lazy { MusicServiceConnection.getInstance(AppGlobals.sApplication) }
    val kRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicReposito
    private val musicRepositoryL by lazy { MusicRepository.getInstance(AppGlobals.sApplication) }
    val nowPlay by lazy { MutableLiveData<Boolean>() }
    private val transportControls by lazy { musicServiceConnectionL.transportControls }
    val songSheetDetail by lazy { MutableLiveData<KRankSheetDetailDto>() }
    val liveDataMV by lazy { MutableLiveData<MVPlayData>() }

    override fun start() {
    }

    fun kSongRankDetail(encodeID: String) {
        flowAsyncWorkOnViewModelScopeLaunch {
            kRepository.kSongRankDetail2(encodeID)
                .onEach {
                    songSheetDetail.postValue(it)
                }
        }
    }

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

    fun getMusicInfo(musicItemBean: MusicItemBean) {
        val nowPlaying = musicServiceConnectionL.nowPlaying.value
        val id = UUIDHelp.getMusicUUID(musicItemBean.musicName, musicItemBean.author)
        if (nowPlaying?.id?.toLong() == id) {
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
                        LrcHelp.savve(id.toString(), lrc)
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