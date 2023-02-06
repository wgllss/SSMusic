package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import androidx.lifecycle.MutableLiveData
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeTabViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var musicRepositoryL: Lazy<MusicRepository>

    val result by lazy { MutableLiveData<MutableList<MusicItemBean>>() }

    override fun start() {
    }

    fun getData(html: String) {
        flowAsyncWorkOnViewModelScopeLaunch {
            musicRepositoryL.get().homeMusic(html)
                .onEach {
                    result.postValue(it)
                    it.forEach {
                        WLog.e(this@HomeTabViewModel, "${it.author}  ${it.musicName}  ${it.detailUrl}")
                    }
                }

//            musicRepositoryL.get().searchKeyByTitle(searchContent.value!!)
//                .onEach {
//                    result.postValue(it)
//                    it.forEach {
//                        WLog.e(this@HomeViewModel, "${it.author}  ${it.musicName}  ${it.detailUrl}")
//                    }
//                }
        }
    }

}