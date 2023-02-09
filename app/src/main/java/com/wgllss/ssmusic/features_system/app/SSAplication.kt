package com.wgllss.ssmusic.features_system.app

import android.content.Context
import android.content.MutableContextWrapper
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tencent.mmkv.MMKV
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.app.AndroidApplication
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.getIntToDip
import com.wgllss.ssmusic.core.ex.toTheme
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
import com.wgllss.ssmusic.core.widget.DividerGridItemDecoration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@HiltAndroidApp
class SSAplication : AndroidApplication() {

    override fun attachBaseContext(activity: Context) {
        LogTimer.initTime(this)
        CoroutineScope(Dispatchers.IO).launch {
            LogTimer.LogE(this@SSAplication, "create 1 ${Thread.currentThread().name}")
            val context: Context = MutableContextWrapper(activity.toTheme(R.style.Theme_SSMusic))
            val res = context.resources
            async {
                MMKV.initialize(this@SSAplication)
            }
            val activityLayoutViewAwait = async(Dispatchers.IO) {
                val layout = FrameLayout(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    layoutParams = lp
                }
                layout
            }
            val textTitleViewAwait = async(Dispatchers.IO) {
                TextView(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_height).toInt())
                    lp.gravity = Gravity.TOP and Gravity.LEFT
                    layoutParams = lp
                    setBackgroundColor(res.getColor(R.color.colorAccent))
                    setTextColor(Color.WHITE)
                    text = "播放列表"
                    gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                    setPadding(0, 0, 0, context.getIntToDip(12f).toInt())
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
                }
            }
            val recyclerViewAwait = async(Dispatchers.IO) {
                RecyclerView(context).apply {
                    LogTimer.LogE(this@SSAplication, "async 2 ${Thread.currentThread().name}")
                    id = res.getIdentifier("rv_tab_list", "id", activity.packageName)
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    lp.gravity = Gravity.TOP and Gravity.LEFT
                    lp.topMargin = res.getDimension(R.dimen.title_bar_height).toInt()
                    layoutParams = lp
                    setBackgroundColor(Color.WHITE)
                    layoutManager = GridLayoutManager(context, 5)
                    val paddingSize = res.getDimension(R.dimen.recycler_padding).toInt()
                    setPadding(paddingSize, paddingSize / 2, paddingSize, 0)
                    val itemDecoration = View(context)
                    val size = context.getIntToDip(5.0f).toInt()
                    itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                    itemDecoration.setBackgroundColor(Color.parseColor("#00000000"))
                    itemDecoration.setBackgroundColor(Color.TRANSPARENT)
                    addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
                }
            }
            val fragmentContainerViewAwait = async(Dispatchers.IO) {
                FragmentContainerView(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    lp.bottomMargin = res.getDimension(R.dimen.navigation_height).toInt()
                    layoutParams = lp
                    id = res.getIdentifier("nav_host_fragment_activity_main", "id", activity.packageName)
                    visibility = View.GONE
                }
            }
            ScreenManager.initScreenSize(activity)
//            val bottomNavigationViewAwait =
            async(Dispatchers.IO) {
                val bottomNavigationView = BottomNavigationView(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.navigation_height).toInt())
                    lp.gravity = Gravity.BOTTOM or Gravity.LEFT
                    layoutParams = lp
                    id = res.getIdentifier("buttom_navigation", "id", activity.packageName)
                }
                bottomNavigationView.menu.apply {
                    clear()
                    add(0, res.getIdentifier("fmt_a", "id", activity.packageName), 0, res.getString(R.string.title_home))//.setIcon(R.drawable.ic_home_black_24dp)
                    add(0, res.getIdentifier("fmt_b", "id", activity.packageName), 0, res.getString(R.string.title_search))//.setIcon(R.drawable.ic_dashboard_black_24dp)
                    add(0, res.getIdentifier("fmt_c", "id", activity.packageName), 0, res.getString(R.string.title_setting))//.setIcon(R.drawable.ic_notifications_black_24dp)
                }
                ScreenManager.measureAndLayout(bottomNavigationView)
                LayoutContains.putViewByKey(LaunchInflateKey.home_navigation, bottomNavigationView)
//                bottomNavigationView
            }
            val activityLayout = activityLayoutViewAwait.await().apply {
                addView(textTitleViewAwait.await())
                addView(recyclerViewAwait.await())
                addView(fragmentContainerViewAwait.await())
            }
            ScreenManager.measureAndLayout(activityLayout)
            LayoutContains.putViewByKey(LaunchInflateKey.home_activity, activityLayout)
            LogTimer.LogE(this@SSAplication, "LayoutContains 0")
//            LayoutContains.putViewByKey(LaunchInflateKey.home_navigation, bottomNavigationViewAwait.await())
//            LogTimer.LogE(this@SSAplication, "LayoutContains")
        }
        super.attachBaseContext(activity)
    }

//    override fun onCreate() {
//        super.onCreate()
//        CoroutineScope(Dispatchers.IO).launch {
//            MMKV.initialize(this@SSAplication)
//            UMHelp.umInit(this@SSAplication)
//            LogTimer.LogE(this, "UMHelp.umInit")
//        }
//    }
}