package com.wgllss.ssmusic.datasource.netbean.pindao

import com.wgllss.ssmusic.data.MusicItemBean

data class PinDaoAllData(
    val map: MutableMap<String, MutableList<MusicItemBean>>,
    val sides: MutableList<PinDaoSideBean>
)
