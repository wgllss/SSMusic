package com.wgllss.ssmusic.core.widget.navigation

import android.app.Application
import android.content.Context
import android.content.MutableContextWrapper
import android.view.*
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wgllss.ssmusic.NavigationConfig
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.ScreenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

object AsynBottomNavigationViewLoader {

    var bottomNavigationView: BottomNavigationView? = null
    var menu: Menu? = null
    var lp: FrameLayout.LayoutParams? = null

    fun loadBottomNavigationView(context: Context, parent: ViewGroup?, layoutID: Int) {
        val coroutineScope = when (context) {
            is Application, is MutableContextWrapper -> GlobalScope
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
            view?.takeIf {
                it is BottomNavigationView
            }?.let {
                bottomNavigationView = it as BottomNavigationView
                menu = bottomNavigationView?.menu
                lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, context.resources.getDimension(R.dimen.navigation_height).toInt()).apply {
                    gravity = Gravity.BOTTOM
                }

//                NavigationConfig.getDestConfig()?.forEach {
//                    it?.value?.run {
//                        menu?.add(0, id, 0, label)?.setIcon(iconId)
//                    }
//                }
            }
        }
    }
}