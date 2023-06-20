package com.wgllss.ssmusic.data

import kotlinx.coroutines.flow.Flow

data class FlowEXData(
    val title: String,
    val flow: Flow<MusicBean>
)