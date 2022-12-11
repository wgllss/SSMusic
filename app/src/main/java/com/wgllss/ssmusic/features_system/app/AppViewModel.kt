package com.wgllss.ssmusic.features_system.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wgllss.ssmusic.core.ex.flowAsyncWorkOnLaunch
import com.wgllss.ssmusic.core.ex.flowOnIOAndCatch
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.data.MusicBean
import com.wgllss.ssmusic.datasource.repository.AppRepository
import com.wgllss.ssmusic.features_system.globle.Constants.MODE_PLAY_REPEAT_QUEUE
import com.wgllss.ssmusic.features_system.globle.Constants.MODE_PLAY_REPEAT_SONG
import com.wgllss.ssmusic.features_system.globle.Constants.MODE_PLAY_SHUFFLE_ALL
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AppViewModel @Inject constructor(application: Application, private val appRepository: AppRepository) : AndroidViewModel(application) {
    //播放列表
    lateinit var liveData: LiveData<MutableList<MusicTabeBean>>
    val isInitSuccess by lazy { MutableLiveData<Boolean>() }
    var currentPosition: Int = 0
    var currentMediaID = 0L

    //正在网络请求的队列里面 需要等待 避免重复请求
    val mapRuningRequest by lazy { ConcurrentHashMap<Long, Boolean>() }

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
            it.size > position && position >= 0
        }?.run {
            return get(position)
        }
        return null
    }

    fun playNext() {
        when (MMKVHelp.getPlayMode()) {
            MODE_PLAY_REPEAT_QUEUE -> {
                currentPosition.let {
                    playPosition(if (it + 1 < liveData.value!!.size) it + 1 else 0)
                }
            }
            MODE_PLAY_SHUFFLE_ALL -> {
                liveData.value?.let {
                    val n = Random.nextInt(it.size)
                    playPosition(if (n >= 0 && n < liveData.value!!.size) n else 0)
                }
            }
            MODE_PLAY_REPEAT_SONG -> {
                playPosition(currentPosition)
            }
        }
    }

    fun playPrevious() {
        when (MMKVHelp.getPlayMode()) {
            MODE_PLAY_REPEAT_QUEUE -> {
                currentPosition.let {
                    playPosition(if (it - 1 >= 0) it - 1 else liveData.value!!.size - 1)
                }
            }
            MODE_PLAY_SHUFFLE_ALL -> {
                liveData.value?.let {
                    val n = Random.nextInt(it.size)
                    playPosition(if (n >= 0 && n < liveData.value!!.size) n else 0)
                }
            }
            MODE_PLAY_REPEAT_SONG -> {
                playPosition(currentPosition)
            }
        }
    }

    private fun playPosition(position: Int) {
        WLog.e(this@AppViewModel, "点击：position:${position}")
        currentPosition = position
        findBeanByPosition(position)?.run {
            currentMediaID = id
            if (mapRuningRequest.containsKey(id) && mapRuningRequest[id] == true) {
                WLog.e(this@AppViewModel, "该资源正在请求中.. $title")
                return@run
            } else
                mapRuningRequest[id] = true
            viewModelScope.launch {
                appRepository.getPlayUrl(id.toString(), url, title, author, pic)
                    .onEach {
                        if (currentMediaID == it.id) {
                            metadataPrepareCompletion.postValue(it)
                            WLog.e(this@AppViewModel, "当前该播放 ${it.title}")
                        }
                        mapRuningRequest.remove(it.id)
                    }.flowOnIOAndCatch()
                    .collect()
                //拿取缓存前2个 后2个
            }
        }
        getCacheURL(position)
    }

    //自动播放的下一曲 //todo 分顺序播放 随机 单曲循环
    private fun getNextPosition(position: Int): Int {
        return position + 1
    }

    //自动播放的上一曲 //todo 分顺序播放 随机 单曲循环
    private fun getPrevious(position: Int): Int {
        return position - 1
    }

    private fun getCacheURL(position: Int) {
        getCache(getNextPosition(position))
        getCache(getPrevious(position))

        getCache(getNextPosition(position + 1))
        getCache(getPrevious(position - 1))

        getCache(getNextPosition(position + 2))
        getCache(getPrevious(position - 2))
    }

    private fun getCache(position: Int) {
        findBeanByPosition(position)?.run {
            if (mapRuningRequest.containsKey(id) && mapRuningRequest[id] == true) {
                WLog.e(this@AppViewModel, "该资源正在请求中.. $title")
                return@run
            } else
                mapRuningRequest[id] = true
            flowAsyncWorkOnLaunch {
                appRepository.getPlayUrl(id.toString(), url, title, author, pic)
                    .onEach {
                        if (currentMediaID == it.id) {
                            metadataPrepareCompletion.postValue(it)
                            WLog.e(this@AppViewModel, "当前该播放:${title}")
                        } else
                            WLog.e(this@AppViewModel, "缓存了:${title}")
                        mapRuningRequest.remove(it.id)
                    }
            }
        }
    }

    fun getPlayUrlFromMediaID(mediaId: String) {
        liveData.value?.forEachIndexed { position, it ->
            it.takeIf {
                mediaId.toLong() == it.id
            }?.let {
                playPosition(position)
                return@forEachIndexed
            }
        }
    }
}