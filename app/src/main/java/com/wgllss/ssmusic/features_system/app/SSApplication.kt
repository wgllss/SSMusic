package com.wgllss.ssmusic.features_system.app

import android.content.Context
import com.wgllss.core.app.AndroidApplication
import com.wgllss.core.units.LogTimer
import dagger.hilt.android.HiltAndroidApp

//@HiltAndroidApp
class SSApplication : AndroidApplication() {

    override fun attachBaseContext(base: Context?) {
        LogTimer.initTime(this)
        super.attachBaseContext(base)
    }
}