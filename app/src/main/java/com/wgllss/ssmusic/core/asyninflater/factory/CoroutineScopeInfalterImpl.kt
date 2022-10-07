package com.wgllss.ssmusic.core.asyninflater.factory

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.ScreenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

/**
 * 协程实现异步加载布局 并且，测量 布局 处理
 */
class CoroutineScopeInfalterImpl : AsyncInflaterLayout {

    override fun asyncInflater(context: Context, parent: ViewGroup?, layoutID: Int, inflaterImpl: (view: View) -> Unit) {
        val coroutineScope = when (context) {
            is Application -> GlobalScope
            is ComponentActivity -> (context as LifecycleOwner).lifecycleScope
            else -> {
                throw Throwable("this context is not with a  coroutineScope ")
            }
        }
        coroutineScope?.launch(Dispatchers.IO) {
            val view = LayoutInflater.from(context).inflate(layoutID, parent, false)
            view?.apply {
                val time = measureTimeMillis {
                    if (ScreenManager.screenHeight == 0 || ScreenManager.screenWidth == 0 || ScreenManager.widthSpec == 0 || ScreenManager.heightSpec == 0) {
                        ScreenManager.initScreenSize(context)
                    }
                    measure(ScreenManager.widthSpec, ScreenManager.heightSpec)
                    layout(0, 0, ScreenManager.screenWidth, ScreenManager.screenHeight)
                }
                logE("测量 绘制 时间为:${time}ms")
            }
            withContext(Dispatchers.Main){
                inflaterImpl.invoke(view)
            }
        }
    }
}