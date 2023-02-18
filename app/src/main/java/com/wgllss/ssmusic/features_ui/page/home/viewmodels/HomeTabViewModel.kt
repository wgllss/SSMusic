package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.music.datasourcelibrary.data.MusicItemBean
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class HomeTabViewModel : BaseViewModel() {
    private val musicServiceConnectionL by lazy { MusicServiceConnection.getInstance(AppGlobals.sApplication) }

    //    @Inject
    val musicRepositoryL by lazy { MusicRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicRepository>

    private val transportControls by lazy { musicServiceConnectionL.transportControls }

    val liveDataLoadSuccessCount by lazy { MutableLiveData(0) }

    var isClick = false

    val result by lazy { mutableMapOf<String, MutableLiveData<MutableList<MusicItemBean>>>() }

    override fun start() {
    }

    fun initKey(key: String) {
        result[key] = MutableLiveData<MutableList<MusicItemBean>>()
    }

    fun getData(key: String) {
        isClick = false
        flowAsyncWorkOnViewModelScopeLaunch {
            musicRepositoryL.homeMusic(key)
                .onEach {
                    if (result[key] == null) {
                        WLog.e(this@HomeTabViewModel, key)
                        val list = MutableLiveData<MutableList<MusicItemBean>>()
                        list.postValue(it)
                        result[key] = list
                    } else {
                        result[key]?.postValue(it)
                    }
                    var c = liveDataLoadSuccessCount.value?.plus(1)
                    liveDataLoadSuccessCount.postValue(c)
                }
        }
    }

    fun getDetailFromSearch(musicItemBean: MusicItemBean) {
//        result[key]?.value?.takeIf {
//            it.size > position
//        }?.run {
        isClick = true
        flowAsyncWorkOnViewModelScopeLaunch {
            val detailUrl = musicItemBean.detailUrl
            musicRepositoryL.getPlayUrl(detailUrl)
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
//        }
    }

}