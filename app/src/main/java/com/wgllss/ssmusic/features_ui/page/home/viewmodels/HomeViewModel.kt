package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.UUIDHelp
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ARTNETWORK_URL_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_AUTHOR_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_ROOT
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_TITLE_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_URL_KEY
import com.wgllss.ssmusic.features_system.music.extensions.id
import com.wgllss.ssmusic.features_system.music.extensions.isPlayEnabled
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
                musicServiceConnectionL.get().playbackState.value?.let { playbackState ->
                    when {
                        playbackState.isPlaying -> transportControls.pause()
                        playbackState.isPlayEnabled -> transportControls.play()
                        else -> {
                            logE("Playable item clicked but neither play nor pause are enabled! (mediaId=$it)")
                        }
                    }
                }
            } else {
                transportControls.playFromMediaId(it, extras)
            }
        }
    }

    override fun start() {
        val mediaId = MEDIA_ID_ROOT
        musicServiceConnectionL.get().run {
            logE("MusicService    it.subscribe(mediaId, subscriptionCallback) ${Thread.currentThread().name} ")
            subscribe(mediaId, subscriptionCallback)
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

    fun getDetailFromSearch(position: Int) {
        result?.value?.takeIf {
            it.size > position
        }?.run {
            flowAsyncWorkOnLaunch {
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
                        logE("getDetailFromSearch extras--> $extras")
                        transportControls.prepareFromUri(it.url.toUri(), extras)
                        musicRepositoryL.get().addToPlayList(it).collect()
                    }
            }
        }
    }
}