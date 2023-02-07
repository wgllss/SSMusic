package com.wgllss.ssmusic.core.units

import android.content.Context
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.WindowManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ScreenManager {
    lateinit var displays: Array<Display>  //屏幕数组
    var isMinScreen = false//小屏
    var screenWidth = 0
    var screenHeight = 0
    var widthSpec = 0
    var heightSpec = 0

//    fun init(context: Context) {
//        GlobalScope.launch {
//            val mDisplayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
//            mDisplayManager?.displays?.takeIf {
//                it.size > 1
//            }?.let {
//                val outSize0 = Rect()
//                displays = it
//                LogTimer.LogE(this@ScreenManager, "create")
//                it[0].getRectSize(outSize0)
//                val outSize1 = Rect()
//                if (outSize0.right - outSize1.right > 100) {
//                    //是小屏
//                    isMinScreen = true
//                }
//            }
//        }
//    }
//
//    fun getPresentationDisplays(): Display {
//        displays.forEach { it ->
//            it.takeIf {
//                (it.flags and Display.FLAG_SECURE != 0 && it.flags and Display.FLAG_SUPPORTS_PROTECTED_BUFFERS != 0 && it.flags and Display.FLAG_PRESENTATION != 0)
//            }?.let {
//                return it
//            }
//        }
//        return displays[0]
//    }

    //初始化 屏幕 宽高相关属性
    fun initScreenSize(context: Context) {
        val metric = DisplayMetrics()
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.defaultDisplay.getRealMetrics(metric)
        screenWidth = metric.widthPixels
        screenHeight = metric.heightPixels
        widthSpec = View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY)
        heightSpec = View.MeasureSpec.makeMeasureSpec(screenHeight, View.MeasureSpec.EXACTLY)
        LogTimer.LogE(this, "initScreenSize")
    }
}