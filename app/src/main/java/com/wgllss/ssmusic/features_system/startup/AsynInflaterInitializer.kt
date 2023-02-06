package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.MutableContextWrapper
import androidx.startup.Initializer
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateItem
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateManager
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.units.LogTimer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AsynInflaterInitializer : Initializer<Unit> {

    override fun create(activity: Context) {
        LogTimer.LogE(this, "create")
        GlobalScope.launch {
            AsyncInflateManager.initScreenSize(activity)
            val context: Context = MutableContextWrapper(activity)
            val homeNavigation = AsyncInflateItem(LaunchInflateKey.home_navigation, R.layout.home_buttom_navigation, null, null)
            val homeFragment = AsyncInflateItem(LaunchInflateKey.home_fragment, R.layout.fragment_home, null, null)
            val homeTabFragment = AsyncInflateItem(LaunchInflateKey.home_tab_fragment, R.layout.fragment_home_tab, null, null)
            AsyncInflateManager.instance.asyncInflate(context, homeNavigation, homeTabFragment, homeFragment)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}