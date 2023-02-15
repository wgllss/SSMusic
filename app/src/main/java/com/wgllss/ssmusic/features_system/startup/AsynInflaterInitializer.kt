package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.MutableContextWrapper
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.toTheme
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.features_system.startup.lazyhome.AsyncHomeLayout
import com.wgllss.ssmusic.features_ui.page.home.fragment.HomeTabFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class AsynInflaterInitializer : Initializer<Unit> {

    override fun create(activity: Context) {
        LogTimer.LogE(this, "create")
        CoroutineScope(Dispatchers.IO).launch {
            LogTimer.LogE(this@AsynInflaterInitializer, "create ${Thread.currentThread().name}")
//            ScreenManager.initScreenSize(activity)
            MMKV.initialize(activity)
            val context: Context = MutableContextWrapper(activity.toTheme(R.style.Theme_SSMusic))
            val res = context.resources
            async(Dispatchers.IO) {
                LayoutContains.putViewByKey(LaunchInflateKey.home_activity, AsyncHomeLayout.syncCreateHomeActivityLayout(context, res))
            }
            async(Dispatchers.IO) {
                LayoutContains.putViewByKey(LaunchInflateKey.home_navigation, AsyncHomeLayout.syncCreateHomeNavigationLayout(context, res))
            }
            async(Dispatchers.IO) {
                LayoutContains.putFragmentByKey(LaunchInflateKey.home_tab_fragment, HomeTabFragment())
                LogTimer.LogE(this@AsynInflaterInitializer, "LayoutContains fragment")
            }
            async(Dispatchers.IO) {
                LayoutContains.putViewByKey(LaunchInflateKey.home_tab_fragment_layout, AsyncHomeLayout.syncCreateHomeTabFragmentLayout(context, res))
            }
            async(Dispatchers.IO) {
                LayoutContains.putViewByKey(LaunchInflateKey.home_fragment, AsyncHomeLayout.syncCreateHomeFragmentLayout(context, res))
            }
        }
        LogTimer.LogE(this@AsynInflaterInitializer, "LayoutContains")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}