package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.data.livedatabus.MusicBeanEvent
import com.wgllss.ssmusic.data.livedatabus.PlayerEvent
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ARTNETWORK_URL_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_AUTHOR_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_ROOT
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_TITLE_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_URL_KEY
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val musicRepositoryL: Lazy<MusicRepository>, private val musicServiceConnectionL: Lazy<MusicServiceConnection>) : BaseViewModel() {
    val playUIToFront by lazy { MutableLiveData<PlayerEvent.PlayUIToFront>() }

    val searchContent by lazy { MutableLiveData<String>() }
    val result by lazy { MutableLiveData<MutableList<MusicItemBean>>() }

    //播放列表
    val liveData: MutableLiveData<MutableList<MediaBrowserCompat.MediaItem>> by lazy { MutableLiveData<MutableList<MediaBrowserCompat.MediaItem>>() }

    private val transportControls by lazy { musicServiceConnectionL.get().transportControls }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            liveData.value = children
        }
    }

    fun mediaItemClicked(clickedItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        logE("clickedItem.isBrowsable : ${clickedItem.isBrowsable}")
        if (clickedItem.isBrowsable) {
//            browseToItem(clickedItem)
        } else {
            clickedItem.mediaId?.let { playMediaId(it) }
        }
    }

    fun playMediaId(mediaId: String) {
//        val nowPlaying = musicServiceConnection.nowPlaying.value
//        val transportControls = musicServiceConnection.transportControls
//
//        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared ?: false
//        if (isPrepared && mediaId == nowPlaying?.id) {
//            musicServiceConnection.playbackState.value?.let { playbackState ->
//                when {
//                    playbackState.isPlaying -> transportControls.pause()
//                    playbackState.isPlayEnabled -> transportControls.play()
//                    else -> {
//                        Log.w(
//                            TAG, "Playable item clicked but neither play nor pause are enabled!" +
//                                    " (mediaId=$mediaId)"
//                        )
//                    }
//                }
//            }
//        } else {
        transportControls.playFromMediaId(mediaId, null)
//        }
    }


    override fun start() {
        val mediaId = MEDIA_ID_ROOT
        musicServiceConnectionL.get().run {
            logE("MusicService    it.subscribe(mediaId, subscriptionCallback) ${Thread.currentThread().name} ")
            subscribe(mediaId, subscriptionCallback)
//            playbackState.observeForever(playbackStateObserver)
//            nowPlaying.observeForever(mediaMetadataObserver)
        }
    }

    fun searchKeyByTitle() {
        if (searchContent.value == null || searchContent.value.isNullOrEmpty()) {
            WLog.e(this, "searchContent.value ${searchContent.value}")
            return
        }
        flowAsyncWorkOnLaunch {
            musicRepositoryL.get().searchKeyByTitle(searchContent.value!!)
                .onEach {
                    result.postValue(it)
                    it.forEach {
                        WLog.e(this@HomeViewModel, "${it.author}  ${it.musicName}  ${it.detailUrl}")
                    }
                }
        }
    }

//    fun getDetail(tableIt: MusicTabeBean) {
//        flowAsyncWorkOnLaunch {
//            musicRepositoryL.get().getPlayUrl(tableIt.url)
//                .onEach {
//                    LiveEventBus.get(MusicBeanEvent::class.java).post(MusicBeanEvent(it.title, it.author, tableIt.url, it.pic, it.url, uuid = tableIt.id))
//                }
//        }
//    }

    fun getDetailFromSearch(position: Int) {
        result?.value?.takeIf {
            it.size > position
        }?.run {
            flowAsyncWorkOnLaunch {
                musicRepositoryL.get().getPlayUrl(get(position).detailUrl)
                    .onEach {
                        val extras = Bundle().apply {
                            putString(MEDIA_TITLE_KEY, it.title)
                            putString(MEDIA_AUTHOR_KEY, it.author)
                            putString(MEDIA_ARTNETWORK_URL_KEY, it.pic)
                            putString(MEDIA_URL_KEY, it.url)
                        }
                        transportControls.prepareFromUri(it.url.toUri(), extras)
//                        LiveEventBus.get(MusicBeanEvent::class.java).post(MusicBeanEvent(it.title, it.author, get(position).detailUrl, it.pic, it.url))
                    }
            }
        }
    }

    fun onResume() {
        if (playUIToFront.value == null) {
            playUIToFront.value = PlayerEvent.PlayUIToFront(true)
        } else {
            playUIToFront.value!!.isFront = true
        }
        logE("onResume ${playUIToFront.value!!.isFront}")
        LiveEventBus.get(PlayerEvent::class.java).post(playUIToFront.value)
    }

    fun onStop() {
//        musicServiceConnection.get()
        if (playUIToFront.value == null) {
            playUIToFront.value = PlayerEvent.PlayUIToFront(false)
        } else {
            playUIToFront.value!!.isFront = false
        }
        LiveEventBus.get(PlayerEvent::class.java).post(playUIToFront.value)
    }
}