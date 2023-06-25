package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.datasource.netbean.mv.KMVItem
import com.wgllss.ssmusic.datasource.netbean.singer.KSingerItem
import com.wgllss.ssmusic.datasource.repository.KRepository
import kotlinx.coroutines.flow.onEach

class KSingerViewModel : BaseViewModel() {

    val kuGouRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }

    //    val liveDataLoadSuccessCount by lazy { MutableLiveData(0) }
    val result by lazy { mutableMapOf<String, MutableLiveData<MutableList<KSingerItem>>>() }

    override fun start() {
    }

    fun initKey(key: String) {
        result[key] = MutableLiveData<MutableList<KSingerItem>>()
    }

    fun kSingers(pathKey: String) {
        flowAsyncWorkOnViewModelScopeLaunch {
            kuGouRepository.kSingers(pathKey).onEach {
                result[pathKey]?.postValue(it)
            }
        }
    }
}