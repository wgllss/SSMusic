package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import androidx.lifecycle.viewModelScope
import com.wgllss.ssmusic.core.ex.flowOnIOAndcatch
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val musicRepositoryL: Lazy<MusicRepository>) : BaseViewModel() {

    override fun start() {
    }

    fun searchKeyByTitle(keyword: String) {
        viewModelScope.launch {
            musicRepositoryL.get().searchKeyByTitle(keyword)
                .flowOnIOAndcatch(errorMsgLiveData)
                .onEach {
                    it.forEach {
                        WLog.e(this@HomeViewModel, "${it.author}    ${it.musicName}   ${it.detailUrl}")
                    }
                }.collect()
        }
    }
}