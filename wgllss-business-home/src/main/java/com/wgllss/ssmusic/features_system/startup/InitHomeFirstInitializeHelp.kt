package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.MutableContextWrapper
import com.tencent.mmkv.MMKV
import com.wgllss.core.ex.toTheme
import com.wgllss.core.units.LogTimer
import com.wgllss.ssmusic.data.DataContains
import com.wgllss.ssmusic.datasource.repository.KRepository
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import com.wgllss.ssmusic.features_ui.home.fragment.HomeTabFragment
import com.wgllss.ssmusic.features_ui.home.fragment.KHomeTabFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.StringBuilder

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
                HomeContains.putFragmentByKey(LaunchInflateKey.home_tab_fragment, KHomeTabFragment())
                LogTimer.LogE(this@InitHomeFirstInitializeHelp, "LayoutContains fragment")
            }
            async(Dispatchers.IO) {
                HomeContains.putViewByKey(LaunchInflateKey.home_tab_fragment_layout, GenerateHomeLayout.syncCreateHomeTabFragmentLayout(context, res))
            }
            async(Dispatchers.IO) {
                HomeContains.putViewByKey(LaunchInflateKey.home_fragment, GenerateHomeLayout.syncCreateHomeFragmentLayout(context, res))
            }
            async(Dispatchers.IO) {
                HomeContains.putViewByKey(LaunchInflateKey.play_bar_layout, GenerateHomeLayout.syncCreatePlayPanelLayout(context, res))
            }

            async(Dispatchers.IO) {
                KRepository.getInstance(activity).homeKMusic()
                    .onEach {
                        DataContains.list.postValue(it)
                    }.collect()
            }
            async(Dispatchers.IO) {
                val am = activity.assets
                try {
                    val resJs = am.list("js")
                    val strOfflineResources = StringBuilder()
                    if (resJs != null && resJs.isNotEmpty()) {
                        for (i in resJs.indices) {
                            strOfflineResources.append(resJs[i])
                        }
                    }
                    LrcHelp.saveJsPath(strOfflineResources.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}