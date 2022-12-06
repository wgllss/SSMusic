package com.wgllss.ssmusic.features_ui.page.playing.viewmodels

import android.graphics.Bitmap
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.features_system.music.extensions.currentPlayBackPosition
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
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
    val nowPlaying = MutableLiveData<MediaMetadataCompat>()
    val playbackState = MutableLiveData<PlaybackStateCompat>()

    private var updatePosition = true
    val mediaPosition = MutableLiveData(0L)

    override fun start() {
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

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState.postValue(it)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        nowPlaying.postValue(it)
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
            }.flowOnIOAndCatch()
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