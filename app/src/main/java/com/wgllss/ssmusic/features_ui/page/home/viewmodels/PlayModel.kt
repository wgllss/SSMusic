package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.livedatabus.PlayerEvent

class PlayModel : BaseViewModel() {
    val toatal by lazy { MutableLiveData<Int>() }
    val position by lazy { MutableLiveData<Int>() }

    override fun start() {
    }

    fun seek(seekingfinished: Boolean, showTime: Boolean) {
        position?.value?.run {
            LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.SeekEvent(this, seekingfinished, showTime))
        }
    }

    val onPlay = View.OnClickListener {//true 暂停 false 继续播放
        LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.PlayEvent(it.isSelected))
    }
    val onPlayNext = View.OnClickListener {
        LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.PlayNext)
    }

    val onPlayPrevious = View.OnClickListener {
        LiveEventBus.get(PlayerEvent::class.java).post(PlayerEvent.PlayPrevious)
    }
}