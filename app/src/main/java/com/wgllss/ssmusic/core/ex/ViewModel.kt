package com.wgllss.ssmusic.core.ex

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wgllss.ssmusic.core.data.DialogBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

val ViewModel.errorMsgLiveData by lazy { MutableLiveData<String>() }
val ViewModel.showUIDialog by lazy { MutableLiveData<DialogBean>() }

fun <T> ViewModel.flowAsyncWorkOnLaunch(flowAsyncWork: suspend () -> Flow<T>) {
    viewModelScope.launch {
        flowAsyncWork.invoke().flowOnIOAndCatchAAndCollect()
    }
}

fun ViewModel.show(strMessage: String = "正在请求数据") {
    val showBean = showUIDialog.value ?: DialogBean(strMessage, true)
    showBean.isShow = true
    showBean.msg = strMessage
    showUIDialog.postValue(showBean)
}

fun ViewModel.hide() {
    val showBean = showUIDialog.value ?: DialogBean("", true)
    showBean.isShow = false
    showUIDialog.postValue(showBean)
}

