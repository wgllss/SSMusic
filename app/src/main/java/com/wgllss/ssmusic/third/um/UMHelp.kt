package com.wgllss.ssmusic.third.um

import android.content.Context
import com.umeng.commonsdk.UMConfigure
import com.wgllss.ssmusic.BuildConfig

object UMHelp {
    const val APP_KEY = "634d040505844627b5672f2f"
    const val APP_MASTER_SECRET = "6y2akwgydlplnewzdhqczfmld5kan6xo"
    const val UMENG_MESSAGE_SECRET = "163045f64d1a5f94f8e50cbf8bb81f65"

    fun umInit(context: Context) {
        UMConfigure.preInit(context, APP_KEY, "umeng");
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        //添加注释
        //添加注释
        UMConfigure.init(context, APP_KEY, "umeng", UMConfigure.DEVICE_TYPE_PHONE, "")
    }
}