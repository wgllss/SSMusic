package com.wgllss.ssmusic.data.livedatabus

import com.jeremyliao.liveeventbus.core.LiveEvent

sealed class MusicPlayEvent(val event: MusicEvent) : LiveEvent