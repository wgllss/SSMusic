package com.wgllss.ssmusic.features_system.activation

import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp

object ActivationUtils {

    fun getActiveType(): Int {
        val unActivityTime = MMKVHelp.getUnActiveTime()
        val currentTime = System.currentTimeMillis()
        return if (unActivityTime == 0L) {
            MMKVHelp.saveUnActiveTime(currentTime)
            //没有激活过,在体验期内
            -1
        } else if (currentTime - unActivityTime > 24 * 60 * 60 * 1000) {
            //没有激活过,体验已经到期
            -2
        } else if (currentTime - unActivityTime < 24 * 60 * 60 * 1000) {
            //没有激活过,在体验期内
            -1
        } else {
            //激活过
            0
        }
    }

    fun isUnActive() = getActiveType() == -1

}