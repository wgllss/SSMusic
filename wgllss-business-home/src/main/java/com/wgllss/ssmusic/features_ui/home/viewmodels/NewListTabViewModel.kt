package com.wgllss.ssmusic.features_ui.home.viewmodels

import androidx.lifecycle.MutableLiveData
import com.wgllss.core.units.AppGlobals
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.repository.KRepository
import kotlinx.coroutines.flow.onEach

class NewListTabViewModel : BaseViewModel() {
    private val kuGouRepository by lazy { KRepository.getInstance(AppGlobals.sApplication) }//: Lazy<MusicRepository>
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
            kuGouRepository.homeKNewList(key)
                .onEach {
                    if (result[key] == null) {
                        WLog.e(this@NewListTabViewModel, key)
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
}