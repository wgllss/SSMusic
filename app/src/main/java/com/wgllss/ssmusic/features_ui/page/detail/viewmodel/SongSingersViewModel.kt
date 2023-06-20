package com.wgllss.ssmusic.features_ui.page.detail.viewmodel

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.datasource.netbean.singer.KSingerInfo
import com.wgllss.ssmusic.datasource.netbean.singer.KSingerSongBean
import com.wgllss.ssmusic.datasource.repository.KRepository
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import java.util.concurrent.ConcurrentHashMap

class SongSingersViewModel : BaseViewModel() {
    private val musicServiceConnectionL by lazy { MusicServiceConnection.getInstance(AppGlobals.sApplication) }
    private val musicRepositoryL by lazy { MusicRepository.getInstance(AppGlobals.sApplication) }
    val kRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicReposito
    val nowPlay by lazy { MutableLiveData<Boolean>() }
    private val transportControls by lazy { musicServiceConnectionL.transportControls }

    //    val songSheetDetail by lazy { MutableLiveData<SongSheetDetailDto>() }
    val singerInfo by lazy { MutableLiveData<KSingerInfo>() }
    private val map by lazy { ConcurrentHashMap<String, KSingerSongBean>() }
    val listLiveData by lazy { MutableLiveData<MutableList<KSingerSongBean>>() }

    override fun start() {
    }

    fun kSingerInfo(encodeID: String, singerName: String) {
        flowAsyncWorkOnViewModelScopeLaunch {
            kRepository.kSingerInfo(encodeID)
                .zip(musicRepositoryL.searchKeyByTitle(singerName)) { it1, it2 ->
                    singerInfo.postValue(it1.info)
                    it1.songs?.list?.forEach {
                        map[it.audio_name] = it
                    }
                    val list = mutableListOf<KSingerSongBean>()
                    it2.forEach {
                        list.add(KSingerSongBean(it.musicName, it.detailUrl, it.author, if (map.containsKey(it.author)) map[it.author]?.mvhash ?: "" else ""))
                    }
                    listLiveData.postValue(list)
                }
        }
    }



    fun getPlayUrl(item: KSingerSongBean) {
        flowAsyncWorkOnViewModelScopeLaunch {
            musicRepositoryL.getPlayUrl(item.song_url)
                .onEach {
                    val extras = Bundle().apply {
                        putString(Constants.MEDIA_ID_KEY, it.id.toString())
                        putString(Constants.MEDIA_TITLE_KEY, it.title)
                        putString(Constants.MEDIA_AUTHOR_KEY, it.author)
                        putString(Constants.MEDIA_ARTNETWORK_URL_KEY, it.pic)
                        putString(Constants.MEDIA_URL_KEY, it.url)
                    }
                    transportControls.prepareFromUri(it.url.toUri(), extras)
                    musicRepositoryL.addToPlayList(it).collect()
                }
        }
    }

//    fun getMusicInfo(kMusicItemBean: KMusicItemBean) {
//        val nowPlaying = musicServiceConnectionL.nowPlaying.value
//        val id = kMusicItemBean.detailUrl.hashCode().toString()
//        if (nowPlaying?.id == id) {
//            nowPlay.postValue(true)
//            return
//        }
//        flowAsyncWorkOnViewModelScopeLaunch {
//            KuGouRepository.getInstance(AppGlobals.sApplication).getMusicInfo(kMusicItemBean.detailUrl)
//                .onEach {
//                    it.musicLrcStr?.takeIf {
//                        it.isNotEmpty()
//                    }?.let { lrc ->
//                        LrcHelp.savve(id, lrc)
//                    }
//
//                    val extras = Bundle().apply {
//                        putString(Constants.MEDIA_ID_KEY, id)
//                        putString(Constants.MEDIA_TITLE_KEY, kMusicItemBean.musicName)
//                        putString(Constants.MEDIA_AUTHOR_KEY, kMusicItemBean.author)
//
//                        putString(Constants.MEDIA_ARTNETWORK_URL_KEY, kMusicItemBean.imgUrl.ifEmpty { it.sTdMusicUrl })
//                        putString(Constants.MEDIA_URL_KEY, it.musicFileUrl)
//                    }
//                    transportControls.prepareFromUri(it.musicFileUrl.toUri(), extras)
//                    nowPlay.postValue(true)
//                }
//        }
//    }
}