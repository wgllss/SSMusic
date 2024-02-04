package com.wgllss.ssmusic.features_system.activation

import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp

object ActivationUtils {

    //3天试用期
    const val deTime = 24 * 60 * 60 * 1000

    fun getActiveType(): Int {
        val unActivityTime = MMKVHelp.getUnActiveTime()
        if (unActivityTime == -1L) {
            //激活过
            return 0
        }
        val currentTime = System.currentTimeMillis()
        val delayTime = currentTime - unActivityTime
        return if (unActivityTime == 0L) {
            MMKVHelp.saveUnActiveTime(currentTime)
            -3
        } else if (delayTime > 3 * deTime) {
            //没有激活过,体验已经到期
            -2
        } else if (delayTime > deTime && delayTime < 3 * deTime) {
            //没有激活过,在体验期内
            -1
        } else {
            //激活过
            0
        }
    }

    /**
     * 不可用状态
     */
    fun isUnUsed() = getActiveType() == -2


}