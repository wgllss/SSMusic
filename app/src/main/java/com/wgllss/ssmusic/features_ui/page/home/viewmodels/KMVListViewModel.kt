package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MVPlayData
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.netbean.mv.KMVItem
import com.wgllss.ssmusic.datasource.repository.KRepository
import kotlinx.coroutines.flow.onEach

class KMVListViewModel : BaseViewModel() {

    val kuGouRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicRepository>

    //    val liveDataList by lazy { MutableLiveData<MutableList<KMVItem>>() }
    val liveDataLoadSuccessCount by lazy { MutableLiveData(0) }
    val liveDataMV by lazy { MutableLiveData<MVPlayData>() }
    var isClick = false
    val result by lazy { mutableMapOf<String, MutableLiveData<MutableList<KMVItem>>>() }

    private var pageNo = 1
    var isLoadingMore = false
    var enableLoadeMore = true

    fun enableLoadMore() = !isLoadingMore && enableLoadeMore
    override fun start() {
    }

    fun initKey(key: String) {
        result[key] = MutableLiveData<MutableList<KMVItem>>()
    }

    fun kmvList(pathKey: String) {
        isLoadingMore = true
        isClick = false
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.kmvList("${pathKey}_$pageNo").onEach {
                if (result[pathKey] == null) {
                    WLog.e(this@KMVListViewModel, pathKey)
                    val list = MutableLiveData<MutableList<KMVItem>>()
                    list.postValue(it)
                    result[pathKey] = list
                } else {
                    if (pageNo >= 2) {
                        val list = result[pathKey]!!.value!!
                        list.removeAt(list.size - 1)
                        list.addAll(it)
                        enableLoadeMore = false
                        result[pathKey]?.postValue(list)
                    } else {
                        enableLoadeMore = !(pageNo == 1 && it.size < 20)
//                        liveDataList.postValue(it)
                        result[pathKey]?.postValue(it)
                    }
                }
                var c = liveDataLoadSuccessCount.value?.plus(1)
                liveDataLoadSuccessCount.postValue(c)
                pageNo = 2
                isLoadingMore = false
            }
        }
    }

    fun getMvData(item: KMVItem) {
        isClick = true
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.getMvData(item.linkUrl).onEach {
                val data = MVPlayData(if (it.mvdata.rq != null && it.mvdata.rq.downurl != null) it.mvdata.rq.downurl else it.mvdata.le.downurl, item.title)
                liveDataMV.postValue(data)
            }
        }
    }
}