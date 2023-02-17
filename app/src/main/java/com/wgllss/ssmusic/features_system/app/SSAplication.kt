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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.app.AndroidApplication
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.getIntToDip
import com.wgllss.ssmusic.core.ex.toTheme
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.widget.DividerGridItemDecoration
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import com.wgllss.ssmusic.features_ui.page.home.adapter.HomeMusicAdapter
import com.wgllss.ssmusic.features_ui.page.home.adapter.TabAdapter
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
//                    setTextColor(Color.WHITE)
//                    text = "播放列表"
//                    gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
//                    setPadding(0, 0, 0, context.getIntToDip(12f).toInt())
//                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
                }
            }

            val tabLayoutAwait = async(Dispatchers.IO) {
                TabLayout(context).apply {
                    id = res.getIdentifier("homeTabLayout", "id", context.packageName)
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_text_height).toInt())
                    lp.gravity = Gravity.TOP or Gravity.LEFT
                    lp.topMargin = res.getDimension(R.dimen.status_bar_height).toInt()
                    layoutParams = lp
                    setBackgroundColor(Color.TRANSPARENT)
                    tabMode = TabLayout.MODE_SCROLLABLE
                    tabGravity = TabLayout.GRAVITY_CENTER
                    setTabTextColors(Color.WHITE, res.getColor(R.color.colorPrimaryDark))
                    setSelectedTabIndicatorHeight(12)
                }
            }
            val viewPager2LayoutAwait = async(Dispatchers.IO) {
                ViewPager2(context).apply {
                    id = res.getIdentifier("homeViewPager2", "id", context.packageName)
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    lp.gravity = Gravity.TOP or Gravity.LEFT
                    lp.topMargin = res.getDimension(R.dimen.title_bar_height).toInt()
                    layoutParams = lp
                    setBackgroundColor(Color.WHITE)
                }
            }



//            val recyclerViewAwait = async(Dispatchers.IO) {
//                RecyclerView(context).apply {
//                    LogTimer.LogE(this@SSAplication, "async 2 ${Thread.currentThread().name}")
//                    id = res.getIdentifier("rv_tab_list", "id", activity.packageName)
//                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
//                    lp.gravity = Gravity.TOP and Gravity.LEFT
//                    lp.topMargin = res.getDimension(R.dimen.title_bar_height).toInt()
//                    layoutParams = lp
//                    setBackgroundColor(Color.WHITE)
//                    layoutManager = GridLayoutManager(context, 5)
//                    val paddingSize = res.getDimension(R.dimen.recycler_padding).toInt()
//                    setPadding(paddingSize, paddingSize / 2, paddingSize, 0)
//                    val itemDecoration = View(context)
//                    val size = context.getIntToDip(5.0f).toInt()
//                    itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
//                    itemDecoration.setBackgroundColor(Color.parseColor("#00000000"))
//                    itemDecoration.setBackgroundColor(Color.TRANSPARENT)
//                    addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
//                    val tabAdapter = TabAdapter()
//                    adapter = tabAdapter
//                    tabAdapter.notifyData(
//                        mutableListOf(
//                            "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深",
////                                 "张学友", "陈奕迅", "刘德华", "张杰", "谭咏麟",
////                                "Yanni", "梁静茹", "半吨兄弟", "汪苏泷", "Beyond",
//                            "王菲", "林俊杰", "许嵩", "胡彦斌", "周深",
//                            "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深",
//                            "周杰伦", "林俊杰", "许嵩", "胡彦斌",
//                            "张学友"
//                        )
//                    )
//                }
//            }
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
            async(Dispatchers.IO) {
                val swipeRefreshLayout = SwipeRefreshLayout(context).apply {
                    layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
//                    initColors()
                }
                val homeFragmentView = RecyclerView(context).apply {
                    id = R.id.home_recycle_view
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    lp.gravity = Gravity.TOP or Gravity.LEFT
                    layoutParams = lp
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    val itemDecoration = View(context)
                    val size = context.getIntToDip(1.0f).toInt()
                    itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                    itemDecoration.setBackgroundColor(Color.parseColor("#60000000"))
                    addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
                    val json = MMKVHelp.getHomeTab1Data()
                    json?.let {
                        val homeMusicAdapter = HomeMusicAdapter()
                        adapter = homeMusicAdapter
                        WLog.e(this, " json json")
                        homeMusicAdapter.notifyData(Gson().fromJson(json, object : TypeToken<MutableList<MusicItemBean>>() {}.type))
                    }
                }
                swipeRefreshLayout.addView(homeFragmentView)
                ScreenManager.measureAndLayout(swipeRefreshLayout)
                LayoutContains.putViewByKey(LaunchInflateKey.home_fragment, swipeRefreshLayout)
            }
            val activityLayout = activityLayoutViewAwait.await().apply {
                addView(textTitleViewAwait.await())
//                addView(recyclerViewAwait.await())
                addView(tabLayoutAwait.await())
                addView(viewPager2LayoutAwait.await())
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


}