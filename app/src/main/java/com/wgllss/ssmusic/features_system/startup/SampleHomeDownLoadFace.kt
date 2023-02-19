package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import com.wgllss.core.units.LogTimer
import com.wgllss.plugin.library.HomeDownLoadFace
import com.wgllss.plugin.library.HomePluginManagerUser

class SampleHomeDownLoadFace : HomeDownLoadFace() {

    override fun succeed(context: Context, fileAbsolutePath: String) {
        HomePluginManagerUser.getInstance(context, fileAbsolutePath).run {
            initManagerImpl()
            getPluginManager().preIntHome(context)
        }
        LogTimer.LogE(this, "SampleHomeDownLoadFace")
//        super.succeed(context, fileAbsolutePath)
    }
}