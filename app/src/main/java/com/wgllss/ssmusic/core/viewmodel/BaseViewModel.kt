package com.wgllss.ssmusic.core.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wgllss.ssmusic.core.data.DialogBean
import com.wgllss.ssmusic.core.ex.flowOnIOAndcatch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    val showDialog by lazy { MutableLiveData<DialogBean>() }
    val errorMsgLiveData by lazy { MutableLiveData<String>() }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    abstract fun start()


    fun <T> Flow<T>.flowOnIOAndcatch(): Flow<T> = flowOnIOAndcatch(errorMsgLiveData)

    fun <T> Flow<T>.onStartAndShow(strMessage: String = "正在请求数据"): Flow<T> = onStart {
        show()
    }

    fun show(strMessage: String = "正在请求数据") {
        val showBean = showDialog.value ?: DialogBean(strMessage, true)
        showBean.isShow = true
        showBean.msg = strMessage
        showDialog.postValue(showBean)
    }

    fun <T> Flow<T>.onCompletionAndHide(): Flow<T> = onCompletion {
        hide()
    }

    fun hide() {
        val showBean = showDialog.value ?: DialogBean("", true)
        showBean.isShow = false
        showDialog.postValue(showBean)
    }

    suspend fun <T> Flow<T>.onStartShowAndFlowOnIOAndcatchAndOnCompletionAndHideAndCollect() {
        onStartAndShow().onCompletionAndHide().flowOnIOAndcatch().collect()//这里，开始结束全放在异步里面处理
    }

    fun <T> flowAsyncWorkOnLaunch(flowAsyncWork: suspend () -> Flow<T>) {
        viewModelScope.launch {
            flowAsyncWork.invoke().onStartShowAndFlowOnIOAndcatchAndOnCompletionAndHideAndCollect()
        }
    }
}