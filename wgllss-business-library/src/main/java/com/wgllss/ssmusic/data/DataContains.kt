package com.wgllss.ssmusic.data

import androidx.lifecycle.MutableLiveData

object DataContains {
    val list by lazy { MutableLiveData<MutableList<HomeItemBean>>() }
}