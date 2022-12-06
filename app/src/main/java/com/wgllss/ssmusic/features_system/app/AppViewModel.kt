package com.wgllss.ssmusic.features_system.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wgllss.ssmusic.core.ex.flowAsyncWorkOnLaunch
import com.wgllss.ssmusic.core.ex.flowOnIOAndCatch
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.data.MusicBean
import com.wgllss.ssmusic.datasource.repository.AppRepository
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(application: Application, private val appRepository: AppRepository) : AndroidViewModel(application) {
    //播放列表
    lateinit var liveData: LiveData<MutableList<MusicTabeBean>>
    val isInitSuccess by lazy { MutableLiveData<Boolean>() }
    var currentPosition: Int = 0
    private val map by lazy { ConcurrentHashMap<String, MusicBean>() }

    //当前播放 数据源 准备完成
    val metadataPrepareCompletion by lazy { MutableLiveData<MusicBean>() }

    //查询播放列表
    fun queryPlayList() {
        flowAsyncWorkOnLaunch {
            appRepository.getMusicList().onEach {
                liveData = it
                isInitSuccess.postValue(true)
            }
        }
    }

    private fun findBeanByPosition(position: Int): MusicTabeBean? {
        liveData?.value?.takeIf {
            it.size > position
        }?.run {
            val mmsicTabeBean = get(position)
            return get(position)
        }
        return null
    }

    fun playNext() {
        currentPosition.takeIf {
            it + 1 < liveData.value!!.size
        }?.let {
            playPosition(it + 1)
        }
    }

    fun playPrevious() {
        currentPosition?.takeIf {
            it - 1 > 0
        }?.let {
            playPosition(it - 1)
        }
    }

    private fun playPosition(position: Int) {
        logE("点击：position:${position}")
        currentPosition = position

        findBeanByPosition(position)?.run {
            val currentMediaID = id.toString()
            if (map.containsKey(currentMediaID)) {
                metadataPrepareCompletion.postValue(map.remove(currentMediaID))
                logE("getDetail 取到缓存的下一曲 ")
                getNextCache(position, currentMediaID)
            } else {
                viewModelScope.launch {
                    appRepository.getPlayUrl(url)
                        .onEach {
                            metadataPrepareCompletion.postValue(it)
                        }.flowOnIOAndCatch()
                        .collect {
                            getNextCache(position, currentMediaID)
                        }
                }
            }
        }
    }

    //自动播放的下一曲 //todo 分顺序播放 随机 单曲循环
    private fun getNextPosition(position: Int): Int {
        return position + 1
    }

    private fun getNextCache(position: Int, currentMediaID: String) {
        findBeanByPosition(getNextPosition(position))?.run {
            flowAsyncWorkOnLaunch {
                appRepository.getPlayUrl(url)
                    .onEach {
                        map[id.toString()] = it
                        map.takeIf { m ->
                            m.containsKey(currentMediaID)
                        }?.let {
                            map.remove(currentMediaID)
                        }
                        logE("缓存到下一曲")
                    }
            }
        }
    }

    fun getPlayUrlFromMediaID(mediaId: String) {
        logE("getPlayUrl mediaId $mediaId")
        liveData.value?.forEachIndexed { position, it ->
            it.takeIf {
                mediaId.toLong() == it.id
            }?.let {
                if (map.containsKey(mediaId)) {
                    metadataPrepareCompletion.postValue(map[mediaId])
                    logE("getPlayUrlFromMediaID 取到缓存的下一曲 ")
                    getNextCache(position, mediaId)
                } else {
                    playPosition(position)
                }
                logE("getPlayUrl mediaId $mediaId picurl:${it.pic}")
                return@forEachIndexed
            }
        }
    }
}