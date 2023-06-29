package com.wgllss.ssmusic.features_ui.page.classics.viewmodels

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MusicItemBean
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
    var isLoadOffine = false
    val result by lazy { mutableMapOf<String, MutableLiveData<MutableList<MusicItemBean>>>() }
    private val pageNoMap by lazy { mutableMapOf<String, Int>() }
    private val isLoadingMore by lazy { mutableMapOf<String, Boolean>() }
    val enableLoadeMore by lazy { mutableMapOf<String, MutableLiveData<Boolean>>() }

    fun enableLoadMore(key: String) = !isLoadingMore[key]!! && enableLoadeMore[key]!!.value!!

    override fun start() {
    }

    fun initKey(key: String) {
        result[key] = MutableLiveData<MutableList<MusicItemBean>>()
        isLoadingMore[key] = false
        enableLoadeMore[key] = MutableLiveData(true)
        pageNoMap[key] = 1
    }

    fun reset(key: String) {
        pageNoMap[key] = 1
    }

    fun getData(key: String) {
        isLoadingMore[key] = true
        isClick = false
        flowAsyncWorkOnViewModelScopeLaunch {
            val homeTag = "$key-${pageNoMap[key]}"
            WLog.e(this@HomeTabViewModel, "homeTag $homeTag")
            musicRepositoryL.homeMusic(homeTag)
                .onEach {
                    if (!isLoadOffine) {
                        if (pageNoMap[key] == 1) {
                            WLog.e(this@HomeTabViewModel, key)
                            result[key]?.postValue(it.list)
                        } else {
                            val list = result[key]?.value
                            list?.removeAt(list.size - 1)
                            list?.addAll(it.list)
                            result[key]?.postValue(list)
                        }
                        enableLoadeMore[key]?.postValue(pageNoMap[key]!! < it.maxPage)
                        if (pageNoMap[key]!! < it.maxPage)
                            pageNoMap[key] = pageNoMap[key]!!.plus(1)
                    } else isLoadOffine = false
                    var c = liveDataLoadSuccessCount.value?.plus(1)
                    liveDataLoadSuccessCount.postValue(c)
                    isLoadingMore[key] = false
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