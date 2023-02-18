package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.wgllss.core.units.LogTimer

class InitHomeFirstInitialize : Initializer<Unit> {

    override fun create(activity: Context) {
        LogTimer.LogE(this, "create")
        InitHomeFirstInitializeHelp.initCreate(activity)
//        CoroutineScope(Dispatchers.IO).launch {
//            LogTimer.LogE(this@InitHomeFirstInitialize, "create ${Thread.currentThread().name}")
////            ScreenManager.initScreenSize(activity)
//            MMKV.initialize(activity)
//            val themeID = activity.resources.getIdentifier("Theme.SSMusic", "style", activity.packageName)
////            val context: Context = MutableContextWrapper(activity.toTheme(R.style.Theme_SSMusic))
//            val context: Context = MutableContextWrapper(activity.toTheme(themeID))
//            val res = context.resources
//
//            async(Dispatchers.IO) {
//                HomeContains.putViewByKey(LaunchInflateKey.home_activity, GenerateHomeLayout.syncCreateHomeActivityLayout(context, res))
//            }
//            async(Dispatchers.IO) {
//                HomeContains.putViewByKey(LaunchInflateKey.home_navigation, GenerateHomeLayout.syncCreateHomeNavigationLayout(context, res))
//            }
//            async(Dispatchers.IO) {
//                HomeContains.putFragmentByKey(LaunchInflateKey.home_tab_fragment, HomeTabFragment())
//                LogTimer.LogE(this@InitHomeFirstInitialize, "LayoutContains fragment")
//            }
//            async(Dispatchers.IO) {
//                HomeContains.putViewByKey(LaunchInflateKey.home_tab_fragment_layout, GenerateHomeLayout.syncCreateHomeTabFragmentLayout(context, res))
//            }
//            async(Dispatchers.IO) {
//                HomeContains.putViewByKey(LaunchInflateKey.home_fragment, GenerateHomeLayout.syncCreateHomeFragmentLayout(context, res))
//            }
//        }
        LogTimer.LogE(this@InitHomeFirstInitialize, "LayoutContains")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}