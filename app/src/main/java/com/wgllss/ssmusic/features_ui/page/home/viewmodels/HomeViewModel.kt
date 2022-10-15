package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wgllss.ssmusic.core.ex.flowOnIOAndcatch
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val musicRepositoryL: Lazy<MusicRepository>) : BaseViewModel() {

    val searchContent by lazy { MutableLiveData<String>() }
    val result by lazy { MutableLiveData<MutableList<MusicItemBean>>() }

    override fun start() {
    }

    fun searchKeyByTitle() {
        if (searchContent.value == null || searchContent.value.isNullOrEmpty()) {
            WLog.e(this, "searchContent.value ${searchContent.value}")
            return
        }
        viewModelScope.launch {
            musicRepositoryL.get().searchKeyByTitle(searchContent.value!!)
                .onStartAndShow()
                .onCompletionAndHide()
                .onEach {
                    result.postValue(it)
                    it.forEach {
                        WLog.e(this@HomeViewModel, "${it.author}  ${it.musicName}  ${it.detailUrl}")
                    }
                }.flowOnIOAndcatch(errorMsgLiveData)
                .collect()
        }
    }

    fun getDetail(position: Int) {
        result?.value?.takeIf {
            it.size > position
        }?.run {
            viewModelScope.launch {
                musicRepositoryL.get().getPlayUrl(get(position).detailUrl)
                    .onStartAndShow()
                    .onCompletionAndHide()
                    .onEach {
                        WLog.e(this@HomeViewModel, "\n作者：${it.author}\n歌名:${it.title}\n地址:${it.url}\n图片:${it.pic}")
                    }.flowOnIOAndcatch(errorMsgLiveData)
                    .collect()
            }
        }
    }
}