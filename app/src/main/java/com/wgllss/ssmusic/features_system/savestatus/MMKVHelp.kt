package com.wgllss.ssmusic.features_system.savestatus

import com.google.gson.Gson
import com.tencent.mmkv.MMKV

object MMKVHelp {
    private val mmkv by lazy { MMKV.defaultMMKV() }
    private val gson by lazy { Gson() }


    /**
     * 得到token
     */
    fun getToken() = mmkv.decodeString("Authorization") ?: ""
}