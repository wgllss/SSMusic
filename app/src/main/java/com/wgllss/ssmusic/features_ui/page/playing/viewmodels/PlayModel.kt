package com.wgllss.ssmusic.features_ui.page.playing.viewmodels

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.features_system.globle.Constants.MODE_PLAY_REPEAT_QUEUE
import com.wgllss.ssmusic.features_system.globle.Constants.MODE_PLAY_REPEAT_SONG
import com.wgllss.ssmusic.features_system.globle.Constants.MODE_PLAY_SHUFFLE_ALL
import com.wgllss.ssmusic.features_system.music.extensions.currentPlayBackPosition
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayModel @Inject constructor(private val musicServiceConnectionL: Lazy<MusicServiceConnection>) : BaseViewModel() {
    var isPlaying: Boolean = false
    val nowPlaying by lazy { MutableLiveData<MediaMetadataCompat>() }
    val playbackState by lazy { MutableLiveData<PlaybackStateCompat>() }
    val currentPlayMode by lazy { MutableLiveData(MODE_PLAY_REPEAT_QUEUE) }

    private var updatePosition = true
    val mediaPosition by lazy { MutableLiveData(0L) }

    override fun start() {
        currentPlayMode.postValue(MMKVHelp.getPlayMode())
        musicServiceConnectionL.get().run {
            playbackState.observeForever(playbackStateObserver)
            nowPlaying.observeForever(mediaMetadataObserver)
        }
        checkPlaybackPosition()
    }

    //从指定位置开始播放
    fun seek(pos: Long) {
        musicServiceConnectionL.get().transportControls.seekTo(pos)
    }

    val onPlay = View.OnClickListener {//true 暂停 false 继续播放
        isPlaying = !it.isSelected
        musicServiceConnectionL.get().transportControls.run {
            if (it.isSelected) pause() else play()
        }
    }

    val onPlayNext = View.OnClickListener {
        musicServiceConnectionL.get().transportControls.skipToNext()
    }

    val onPlayPrevious = View.OnClickListener {
        musicServiceConnectionL.get().transportControls.skipToPrevious()
    }

    val switchMode = View.OnClickListener {
        var mode = MODE_PLAY_REPEAT_QUEUE
        var modeToast = ""
        when (currentPlayMode.value) {
            MODE_PLAY_REPEAT_QUEUE -> {
                mode = MODE_PLAY_SHUFFLE_ALL
                modeToast = "随机模式"
            }
            MODE_PLAY_SHUFFLE_ALL -> {
                mode = MODE_PLAY_REPEAT_SONG
                modeToast = "单曲循环"
            }
            MODE_PLAY_REPEAT_SONG -> {
                mode = MODE_PLAY_REPEAT_QUEUE
                modeToast = "列表循环"
            }
            else -> MODE_PLAY_REPEAT_QUEUE
        }
        currentPlayMode.postValue(mode)
        MMKVHelp.setPlayMode(mode)
        errorMsgLiveData.value = modeToast
    }

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState.postValue(it)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        nowPlaying.postValue(it)
    }

    private fun checkPlaybackPosition() {
        viewModelScope.launch {
            flow {
                while (updatePosition) {
                    val currPosition = playbackState?.value?.currentPlayBackPosition ?: 0
                    if (mediaPosition.value != currPosition)
                        mediaPosition.postValue(currPosition)
                }
                delay(300)
                emit(0)
            }.flowOnIOAndCatch()
                .collect()
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnectionL.get().playbackState.removeObserver(playbackStateObserver)
        musicServiceConnectionL.get().nowPlaying.removeObserver(mediaMetadataObserver)
        updatePosition = false
    }
}