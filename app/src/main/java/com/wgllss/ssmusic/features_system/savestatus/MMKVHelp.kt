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
}