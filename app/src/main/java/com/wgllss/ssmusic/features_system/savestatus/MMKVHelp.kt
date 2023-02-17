package com.wgllss.ssmusic.features_system.savestatus

import com.tencent.mmkv.MMKV

object MMKVHelp {
    private val mmkv by lazy { MMKV.defaultMMKV() }

    fun setUmInit() = mmkv.encode("um_init", 1)

    fun getUmInit() = mmkv.decodeInt("um_init", 0)

    /**
     * 设置桌面歌词开关
     */
    fun setLockerSwitch(isOpen: Boolean) = mmkv.encode("open_locker_ui", isOpen)

    /**
     * 桌面歌词开关是否打开
     */
    fun isOpenLockerUI() = mmkv.decodeBool("open_locker_ui")

    /**
     * 设置播放模式 单曲循环 随机 顺序播放
     */
    fun setPlayMode(playMode: Int) = mmkv.encode("PlayMode", playMode)

    /**
     * 得到播放模式
     */
    fun getPlayMode() = mmkv.decodeInt("PlayMode")

    fun saveHomeTab1Data(data: String) = mmkv.encode("home_fragment_tab1", data)

    fun getHomeTab1Data()= mmkv.decodeString("home_fragment_tab1")
}