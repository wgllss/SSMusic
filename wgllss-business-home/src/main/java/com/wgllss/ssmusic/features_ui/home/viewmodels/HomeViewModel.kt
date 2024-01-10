package com.wgllss.ssmusic.features_ui.home.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.wgllss.core.ex.logE
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.LogTimer
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.core.units.UUIDHelp
import com.wgllss.ssmusic.data.DataContains
import com.wgllss.ssmusic.data.HomeItemBean
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class HomeViewModel : BaseViewModel() {
    private val musicServiceConnectionL by lazy { MusicServiceConnection.getInstance(AppGlobals.sApplication) }
    private val kRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }
    private val musicRepositoryL by lazy { MusicRepository.getInstance(AppGlobals.sApplication) }
    private val currentMediaID by lazy { MutableLiveData("") }
    val mCurrentFragmentTAG by lazy { StringBuilder() }

    val nowPlaying by lazy { MutableLiveData<MediaMetadataCompat>() }
    val playbackState by lazy { MutableLiveData<PlaybackStateCompat>() }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        nowPlaying.postValue(it)
    }

    val nowPlay by lazy { MutableLiveData<Boolean>() }
    var isClick = false
    val lazyTabViewPager2 by lazy { MutableLiveData<Boolean>() }
    var isFirst = true
    val activationType by lazy { MutableLiveData<Int>() }

    val rootMediaId: LiveData<String> by lazy {
        Transformations.map(musicServiceConnectionL.isConnected) { isConnected ->
            if (isConnected) {
                LogTimer.LogE(this@HomeViewModel, "isConnected")
                musicServiceConnectionL.rootMediaId
            } else {
                null
            }
        }
    }

    //播放列表
    val liveData: MutableLiveData<MutableList<MediaBrowserCompat.MediaItem>> by lazy { MutableLiveData<MutableList<MediaBrowserCompat.MediaItem>>() }
    val list by lazy { MutableLiveData<MutableList<HomeItemBean>>() }
    private val transportControls by lazy { musicServiceConnectionL.transportControls }

    private val subscriptionCallback by lazy {
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                liveData.value = children
            }
        }
    }

    fun getMusicInfo(musicItemBean: MusicItemBean) {
        isClick = true
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
                        putString(MEDIA_ID_KEY, it.id.toString())
                        putString(MEDIA_TITLE_KEY, it.title)
                        putString(MEDIA_AUTHOR_KEY, it.author)
                        putString(MEDIA_ARTNETWORK_URL_KEY, it.pic)
                        putString(MEDIA_URL_KEY, it.url)
                    })
                    musicRepositoryL.addToPlayList(it).collect()
                }
        }
    }

    fun lazyTabView() {
        lazyTabViewPager2.value = true
    }

    override fun start() {
        musicServiceConnectionL.run {
            startConnect()
            playbackState.observeForever(playbackStateObserver)
            nowPlaying.observeForever(mediaMetadataObserver)
        }
    }

    fun subscribeByMediaID(mediaId: String) {
        musicServiceConnectionL.run {
            LogTimer.LogE(this@HomeViewModel, "subscribeByMediaID")
            subscribe(mediaId, subscriptionCallback)
            playbackState.observeForever(playbackStateObserver)
        }
    }

    private val playbackStateObserver by lazy {
        Observer<PlaybackStateCompat> {
            playbackState.postValue(it)
            currentMediaID.postValue(if (it.isPlaying) musicServiceConnectionL.nowPlaying.value?.id ?: "" else "")
        }
    }

    fun homeKMusic() {
        isClick = false
        flowAsyncWorkOnViewModelScopeLaunch {
            kRepository.homeKMusic().onEach {
                list.postValue(it)
            }
        }
    }

    fun checkActivation() {
        flowAsyncWorkOnViewModelScopeLaunch {
            musicRepositoryL.checkActivation().onEach {
                when (it) {
                    -1 -> {
                        WLog.e(this@HomeViewModel, "没有激活过,在体验期内")
                        activationType.postValue(it)
                    }
                    -2 -> {
                        WLog.e(this@HomeViewModel, "没有激活过,体验已经到期")
                        activationType.postValue(it)
                    }
                    0 -> {
                        WLog.e(this@HomeViewModel, "激活过")
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnectionL.playbackState.removeObserver(playbackStateObserver)
        musicServiceConnectionL.nowPlaying.removeObserver(mediaMetadataObserver)
    }
}