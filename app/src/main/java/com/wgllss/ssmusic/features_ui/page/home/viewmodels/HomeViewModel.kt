package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.wgllss.music.datasourcelibrary.data.MusicItemBean
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ARTNETWORK_URL_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_AUTHOR_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_TITLE_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_URL_KEY
import com.wgllss.ssmusic.features_system.music.extensions.id
import com.wgllss.ssmusic.features_system.music.extensions.isPlaying
import com.wgllss.ssmusic.features_system.music.extensions.isPrepared
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val musicRepositoryL: Lazy<MusicRepository>, private val musicServiceConnectionL: Lazy<MusicServiceConnection>) : BaseViewModel() {

    val searchContent by lazy { MutableLiveData<String>() }

    val result by lazy { MutableLiveData<MutableList<MusicItemBean>>() }

    val currentMediaID by lazy { MutableLiveData("") }

    val rootMediaId: LiveData<String> =
        Transformations.map(musicServiceConnectionL.get().isConnected) { isConnected ->
            if (isConnected) {
                WLog.e(this@HomeViewModel, "isConnected $isConnected   musicServiceConnection.rootMediaId：${musicServiceConnectionL.get().rootMediaId} ${Thread.currentThread().name}")
                musicServiceConnectionL.get().rootMediaId
            } else {
                null
            }
        }

    //播放列表
    val liveData: MutableLiveData<MutableList<MediaBrowserCompat.MediaItem>> by lazy { MutableLiveData<MutableList<MediaBrowserCompat.MediaItem>>() }

    private val transportControls by lazy { musicServiceConnectionL.get().transportControls }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            liveData.value = children
        }
    }

    fun mediaItemClicked(clickedItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        clickedItem.mediaId?.let {
            val nowPlaying = musicServiceConnectionL.get().nowPlaying.value
            val transportControls = musicServiceConnectionL.get().transportControls
            val isPrepared = musicServiceConnectionL.get().playbackState.value?.isPrepared ?: false
            if (isPrepared && it == nowPlaying?.id) {
                //当前正在播放 or 准备播放
            } else {
                transportControls.playFromMediaId(it, extras)
            }
        }
    }

    override fun start() {
    }

    fun subscribeByMediaID(mediaId: String) {
        musicServiceConnectionL.get().run {
            WLog.e(this@HomeViewModel, "MusicService    it.subscribe(mediaId, subscriptionCallback) ${Thread.currentThread().name} ")
            subscribe(mediaId, subscriptionCallback)
            playbackState.observeForever(playbackStateObserver)
        }
    }

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        currentMediaID.postValue(if (it.isPlaying) musicServiceConnectionL.get().nowPlaying.value?.id ?: "" else "")
    }

    fun searchKeyByTitle() {
        if (searchContent.value == null || searchContent.value.isNullOrEmpty()) {
            WLog.e(this, "searchContent.value ${searchContent.value}")
            return
        }
        flowAsyncWorkOnViewModelScopeLaunch {
            musicRepositoryL.get().searchKeyByTitle(searchContent.value!!)
                .onEach {
                    result.postValue(it)
                    it.forEach {
                        WLog.e(this@HomeViewModel, "${it.author}  ${it.musicName}  ${it.detailUrl}")
                    }
                }
        }
    }

    fun getDetailFromSearch(position: Int) {
        result?.value?.takeIf {
            it.size > position
        }?.run {
            flowAsyncWorkOnViewModelScopeLaunch {
                val detailUrl = get(position).detailUrl
                musicRepositoryL.get().getPlayUrl(detailUrl)
                    .onEach {
                        val extras = Bundle().apply {
                            putString(MEDIA_ID_KEY, it.id.toString())
                            putString(MEDIA_TITLE_KEY, it.title)
                            putString(MEDIA_AUTHOR_KEY, it.author)
                            putString(MEDIA_ARTNETWORK_URL_KEY, it.pic)
                            putString(MEDIA_URL_KEY, it.url)
                        }
                        transportControls.prepareFromUri(it.url.toUri(), extras)
                        musicRepositoryL.get().addToPlayList(it).collect()
                    }
            }
        }
    }

    fun deleteFromPlayList(id: Long) {
        currentMediaID.value?.takeIf {
            it.toLong() == id
        }?.let {
            errorMsgLiveData.postValue("当前正在播放该歌曲，不能删除")
            return
        }
        flowAsyncWorkOnViewModelScopeLaunch {
            musicRepositoryL.get().deledeFromId(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnectionL.get().playbackState.removeObserver(playbackStateObserver)
    }
}