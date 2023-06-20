package com.wgllss.ssmusic.features_ui.page.mv.viewmodel

import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MVPlayData
import com.wgllss.ssmusic.datasource.netbean.mv.KMVItem
import com.wgllss.ssmusic.datasource.repository.KRepository
import kotlinx.coroutines.flow.onEach

class KMVListViewModel : BaseViewModel() {

    val kuGouRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicRepository>
    val liveDataList by lazy { MutableLiveData<MutableList<KMVItem>>() }
    val liveDataMV by lazy { MutableLiveData<MVPlayData>() }
    private var pageNo = 1
    var isLoadingMore = false
    var enableLoadeMore = true

    fun enableLoadMore() = !isLoadingMore && enableLoadeMore
    override fun start() {
    }

    fun kmvList(pathKey: String) {
        isLoadingMore = true
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.kmvList("${pathKey}_$pageNo").onEach {
                if (pageNo == 2) {
                    val list = liveDataList.value!!
                    list.removeAt(list.size - 1)
                    list.addAll(it)
                    enableLoadeMore = false
                    liveDataList.postValue(list)
                } else {
                    enableLoadeMore = !(pageNo == 1 && it.size < 20)
                    liveDataList.postValue(it)
                }
                pageNo = 2
                isLoadingMore = false
            }
        }
    }

    fun getMvData(item: KMVItem) {
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.getMvData(item.linkUrl).onEach {
                val data = MVPlayData(if (it.mvdata.rq != null && it.mvdata.rq.downurl != null) it.mvdata.rq.downurl else it.mvdata.le.downurl, item.title)
                liveDataMV.postValue(data)
            }
        }
    }
}