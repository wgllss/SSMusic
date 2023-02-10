package com.wgllss.ssmusic.features_system.startup

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
import androidx.startup.Initializer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE
import com.tencent.mmkv.MMKV
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.getIntToDip
import com.wgllss.ssmusic.core.ex.toTheme
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
import com.wgllss.ssmusic.core.widget.DividerGridItemDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class AsynInflaterInitializer : Initializer<Unit> {

    override fun create(activity: Context) {
        LogTimer.LogE(this, "create")
        CoroutineScope(Dispatchers.IO).launch {
            LogTimer.LogE(this@AsynInflaterInitializer, "create ${Thread.currentThread().name}")
            ScreenManager.initScreenSize(activity)
            async {
                MMKV.initialize(activity)
            }
            val context: Context = MutableContextWrapper(activity.toTheme(R.style.Theme_SSMusic))
            val res = context.resources
            async(Dispatchers.IO) {
                val activityLayout = FragmentContainerView(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    lp.bottomMargin = res.getDimension(R.dimen.navigation_height).toInt()
                    layoutParams = lp
                    id = res.getIdentifier("nav_host_fragment_activity_main", "id", activity.packageName)
                }
                ScreenManager.measureAndLayout(activityLayout)
                LayoutContains.putViewByKey(LaunchInflateKey.home_activity, activityLayout)
            }
            async(Dispatchers.IO) {
                val bottomNavigationView = BottomNavigationView(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.navigation_height).toInt())
                    lp.gravity = Gravity.BOTTOM or Gravity.LEFT
                    layoutParams = lp
                    id = res.getIdentifier("buttom_navigation", "id", activity.packageName)
                    menu.apply {
                        clear()
                        add(0, res.getIdentifier("fmt_a", "id", activity.packageName), 0, res.getString(R.string.title_home))//.setIcon(R.drawable.ic_home_black_24dp)
                        add(0, res.getIdentifier("fmt_b", "id", activity.packageName), 0, res.getString(R.string.title_search))//.setIcon(R.drawable.ic_dashboard_black_24dp)
                        add(0, res.getIdentifier("fmt_c", "id", activity.packageName), 0, res.getString(R.string.title_setting))//.setIcon(R.drawable.ic_notifications_black_24dp)
                    }
                }
                ScreenManager.measureAndLayout(bottomNavigationView)
                LayoutContains.putViewByKey(LaunchInflateKey.home_navigation, bottomNavigationView)
            }
            async(Dispatchers.IO) {
                val tabFragmentLayout = FrameLayout(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    layoutParams = lp
                }
                val viewTitleBg = View(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_height).toInt())
                    lp.gravity = Gravity.TOP or Gravity.LEFT
                    layoutParams = lp
                    setBackgroundColor(res.getColor(R.color.colorAccent))
                }
                tabFragmentLayout.addView(viewTitleBg)
                val tabLayout = TabLayout(context).apply {
                    id = res.getIdentifier("homeTabLayout", "id", activity.packageName)
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_text_height).toInt())
                    lp.gravity = Gravity.TOP or Gravity.LEFT
                    lp.topMargin = res.getDimension(R.dimen.status_bar_height).toInt()
                    layoutParams = lp
                    setBackgroundColor(Color.TRANSPARENT)
                    tabMode = MODE_SCROLLABLE
                    tabGravity = TabLayout.GRAVITY_CENTER
                    setTabTextColors(Color.WHITE, res.getColor(R.color.colorPrimaryDark))
                    setSelectedTabIndicatorHeight(12)
                }
                tabFragmentLayout.addView(tabLayout)
                val viewPager2Layout = ViewPager2(context).apply {
                    id = res.getIdentifier("homeViewPager2", "id", activity.packageName)
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    lp.gravity = Gravity.TOP or Gravity.LEFT
                    lp.topMargin = res.getDimension(R.dimen.title_bar_height).toInt()
                    layoutParams = lp
                    setBackgroundColor(Color.WHITE)
                }
                tabFragmentLayout.addView(viewPager2Layout)
                ScreenManager.measureAndLayout(tabFragmentLayout)
                LayoutContains.putViewByKey(LaunchInflateKey.home_tab_fragment, tabFragmentLayout)
            }
//            async(Dispatchers.IO) {
//                val homeFragmentView = RecyclerView(context).apply {
//                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
//                    lp.gravity = Gravity.TOP or Gravity.LEFT
//                    layoutParams = lp
//                    setBackgroundColor(Color.WHITE)
//                    layoutManager = LinearLayoutManager(context)
//                    val paddingSize = res.getDimension(R.dimen.recycler_padding).toInt()
//                    setPadding(paddingSize, 0, paddingSize, 0)
//                    val itemDecoration = View(context)
//                    val size = context.getIntToDip(1.0f).toInt()
//                    itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
//                    itemDecoration.setBackgroundColor(Color.parseColor("#60000000"))
//                    addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
//                }
//                ScreenManager.measureAndLayout(homeFragmentView)
//                LayoutContains.putViewByKey(LaunchInflateKey.home_fragment, homeFragmentView)
//            }
            LogTimer.LogE(this@AsynInflaterInitializer, "LayoutContains")
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

//    private fun measureAndLayout(view: View) {
//        view?.measure(ScreenManager.widthSpec, ScreenManager.heightSpec)
//        view?.layout(0, 0, ScreenManager.screenWidth, ScreenManager.screenHeight)
//    }
}