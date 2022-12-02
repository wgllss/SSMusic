package com.wgllss.ssmusic.features_system.app

import android.app.Application
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.util.MimeTypes
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wgllss.ssmusic.core.ex.flowOnIOAndcatch
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.UUIDHelp
import com.wgllss.ssmusic.data.MusicBean
import com.wgllss.ssmusic.data.livedatabus.MusicBeanEvent
import com.wgllss.ssmusic.datasource.repository.AppRepository
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.http.Url
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(application: Application, private val appRepository: AppRepository) : AndroidViewModel(application) {

    val errorMsgLiveData by lazy { MutableLiveData<String>() }

    //播放列表
    lateinit var liveData: LiveData<MutableList<MusicTabeBean>>
    val isInitSuccess by lazy { MutableLiveData<Boolean>() }
    val currentPosition by lazy { MutableLiveData<Int>() }

    val metadataList by lazy { MutableLiveData<MusicBean>() }

    private fun <T> Flow<T>.flowOnIOAndcatch(): Flow<T> = flowOnIOAndcatch(errorMsgLiveData)

    private suspend fun <T> Flow<T>.flowOnIOAndcatchAAndCollect() {
        flowOnIOAndcatch().collect()//这里，开始结束全放在异步里面处理
    }

    private fun <T> flowAsyncWorkOnLaunch(flowAsyncWork: suspend () -> Flow<T>) {
        viewModelScope.launch {
            flowAsyncWork.invoke().flowOnIOAndcatchAAndCollect()
        }
    }

    //查询播放列表
    fun queryPlayList() {
        flowAsyncWorkOnLaunch {
            appRepository.getMusicList().onEach {
                logE("11111 onEach ${it.value}")
                it.value?.forEach {
                    logE("11111 ${it.url}}")
                }
                liveData = it
                isInitSuccess.postValue(true)
            }
        }
    }

    //添加到播放列表
//    fun addToPlayList(it: MusicBeanEvent) {
//        flowAsyncWorkOnLaunch {
//            appRepository.addToPlayList(it).onEach {
//                liveData?.value?.let {
//                    currentPosition.postValue(it.size)
//                }
//            }
//        }
//    }

    private fun findBeanByPosition(position: Int): MusicTabeBean? {
        liveData?.value?.takeIf {
            it.size > position
        }?.run {
            return get(position)
        }
        return null
    }

    fun playNext() {
        currentPosition?.value?.takeIf {
            it + 1 < liveData.value!!.size
        }?.let {
            playPosition(it + 1)
        }
    }

    fun playPrevious() {
        currentPosition?.value?.takeIf {
            it - 1 > 0
        }?.let {
            playPosition(it - 1)
        }
    }

    private fun playPosition(position: Int) {
        logE("点击：position:${position}")
        currentPosition.postValue(position)
    }

    fun getDetail(position: Int) {
        findBeanByPosition(position)?.run {
            flowAsyncWorkOnLaunch {
                appRepository.getPlayUrl(url)
                    .onEach {
                        metadataList.postValue(it)
                        logE("getPlayUrl mediaId onEach ")
                    }
            }
        }
    }

//    fun getPlayUrlFromMediaUri(url: Url) {
//
//    }

    fun getPlayUrlFromMediaID(mediaId: String) {
        logE("getPlayUrl mediaId $mediaId")
        liveData.value?.forEachIndexed { position, it ->
            it.takeIf {
                mediaId.toLong() == it.id
            }?.let {
                logE("getPlayUrl mediaId $mediaId picurl:${it.pic}")
                currentPosition.postValue(position)
                return@forEachIndexed
            }
        }
    }
}