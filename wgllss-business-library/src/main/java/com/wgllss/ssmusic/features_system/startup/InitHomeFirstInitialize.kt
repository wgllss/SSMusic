package com.wgllss.ssmusic.features_system.startup
//
//import android.content.Context
//import androidx.startup.Initializer
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//abstract class InitHomeFirstInitialize : Initializer<Unit> {
//    override fun create(activity: Context) {
//        CoroutineScope(Dispatchers.IO).launch {
//            initCreate(activity)
////            LogTimer.LogE(this@InitHomeFirstInitialize, "create ${Thread.currentThread().name}")
//////            ScreenManager.initScreenSize(activity)
////            MMKV.initialize(activity)
////            val themeID = activity.resources.getIdentifier("Theme.SSMusic", "style", activity.packageName)
//////            val context: Context = MutableContextWrapper(activity.toTheme(R.style.Theme_SSMusic))
////            val context: Context = MutableContextWrapper(activity.toTheme(themeID))
////            val res = context.resources
////
////            async(Dispatchers.IO) {
////                HomeContains.putViewByKey(LaunchInflateKey.home_activity, GenerateHomeLayout.syncCreateHomeActivityLayout(context, res))
////            }
////            async(Dispatchers.IO) {
////                HomeContains.putViewByKey(LaunchInflateKey.home_navigation, GenerateHomeLayout.syncCreateHomeNavigationLayout(context, res))
////            }
////            async(Dispatchers.IO) {
////                HomeContains.putFragmentByKey(LaunchInflateKey.home_tab_fragment, HomeTabFragment())
////                LogTimer.LogE(this@AsynInflaterInitializer, "LayoutContains fragment")
////            }
////            async(Dispatchers.IO) {
////                HomeContains.putViewByKey(LaunchInflateKey.home_tab_fragment_layout, GenerateHomeLayout.syncCreateHomeTabFragmentLayout(context, res))
////            }
////            async(Dispatchers.IO) {
////                HomeContains.putViewByKey(LaunchInflateKey.home_fragment, GenerateHomeLayout.syncCreateHomeFragmentLayout(context, res))
////            }
//        }
////        LogTimer.LogE(this@AsynInflaterInitializer, "LayoutContains")
//    }
//
//    open fun initCreate(activity: Context) {}
//
//    override fun dependencies(): List<Class<out Initializer<*>>> {
//        return emptyList()
//    }
//
//}