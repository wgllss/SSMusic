package com.wgllss.ssmusic.features_system.music.music_web

import com.tencent.mmkv.MMKV

object LrcHelp {

    fun saveLrc(id: String, lrc: String) {
        MMKV.defaultMMKV().encode(id, lrc)
    }

    fun getLrc(id: String): String {
        return MMKV.defaultMMKV().decodeString(id) ?: ""
    }

//    fun saveHomeData(json: String) {
//        MMKV.defaultMMKV().encode("home_key_json", json)
//    }
//
//    fun getHomeData(): String {
//        return MMKV.defaultMMKV().decodeString("home_key_json") ?: ""
//    }

    fun saveJsPath(path: String) {
        MMKV.defaultMMKV().encode("webview_js_Key", path)
    }

    fun getJsPath(): String {
        return MMKV.defaultMMKV().decodeString("webview_js_Key") ?: ""
    }
}