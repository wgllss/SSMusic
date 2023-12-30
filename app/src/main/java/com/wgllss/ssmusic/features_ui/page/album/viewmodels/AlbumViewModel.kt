package com.wgllss.ssmusic.features_ui.page.album.viewmodels

import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.netbean.album.AlbumBean
import com.wgllss.ssmusic.datasource.netbean.mv.KMVItem
import com.wgllss.ssmusic.datasource.repository.KRepository
import kotlinx.coroutines.flow.onEach

class AlbumViewModel : BaseViewModel() {

    private val kuGouRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }
    val result by lazy { mutableMapOf<String, MutableLiveData<MutableList<AlbumBean>>>() }

    val enableLoadeMore by lazy { mutableMapOf<String, MutableLiveData<Boolean>>() }
    private val pageNoMap by lazy { mutableMapOf<String, Int>() }
    val liveDataLoadSuccessCount by lazy { MutableLiveData(0) }
    var isClick = false

    private val isLoadingMore by lazy { mutableMapOf<String, Boolean>() }
    fun enableLoadMore(key: String) = !isLoadingMore[key]!! && enableLoadeMore[key]!!.value!!

    fun initKey(key: String) {
        result[key] = MutableLiveData<MutableList<AlbumBean>>()
        isLoadingMore[key] = false
        enableLoadeMore[key] = MutableLiveData(true)
        pageNoMap[key] = 1
    }

    override fun start() {
//        flowAsyncWorkOnViewModelScopeLaunch {
//            kuGouRepository.queryAlbumList(1, "")
//        }
    }

    fun reset(key: String) {
        pageNoMap[key] = 1
    }

    fun getData(key: String) {
        isLoadingMore[key] = true
        isClick = false
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.queryAlbumList(pageNoMap[key]!!, key)
                .onEach {
                    if (pageNoMap[key] == 1) {
                        result[key]?.postValue(it)
                    } else {
                        val list = result[key]?.value
                        list?.removeAt(list.size - 1)
                        list?.addAll(it)
                        result[key]?.postValue(list)
                    }
                    enableLoadeMore[key]?.postValue(it.size >= 10 && pageNoMap[key]!! < 5)
                    if (it.size >= 10)
                        pageNoMap[key] = pageNoMap[key]!!.plus(1)
                    else if (it.size == 0) {
                        pageNoMap[key] = pageNoMap[key]!!.minus(1)
                    }
                    var c = liveDataLoadSuccessCount.value?.plus(1)
                    liveDataLoadSuccessCount.postValue(c)
                    isLoadingMore[key] = false
                }
        }
    }
}