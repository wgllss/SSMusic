package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.MutableContextWrapper
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.startup.Initializer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE
import com.google.android.material.tabs.TabLayoutMediator
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateItem
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateManager
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.getIntToDip
import com.wgllss.ssmusic.core.ex.toTheme
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
import com.wgllss.ssmusic.core.widget.DividerGridItemDecoration
import com.wgllss.ssmusic.features_ui.page.home.adapter.TabAdapter
import com.wgllss.ssmusic.features_ui.page.home.fragment.HomeFragment
import kotlinx.coroutines.*


class AsynInflaterInitializer : Initializer<Unit> {

    override fun create(activity: Context) {
        LogTimer.LogE(this, "create")
        CoroutineScope(Dispatchers.IO).launch {
            LogTimer.LogE(this@AsynInflaterInitializer, "create 1 ${Thread.currentThread().name}")
            val context: Context = MutableContextWrapper(activity.toTheme(R.style.Theme_SSMusic))
            val res = context.resources
            val activityLayoutViewAwait = async(Dispatchers.IO) {
                FrameLayout(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    layoutParams = lp
                }
            }
            val viewTitleBgAwait = async(Dispatchers.IO) {
                View(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_height).toInt())
                    lp.gravity = Gravity.TOP or Gravity.LEFT
                    layoutParams = lp
                    setBackgroundColor(res.getColor(R.color.colorAccent))
                }
            }

            val textTitleViewAwait = async(Dispatchers.IO) {
                TextView(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_text_height).toInt())
                    lp.gravity = Gravity.TOP and Gravity.LEFT
                    lp.topMargin = res.getDimension(R.dimen.status_bar_height).toInt()
                    layoutParams = lp
                    setBackgroundColor(res.getColor(R.color.colorAccent))
                    setTextColor(Color.WHITE)
                    text = "播放列表"
                    gravity = Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
                }
            }
            val recyclerViewAwait = async(Dispatchers.IO) {
                RecyclerView(context).apply {
                    LogTimer.LogE(this@AsynInflaterInitializer, "async 2 ${Thread.currentThread().name}")
                    id = res.getIdentifier("rv_tab_list", "id", activity.packageName)
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    lp.gravity = Gravity.TOP and Gravity.LEFT
                    lp.topMargin = res.getDimension(R.dimen.title_bar_height).toInt()
                    layoutParams = lp
                    setBackgroundColor(Color.WHITE)
                    layoutManager = GridLayoutManager(context, 5)
                    val paddingSize = res.getDimension(R.dimen.recycler_padding).toInt()
                    setPadding(paddingSize, 0, paddingSize, 0)
                    val itemDecoration = View(context)
                    val size = context.getIntToDip(5.0f).toInt()
                    itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                    itemDecoration.setBackgroundColor(Color.parseColor("#60000000"))
                    addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
//                    val rvPlList = contentLayout.findViewById<RecyclerView>(R.id.rv_tab_list)
//                    adapter = TabAdapter(
//                        mutableListOf(
//                            "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深",
////                                "张学友", "陈奕迅", "刘德华", "张杰", "谭咏麟",
////                                "Yanni", "梁静茹", "半吨兄弟", "汪苏泷", "Beyond",
//                            "王菲", "林俊杰", "许嵩", "胡彦斌", "周深",
//                            "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深",
//                            "周杰伦", "林俊杰", "许嵩", "胡彦斌",
//                            "张学友"
//                        )
//                    )

                }
            }

//            val tabLayoutAwait = async(Dispatchers.IO) {
//                TabLayout(context).apply {
//                    id = res.getIdentifier("homeTabLayout", "id", context.packageName)
//                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_text_height).toInt())
//                    lp.gravity = Gravity.TOP or Gravity.LEFT
//                    lp.topMargin = res.getDimension(R.dimen.status_bar_height).toInt()
//                    layoutParams = lp
//                    setBackgroundColor(Color.TRANSPARENT)
//                    tabMode = TabLayout.MODE_SCROLLABLE
//                    tabGravity = TabLayout.GRAVITY_CENTER
//                    setTabTextColors(Color.WHITE, res.getColor(R.color.colorPrimaryDark))
//                    setSelectedTabIndicatorHeight(12)
//                }
//            }
//            val viewPager2LayoutAwait = async(Dispatchers.IO) {
//                ViewPager2(context).apply {
//                    id = res.getIdentifier("homeViewPager2", "id", context.packageName)
//                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
//                    lp.gravity = Gravity.TOP or Gravity.LEFT
//                    lp.topMargin = res.getDimension(R.dimen.title_bar_height).toInt()
//                    layoutParams = lp
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
            val bottomNavigationViewAwait = async(Dispatchers.IO) {
                val bottomNavigationView = BottomNavigationView(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.navigation_height).toInt())
                    lp.gravity = Gravity.BOTTOM or Gravity.LEFT
                    layoutParams = lp
                    id = res.getIdentifier("buttom_navigation", "id", activity.packageName)
                }
//                bottomNavigationView.menu.apply {
//                    clear()
//                    add(0, res.getIdentifier("fmt_a", "id", activity.packageName), 0, res.getString(R.string.title_home)).setIcon(R.drawable.ic_home_black_24dp)
//                    add(0, res.getIdentifier("fmt_b", "id", activity.packageName), 0, res.getString(R.string.title_search)).setIcon(R.drawable.ic_dashboard_black_24dp)
//                    add(0, res.getIdentifier("fmt_c", "id", activity.packageName), 0, res.getString(R.string.title_setting)).setIcon(R.drawable.ic_notifications_black_24dp)
//                }
                bottomNavigationView
            }
            ScreenManager.initScreenSize(activity)
            val activityLayout = activityLayoutViewAwait.await().apply {
                addView(viewTitleBgAwait.await())
                addView(textTitleViewAwait.await())
                addView(recyclerViewAwait.await())
//                addView(tabLayoutAwait.await())
                addView(fragmentContainerViewAwait.await())
//                addView(viewPager2LayoutAwait.await())
                addView(bottomNavigationViewAwait.await())
            }
            ScreenManager.measureAndLayout(activityLayout)
            LayoutContains.putViewByKey(LaunchInflateKey.home_activity, activityLayout)
            LogTimer.LogE(this@AsynInflaterInitializer, "LayoutContains")
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}