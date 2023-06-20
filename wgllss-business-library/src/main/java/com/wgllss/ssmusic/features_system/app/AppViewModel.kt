package com.wgllss.ssmusic.features_system.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wgllss.core.ex.flowAsyncWorkOnLaunch
import com.wgllss.core.units.WLog
import com.wgllss.ssmusic.data.MusicBean
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.data.RandomPosition
import com.wgllss.ssmusic.datasource.repository.AppRepository
import com.wgllss.ssmusic.features_system.globle.Constants.MODE_PLAY_REPEAT_QUEUE
import com.wgllss.ssmusic.features_system.globle.Constants.MODE_PLAY_SHUFFLE_ALL
import com.wgllss.ssmusic.features_system.room.table.MusicTableBean
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.random.Random

class AppViewModel private constructor(application: Application) : AndroidViewModel(application) {
    private val appRepository by lazy { AppRepository.getInstance(application) }
//    private val kRepository by lazy { KRepository.getInstance(application) }

    companion object {

        @Volatile
        private var instance: AppViewModel? = null

        fun getInstance(context: Application) = instance ?: synchronized(this) {
            instance ?: AppViewModel(context).also { instance = it }
        }
    }

    //播放列表
    lateinit var liveData: LiveData<MutableList<MusicTableBean>>
    val isInitSuccess by lazy { MutableLiveData<Boolean>() }
    private var currentPosition: Int = 0
    private var currentMediaID = 0L

    //正在网络请求的队列里面 需要等待 避免重复请求
    private val mapRuningRequest by lazy { ConcurrentHashMap<Long, Boolean>() }

    //当前播放 数据源 准备完成
    val metadataPrepareCompletion by lazy { MutableLiveData<MusicBean>() }

    //缓存随机位置
    private val randomPosition by lazy { RandomPosition() }

    /**
     * 查询播放列表
     */
    fun queryPlayList() {
        flowAsyncWorkOnLaunch {
            appRepository.getMusicList().onEach {
                liveData = it
                isInitSuccess.postValue(true)
            }
        }
    }

    /**
     *点击播放列表播放
     */
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

    /**
     * 查找位置是否在列表中可用
     */
    private fun findBeanByPosition(position: Int): MusicTableBean? {
        liveData?.value?.takeIf {
            it.size > position && position >= 0
        }?.run {
            return get(position)
        }
        return null
    }

    /**
     * 播放下一曲
     */
    fun playNext() {
        playPosition(getNextPosition(currentPosition))
    }

    /**
     * 播放上一曲
     */
    fun playPrevious() {
        playPosition(getPrevious(currentPosition))
    }

    /**
     * 播放列表中指定位置
     * @param position:列表中位置
     */
    private fun playPosition(position: Int) {
        WLog.e(this@AppViewModel, "当前触发：position:${position}")
        currentPosition = position
        findBeanByPosition(position)?.run {
            currentMediaID = id
            if (mapRuningRequest.containsKey(id) && mapRuningRequest[id] == true) {
                WLog.e(this@AppViewModel, "该资源正在请求中.. $title")
                return@run
            } else
                mapRuningRequest[id] = true
            viewModelScope.launch {
                getPlaySwitch(this@run, position)
            }
        }
        getCacheURL(position)
    }

    private suspend fun getPlaySwitch(it: MusicTableBean, position: Int) {
        if (it.dataSourceType == 0)
            getPlayUrl(it, position)
        else
            getMusicInfo(it, position)
    }

    private suspend fun getMusicInfo(it: MusicTableBean, position: Int) {
        it.run {
            appRepository.containsKey(id.toString())?.let {
                doFlow(this, position, flow {
                    WLog.e(this@AppViewModel, "拿到缓存: $title")
                    val musicBean = MusicBean(title, author, it, pic, 1, 0, mvhash).apply {
                        requestRealUrl = url
                    }
                    emit(musicBean)
                })
                return
            }
            doFlow(it, position, appRepository.getMusicInfo(id.toString(), url, title, author, pic, mvhash), 1)
        }
    }

    private suspend fun getPlayUrl(it: MusicTableBean, position: Int) {
        it.run {
            doFlow(it, position, appRepository.getPlayUrl(id.toString(), url, title, author, pic))
        }
    }

    private var webviewIsRequest = false
    private val quare = ConcurrentLinkedDeque<Flow<MusicBean>>()

    private suspend fun doFLowEx(it: MusicTableBean, position: Int, flow: Flow<MusicBean>) {
        it.run {
            val flowEx = flow.onEach {
                if (currentMediaID == it.id) {
                    metadataPrepareCompletion.postValue(it)
                    WLog.e(this@AppViewModel, "当前该播放 position:$position   ${it.title}")
                } else
                    WLog.e(this@AppViewModel, "缓存了:${title}")
                mapRuningRequest.remove(it.id)
            }.catch {
                mapRuningRequest.remove(id)
                webviewIsRequest = false
                it.printStackTrace()
            }.flowOn(Dispatchers.IO)
                .onCompletion {
                    webviewIsRequest = false
                    quare.takeIf {
                        it.size > 0
                    }?.let {
                        webviewIsRequest = true
                        it.poll().collect()
                        WLog.e(this@AppViewModel, "取出一个 队列长度: ${quare.size}")
                    }
                }
            if (!webviewIsRequest) {
                webviewIsRequest = true
                flowEx.collect()
            } else {
                quare.add(flowEx)
                WLog.e(this@AppViewModel, "加入队列:${title} 队列长度:${quare.size}")
            }
        }
    }

    private suspend fun doFlow(it: MusicTableBean, position: Int, flow: Flow<MusicBean>, type: Int = 0) {
        it.run {
            if (type == 1) {
                doFLowEx(it, position, flow)
            } else {
                flow.onEach {
                    if (currentMediaID == it.id) {
                        metadataPrepareCompletion.postValue(it)
                        WLog.e(this@AppViewModel, "当前该播放 position:$position   ${it.title}")
                    } else
                        WLog.e(this@AppViewModel, "缓存了:${title}")
                    mapRuningRequest.remove(it.id)
                }.catch {
                    mapRuningRequest.remove(id)
                    it.printStackTrace()
                }.flowOn(Dispatchers.IO)
                    .collect()
            }
        }
    }

    /**
     * 得到下一曲播放的位置
     * @param position :当前位置
     */
    private fun getNextPosition(position: Int): Int {
        return when (MMKVHelp.getPlayMode()) {
            MODE_PLAY_REPEAT_QUEUE -> if (position + 1 < liveData.value!!.size) position + 1 else 0
            MODE_PLAY_SHUFFLE_ALL -> {
                var randomPositon = 0
                liveData.value?.let {
                    synchronized(randomPosition) {
                        randomPosition.run {
                            randomPositon = if (randomNextPosition1 != -1 && currentPosition != randomNextPosition1) {
                                randomNextPosition1
                            } else if (randomPosition.randomNextPosition2 != -1 && currentPosition != randomNextPosition2) {
                                randomNextPosition2
                            } else if (randomPosition.randomNextPosition3 != -1 && currentPosition != randomNextPosition3) {
                                randomNextPosition3
                            } else {
                                Random.nextInt(it.size).also { r ->
                                    if (r >= 0 && r < it.size) r else 0
                                }
                            }
                        }
                    }
                }
                WLog.e(this@AppViewModel, "随机位置 randomPositon: $randomPositon")
                randomPositon
            }
            else -> position
        }
    }

    /**
     * 得到上一曲播放的位置
     * @param position :当前位置
     */
    private fun getPrevious(position: Int): Int {
        return when (MMKVHelp.getPlayMode()) {
            MODE_PLAY_REPEAT_QUEUE -> if (position - 1 >= 0) position - 1 else liveData.value!!.size - 1
            MODE_PLAY_SHUFFLE_ALL -> {
                var randomPositon = 0
                liveData.value?.let {
                    synchronized(randomPosition) {
                        randomPosition.run {
                            randomPositon = if (randomPreviousPosition1 != -1 && randomPreviousPosition1 != currentPosition) {
                                randomPreviousPosition1
                            } else if (randomPreviousPosition2 != -1 && randomPreviousPosition2 != currentPosition) {
                                randomPreviousPosition2
                            } else if (randomPreviousPosition3 != -1 && randomPreviousPosition3 != currentPosition) {
                                randomPreviousPosition3
                            } else {
                                Random.nextInt(it.size).also { r ->
                                    if (r >= 0 && r < it.size) r else 0
                                }
                            }
                        }
                    }
                }
                WLog.e(this@AppViewModel, "随机位置 randomPositon: $randomPositon")
                randomPositon
            }
            else -> position
        }
    }

    /**
     * 缓存 自动播放 或者 快速点击上下一曲 避免 点击时才请求 播放链接
     * @param position：当前位置
     */
    fun getCacheURL(position: Int = currentPosition) {
        val next1 = getNextPosition(position)
        val next2 = getNextPosition(position + 1)
        val next3 = getNextPosition(position + 2)

        val previous1 = getPrevious(position)
        val previous2 = getPrevious(position - 1)
        val previous3 = getPrevious(position - 2)

        getCache(next1)
        getCache(previous1)

        getCache(next2)
        getCache(previous2)

        getCache(next3)
        getCache(previous3)

        if (MMKVHelp.getPlayMode() == MODE_PLAY_SHUFFLE_ALL) {
            synchronized(randomPosition) {
                randomPosition.apply {
                    randomPreviousPosition1 = previous1
                    randomPreviousPosition2 = previous2
                    randomPreviousPosition3 = previous3

                    randomNextPosition1 = next1
                    randomNextPosition2 = next2
                    randomNextPosition3 = next3
                }
            }
        }
    }

    /**
     * 获取缓存位置 播放url
     * @param position:缓存位置
     */
    private fun getCache(position: Int) {
        findBeanByPosition(position)?.run {
            if (mapRuningRequest.containsKey(id) && mapRuningRequest[id] == true) {
                WLog.e(this@AppViewModel, "该资源正在请求中.. $title")
                return@run
            } else
                mapRuningRequest[id] = true
            viewModelScope.launch {
                getPlaySwitch(this@run, position)
            }
        }
    }
}