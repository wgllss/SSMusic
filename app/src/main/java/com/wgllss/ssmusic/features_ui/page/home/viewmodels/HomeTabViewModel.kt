package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.wgllss.ssmusic.core.data.DialogBean
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeTabViewModel @Inject constructor(private val musicServiceConnectionL: Lazy<MusicServiceConnection>) : BaseViewModel() {
    @Inject
    lateinit var musicRepositoryL: Lazy<MusicRepository>

    private val transportControls by lazy { musicServiceConnectionL.get().transportControls }

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
            musicRepositoryL.get().homeMusic(key)
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
            musicRepositoryL.get().getPlayUrl(detailUrl)
                .onEach {
                    val extras = Bundle().apply {
                        putString(Constants.MEDIA_ID_KEY, it.id.toString())
                        putString(Constants.MEDIA_TITLE_KEY, it.title)
                        putString(Constants.MEDIA_AUTHOR_KEY, it.author)
                        putString(Constants.MEDIA_ARTNETWORK_URL_KEY, it.pic)
                        putString(Constants.MEDIA_URL_KEY, it.url)
                    }
                    transportControls.prepareFromUri(it.url.toUri(), extras)
                    musicRepositoryL.get().addToPlayList(it).collect()
                }
        }
//        }
    }

}