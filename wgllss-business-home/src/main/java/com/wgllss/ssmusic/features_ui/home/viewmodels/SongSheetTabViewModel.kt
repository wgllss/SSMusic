package com.wgllss.ssmusic.features_ui.home.viewmodels

import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.datasource.netbean.sheet.KSheetListDtoPlistListItem
import com.wgllss.ssmusic.datasource.repository.KRepository
import kotlinx.coroutines.flow.onEach

class SongSheetTabViewModel : BaseViewModel() {
    val kuGouRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicRepository>

    val result by lazy { MutableLiveData<MutableList<KSheetListDtoPlistListItem>>() }
    private var pageNo = 1
    var isLoadingMore = false
    val enableLoadeMore by lazy { MutableLiveData(true) }

    override fun start() {
    }

    fun enableLoadMore() = !isLoadingMore && enableLoadeMore.value!!

    fun homeKuGouSongSheet() {
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.homeKSongSheet().onEach {
                result.postValue(it)
                pageNo = 2
            }
        }
    }

    fun homeKSongSheetLoadMore() {
        isLoadingMore = true
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.homeKSongSheetLoadMore(pageNo).onEach {
                it?.plist?.list?.run {
                    total.takeIf { t ->
                        t > 0
                    }?.run {
                        val list = result.value!!
                        list.removeAt(list.size - 1)
                        list.addAll(info)
                        result.postValue(list)
                    }
                    if (has_next == 1) {
                        enableLoadeMore.postValue(true)
                        pageNo++
                    } else
                        enableLoadeMore.postValue(false)
                    isLoadingMore = false
                }
            }
        }
    }
}