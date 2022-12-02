package com.wgllss.ssmusic.features_system.savestatus

import com.google.gson.Gson
import com.tencent.mmkv.MMKV

object MMKVHelp {
    private val mmkv by lazy { MMKV.defaultMMKV() }
    private val gson by lazy { Gson() }

//    fun setPlayID(ID: Long) = mmkv.encode("current_play_id", ID)
//
//    fun getPlayID() = mmkv.decodeLong("current_play_id")


    /**
     * 得到token
     */
    fun getToken() = mmkv.decodeString("Authorization") ?: ""

    fun setUmInit() = mmkv.encode("um_init", 1)

    fun getUmInit() = mmkv.decodeInt("um_init", 0)
}