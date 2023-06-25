package com.wgllss.ssmusic.features_ui.home.viewmodels

import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.datasource.netbean.rank.KRankBean
import com.wgllss.ssmusic.datasource.repository.KRepository
import kotlinx.coroutines.flow.onEach

class RankViewModel : BaseViewModel() {
    val kuGouRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicRepositor
    val list by lazy { MutableLiveData<MutableList<KRankBean>>() }
    val liveDataLoadSuccessCount by lazy { MutableLiveData(0) }
    var isClick = false
    override fun start() {
        isClick = false
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.kRankList().onEach {
                list.postValue(it)
                var c = liveDataLoadSuccessCount.value?.plus(1)
                liveDataLoadSuccessCount.postValue(c)
            }
        }
    }

//    fun kRankList() {
//        flowAsyncWorkOnViewModelScopeLaunch {
//            kuGouRepository.kRankList().onEach {
//
//            }
//        }
//    }
}