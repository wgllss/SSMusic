package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.MutableContextWrapper
import com.tencent.mmkv.MMKV
import com.wgllss.core.ex.toTheme
import com.wgllss.core.units.LogTimer
import com.wgllss.ssmusic.features_ui.home.fragment.HomeTabFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object InitHomeFirstInitializeHelp {

    fun initCreate(activity: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            LogTimer.LogE(this@InitHomeFirstInitializeHelp, "create ${Thread.currentThread().name}")
//            ScreenManager.initScreenSize(activity)
            MMKV.initialize(activity)
            val themeID = activity.resources.getIdentifier("Theme.SSMusic", "style", activity.packageName)
//            val context: Context = MutableContextWrapper(activity.toTheme(R.style.Theme_SSMusic))
            val context: Context = MutableContextWrapper(activity.toTheme(themeID))
            val res = context.resources

            async(Dispatchers.IO) {
                HomeContains.putViewByKey(LaunchInflateKey.home_activity, GenerateHomeLayout.syncCreateHomeActivityLayout(context, res))
            }
            async(Dispatchers.IO) {
                HomeContains.putViewByKey(LaunchInflateKey.home_navigation, GenerateHomeLayout.syncCreateHomeNavigationLayout(context, res))
            }
            async(Dispatchers.IO) {
                HomeContains.putFragmentByKey(LaunchInflateKey.home_tab_fragment, HomeTabFragment())
                LogTimer.LogE(this@InitHomeFirstInitializeHelp, "LayoutContains fragment")
            }
            async(Dispatchers.IO) {
                HomeContains.putViewByKey(LaunchInflateKey.home_tab_fragment_layout, GenerateHomeLayout.syncCreateHomeTabFragmentLayout(context, res))
            }
            async(Dispatchers.IO) {
                HomeContains.putViewByKey(LaunchInflateKey.home_fragment, GenerateHomeLayout.syncCreateHomeFragmentLayout(context, res))
            }
        }
    }
}