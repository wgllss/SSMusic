package com.wgllss.ssmusic.data.livedatabus

import com.jeremyliao.liveeventbus.core.LiveEvent

sealed class PlayerEvent : LiveEvent {

    data class PlayEvent(val pause: Boolean) : PlayerEvent()

    object PlayNext : PlayerEvent()

    object PlayPrevious : PlayerEvent()

    data class SeekEvent(val position: Int, val seekingfinished: Boolean, val showTime: Boolean) : PlayerEvent()

    data class PlayUIToFront(var isFront: Boolean) : PlayerEvent()
}