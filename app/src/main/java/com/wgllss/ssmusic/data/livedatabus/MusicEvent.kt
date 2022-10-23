package com.wgllss.ssmusic.data.livedatabus

import com.jeremyliao.liveeventbus.core.LiveEvent

sealed class MusicEvent : LiveEvent {
    data class ChangeMusic(val pic: String, val title: String, val author: String) : MusicEvent()

    data class PlayerProgress(val progress: Int) : MusicEvent()

    data class BufferingUpdate(val percent: Int): MusicEvent()

    object PlayerStart : MusicEvent()

    object PlayerPause : MusicEvent()
}
