package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.livedatabus.PlayerEvent
import com.wgllss.ssmusic.features_system.music.extensions.*
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.EMPTY_PLAYBACK_STATE
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.NOTHING_PLAYING
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayModel @Inject constructor(private val musicServiceConnectionL: Lazy<MusicServiceConnection>) : BaseViewModel() {
    val toatal by lazy { MutableLiveData<Int>() }
    val position by lazy { MutableLiveData<Int>() }
    var isPlaying: Boolean = false
    val pic by lazy { MutableLiveData<String>() }
    val nowPlaying = MutableLiveData<MediaMetadataCompat>()
    val playbackState = MutableLiveData<PlaybackStateCompat>()

    private var updatePosition = true
    val mediaPosition = MutableLiveData(0L)

    override fun start() {
    }

    fun seek(seekingfinished: Boolean, showTime: Boolean) {
        position?.value?.run {
            LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.SeekEvent(this, seekingfinished, showTime))
        }
    }

    val onPlay = View.OnClickListener {//true 暂停 false 继续播放
        isPlaying = !it.isSelected
        musicServiceConnectionL.get().transportControls.run {
            if (it.isSelected) pause() else play()
        }
//        if (it.isSelected) {
//            musicServiceConnectionL.get().transportControls.pause()
//        }
//
//        LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.PlayEvent(it.isSelected))
    }
    val onPlayNext = View.OnClickListener {
        LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.PlayNext)
    }

    val onPlayPrevious = View.OnClickListener {
        LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.PlayPrevious)
    }

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState.postValue(it)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        nowPlaying.postValue(it)
        it?.albumArtUri?.let { url ->
            if (pic.value == null || pic!!.value != url) {
                logE("mediaMetadataObserver url $url")
                pic.postValue(url)
            }
        }
    }

    private val musicServiceConnection = musicServiceConnectionL.get().also {
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
        checkPlaybackPosition()
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
            }.flowOn(Dispatchers.IO)
                .catch { it.printStackTrace() }
                .collect()
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.playbackState.removeObserver(playbackStateObserver)
        musicServiceConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        updatePosition = false
    }
}