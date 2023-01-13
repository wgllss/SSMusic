package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.MutableContextWrapper
import androidx.startup.Initializer
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateItem
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateManager
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AsynInflaterInitializer : Initializer<Unit> {

    override fun create(activity: Context) {
        LogTimer.LogE(this, "create")
        GlobalScope.launch {
            ScreenManager.initScreenSize(activity)
            AsyncInflateManager.initScreenSize(activity)
            val context: Context = MutableContextWrapper(activity)
            val home = AsyncInflateItem(LaunchInflateKey.home, R.layout.home_buttom_navigation, null, null)
            val homeFragment = AsyncInflateItem(LaunchInflateKey.home_fragment, R.layout.fragment_home, null, null)
            AsyncInflateManager.instance.asyncInflate(context, home,homeFragment)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}