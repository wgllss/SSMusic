package com.wgllss.ssmusic.features_ui.page.search.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.wgllss.core.ex.flowOnIOAndCatch
import com.wgllss.core.ex.logE
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.core.units.UUIDHelp
import com.wgllss.ssmusic.data.MVPlayData
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.repository.KRepository
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ARTNETWORK_URL_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_AUTHOR_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_TITLE_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_URL_KEY
import com.wgllss.ssmusic.features_system.music.extensions.id
import com.wgllss.ssmusic.features_system.music.extensions.isPlaying
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

//@HiltViewModel
class HomeViewModel3 : BaseViewModel() {
    private val musicServiceConnectionL by lazy { MusicServiceConnection.getInstance(AppGlobals.sApplication) }

    private val kRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicReposito
    private val musicRepositoryL by lazy { MusicRepository.getInstance(AppGlobals.sApplication) }

    val searchContent by lazy { MutableLiveData<String>() }
    val result by lazy { MutableLiveData<MutableList<MusicItemBean>>() }

    val currentMediaID by lazy { MutableLiveData("") }
    val nowPlay by lazy { MutableLiveData<Boolean>() }

//    val rootMediaId: LiveData<String> by lazy {
//        Transformations.map(musicServiceConnectionL.isConnected) { isConnected ->
//            if (isConnected) {
//                LogTimer.LogE(this@HomeViewModel3, "isConnected")
//                musicServiceConnectionL.rootMediaId
//            } else {
//                null
//            }
//        }
//    }

    //播放列表
    val liveData: MutableLiveData<MutableList<MediaBrowserCompat.MediaItem>> by lazy { MutableLiveData<MutableList<MediaBrowserCompat.MediaItem>>() }

    private val transportControls by lazy { musicServiceConnectionL.transportControls }

    private val subscriptionCallback by lazy {
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                liveData.value = children
            }
        }
    }

//    fun mediaItemClicked(clickedItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
//        clickedItem.mediaId?.let {
//            val nowPlaying = musicServiceConnectionL.nowPlaying.value
//            val transportControls = musicServiceConnectionL.transportControls
//            val isPrepared = musicServiceConnectionL.playbackState.value?.isPrepared ?: false
//            if (isPrepared && it == nowPlaying?.id) {
//                //当前正在播放 or 准备播放
//            } else {
//                transportControls.playFromMediaId(it, extras)
//            }
//        }
//    }

    override fun start() {
        musicServiceConnectionL.startConnect()
    }

//    fun subscribeByMediaID(mediaId: String) {
//        musicServiceConnectionL.run {
//            LogTimer.LogE(this@HomeViewModel3, "subscribeByMediaID")
//            subscribe(mediaId, subscriptionCallback)
//            playbackState.observeForever(playbackStateObserver)
//        }
//    }

    private val playbackStateObserver by lazy {
        Observer<PlaybackStateCompat> {
            currentMediaID.postValue(if (it.isPlaying) musicServiceConnectionL.nowPlaying.value?.id ?: "" else "")
        }
    }

    fun searchKeyByTitle() {
        if (searchContent.value == null || searchContent.value.isNullOrEmpty()) {
            WLog.e(this, "searchContent.value ${searchContent.value}")
            return
        }
        flowAsyncWorkOnViewModelScopeLaunch {
            kRepository.searchKeyWord(searchContent.value!!).onEach {
                result.postValue(it)
            }
        }
    }

    fun doPlay(item: MusicItemBean) {
        val nowPlaying = musicServiceConnectionL.nowPlaying.value
        val id = UUIDHelp.getMusicUUID(item.musicName, item.author)
        nowPlaying?.id?.takeIf {
            it.isNotEmpty() && it.toLong() == id
        }?.let {
            nowPlay.postValue(true)
            return
        }
        if (item.privilege == 10 && item.mvhash.isNotEmpty()) {
            playMv(item)
        } else {
            playMusic(item)
        }
    }

    private fun playMv(item: MusicItemBean) {
        viewModelScope.launch {
            kRepository.getMusicInfo(item, true).flowOnIOAndCatch().onStartAndShow().onCompletionAndHide().collect {
                flowAsyncWorkOnViewModelScopeLaunch {
                    val mvUrl = "https://www.kugou.com/mvweb/html/mv_${item.mvhash}.html"
                    kRepository.getMvData(mvUrl).onEach { it2 ->
                        val data = MVPlayData(if (it2.mvdata.rq != null && it2.mvdata.rq.downurl != null) it2.mvdata.rq.downurl else it2.mvdata.le.downurl, item.musicName)
                        val id = UUIDHelp.getMusicUUID(item.musicName, item.author)
                        it.musicLrcStr?.takeIf {
                            it.isNotEmpty()
                        }?.let { lrc ->
                            LrcHelp.saveLrc(id.toString(), lrc)
                        }
                        it.url = data.url
                        transportControls.prepareFromUri(data.url.toUri(), Bundle().apply {
                            putString(MEDIA_ID_KEY, it.id.toString())
                            putString(MEDIA_TITLE_KEY, it.title)
                            putString(MEDIA_AUTHOR_KEY, it.author)
                            putString(MEDIA_ARTNETWORK_URL_KEY, it.pic)
                            putString(MEDIA_URL_KEY, data.url)
                        })
                        nowPlay.postValue(true)
                        WLog.e(this@HomeViewModel3, "it.privilege:${it.privilege} it.requestRealUrl:${it.requestRealUrl} it.url:${it.url}")
                        musicRepositoryL.addToPlayList(it).collect()
                    }
                }
            }
        }
    }

    private fun playMusic(musicItemBean: MusicItemBean) {
        flowAsyncWorkOnViewModelScopeLaunch {
            kRepository.getMusicInfo(musicItemBean)
                .onEach {
                    logE("lrc-11111->${it.musicLrcStr}")
                    it.musicLrcStr?.takeIf {
                        it.isNotEmpty()
                    }?.let { lrc ->
                        LrcHelp.saveLrc(it.id.toString(), lrc)
                    }
                    transportControls.prepareFromUri(it.url.toUri(), Bundle().apply {
                        putString(MEDIA_ID_KEY, it.id.toString())
                        putString(MEDIA_TITLE_KEY, it.title)
                        putString(MEDIA_AUTHOR_KEY, it.author)
                        putString(MEDIA_ARTNETWORK_URL_KEY, it.pic)
                        putString(MEDIA_URL_KEY, it.url)
                    })
                    nowPlay.postValue(true)
                    musicRepositoryL.addToPlayList(it).collect()
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnectionL.playbackState.removeObserver(playbackStateObserver)
    }
}