package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.livedatabus.PlayerEvent
import com.wgllss.ssmusic.features_system.music.MusicComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class PlayModel : BaseViewModel() {
    val toatal by lazy { MutableLiveData<Int>() }
    val position by lazy { MutableLiveData<Int>() }
    var isPlaying: Boolean = false
    val pic by lazy { MutableLiveData<String>() }
    val playUIToFront by lazy { MutableLiveData<PlayerEvent.PlayUIToFront>() }

    override fun start() {
    }

    fun seek(seekingfinished: Boolean, showTime: Boolean) {
        position?.value?.run {
            LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.SeekEvent(this, seekingfinished, showTime))
        }
    }

    val onPlay = View.OnClickListener {//true 暂停 false 继续播放
        isPlaying = !it.isSelected
        LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.PlayEvent(it.isSelected))
    }
    val onPlayNext = View.OnClickListener {
        LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.PlayNext)
    }

    val onPlayPrevious = View.OnClickListener {
        LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.PlayPrevious)
    }

    fun onResume() {
        if (playUIToFront.value == null) {
            playUIToFront.value = PlayerEvent.PlayUIToFront(true)
        } else {
            playUIToFront.value!!.isFront = true
        }
        logE("onResume ${playUIToFront.value!!.isFront}")
        LiveEventBus.get(PlayerEvent::class.java).post(playUIToFront.value)
    }

    fun onStop() {
        if (playUIToFront.value == null) {
            playUIToFront.value = PlayerEvent.PlayUIToFront(false)
        } else {
            playUIToFront.value!!.isFront = false
        }
        LiveEventBus.get(PlayerEvent::class.java).post(playUIToFront.value)
    }

    fun getBitMapColor(url: String) {
//        flow {
//            url?.let {
//                val futureBitmap = Glide.with(musicService).asBitmap().load(musicPic).submit();
//                val artwork: Bitmap = futureBitmap.get()
//                emit(artwork)
//            }
//        }.flowOn(Dispatchers.IO)
//            .onEach {
//                if (newNotifyMode == MusicComponent.NOTIFY_MODE_FOREGROUND) {
//                    musicService.startForeground(notificationId, buildNotification(it))
//                } else {
//                    mNotificationManager.notify(notificationId, buildNotification(it))
//                }
//                mNotifyMode = newNotifyMode
//            }
//            .catch {
//                it.printStackTrace()
//            }.collect()
    }
}