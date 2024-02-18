package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.LogTimer
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.core.units.UUIDHelp
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.activation.ActivationUtils
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ARTNETWORK_URL_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_AUTHOR_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_ID_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_TITLE_KEY
import com.wgllss.ssmusic.features_system.globle.Constants.MEDIA_URL_KEY
import com.wgllss.ssmusic.features_system.music.extensions.id
import com.wgllss.ssmusic.features_system.music.extensions.isPlaying
import com.wgllss.ssmusic.features_system.music.extensions.isPrepared
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

//@HiltViewModel
class HomeViewModel2 : BaseViewModel() {
    private val musicServiceConnectionL by lazy { MusicServiceConnection.getInstance(AppGlobals.sApplication) }
    private val musicRepositoryL by lazy { MusicRepository.getInstance(AppGlobals.sApplication) }

    //    val searchContent by lazy { MutableLiveData<String>() }
    var contentCache: String = ""

    val result by lazy { MutableLiveData<MutableList<MusicItemBean>>() }

    val currentMediaID by lazy { MutableLiveData("") }
//    val mCurrentFragmentTAG by lazy { StringBuilder() }

    //    val lazyTabViewPager2 by lazy { MutableLiveData<Boolean>() }
    var isFirst = true
    val nowPlay by lazy { MutableLiveData<Boolean>() }
    private var isLoadingMore = false
    val enableLoadeMore by lazy { MutableLiveData(true) }
    var pageNo = 1
    var isClick = false

    fun enableLoadMore() = !isLoadingMore && enableLoadeMore.value!!

    val rootMediaId: LiveData<String> by lazy {
        Transformations.map(musicServiceConnectionL.isConnected) { isConnected ->
            if (isConnected) {
                LogTimer.LogE(this@HomeViewModel2, "isConnected")
                musicServiceConnectionL.rootMediaId
            } else {
                null
            }
        }
    }

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

    fun mediaItemClicked(clickedItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        clickedItem.mediaId?.let {
            val nowPlaying = musicServiceConnectionL.nowPlaying.value
            val transportControls = musicServiceConnectionL.transportControls
            val isPrepared = musicServiceConnectionL.playbackState.value?.isPrepared ?: false
            if (isPrepared && it == nowPlaying?.id) {
                //当前正在播放 or 准备播放
            } else {
                transportControls.playFromMediaId(it, extras)
            }
        }
    }

//    fun lazyTabView() {
//        lazyTabViewPager2.value = true
//    }

    override fun start() {
        musicServiceConnectionL.startConnect()
    }

    fun subscribeByMediaID(mediaId: String) {
        musicServiceConnectionL.run {
            LogTimer.LogE(this@HomeViewModel2, "subscribeByMediaID")
            subscribe(mediaId, subscriptionCallback)
            playbackState.observeForever(playbackStateObserver)
        }
    }

    private val playbackStateObserver by lazy {
        Observer<PlaybackStateCompat> {
            currentMediaID.postValue(if (it.isPlaying) musicServiceConnectionL.nowPlaying.value?.id ?: "" else "")
        }
    }

    fun initPage() {
        pageNo = 1
    }

    fun searchKeyByTitle(content: String = contentCache) {
        if (content == null || content.isNullOrEmpty()) {
//            WLog.e(this, "searchContent.value ${searchContent.value}")
            return
        }
        contentCache = content
        isLoadingMore = true
        isClick = false
        WLog.e(this, "isLoadingMore : $isLoadingMore")
        flowAsyncWorkOnViewModelScopeLaunch {
            musicRepositoryL.searchKeyByTitle(content, pageNo)
                .onEach {
                    val resultList = if (pageNo == 1) {
                        it.list
                    } else {
                        val list = result.value
                        list?.removeAt(list.size - 1)
                        list?.addAll(it.list)
                        list
                    }
                    result.postValue(resultList)
                    enableLoadeMore.postValue(pageNo < it.maxPage)
                    if (pageNo < it.maxPage)
                        pageNo++
                    isLoadingMore = false
                }
        }
    }

    fun getDetailFromSearch(position: Int) {
        if (ActivationUtils.isUnUsed()) {
            errorMsgLiveData.value = "亲！请您先激活吧"
            return
        }
        result?.value?.takeIf {
            it.size > position
        }?.run {
            val nowPlaying = musicServiceConnectionL.nowPlaying.value
            val id = UUIDHelp.getMusicUUID(get(position).musicName, get(position).author, 0)
            nowPlaying?.id?.takeIf {
                it.isNotEmpty() && it.toLong() == id
            }?.let {
                nowPlay.postValue(true)
                return
            }
            isClick = true
            flowAsyncWorkOnViewModelScopeLaunch {
                val detailUrl = get(position).detailUrl
                musicRepositoryL.getPlayUrl(detailUrl)
                    .onEach {
                        val extras = Bundle().apply {
                            putString(MEDIA_ID_KEY, it.id.toString())
                            putString(MEDIA_TITLE_KEY, "${it.title}(高品质)")
                            putString(MEDIA_AUTHOR_KEY, it.author)
                            putString(MEDIA_ARTNETWORK_URL_KEY, it.pic)
                            putString(MEDIA_URL_KEY, it.url)
                        }
                        transportControls.prepareFromUri(it.url.toUri(), extras)
                        musicRepositoryL.addToPlayList(it).collect()
                    }
            }
        }
    }

    fun deleteFromPlayList(id: Long) {
        currentMediaID.value?.takeIf {
            it.isNotEmpty() && it.toLong() == id
        }?.let {
            errorMsgLiveData.postValue("当前正在播放该歌曲，不能删除")
            return
        }
        flowAsyncWorkOnViewModelScopeLaunch {
            musicRepositoryL.deledeFromId(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnectionL.playbackState.removeObserver(playbackStateObserver)
    }
}