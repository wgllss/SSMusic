package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.MutableContextWrapper
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
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
import com.wgllss.ssmusic.core.ex.toTheme
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
import com.wgllss.ssmusic.features_ui.page.home.fragment.HomeFragment
import kotlinx.coroutines.*


class AsynInflaterInitializer : Initializer<Unit> {

    override fun create(activity: Context) {
        LogTimer.LogE(this, "create")
        CoroutineScope(Dispatchers.IO).launch {
            LogTimer.LogE(this@AsynInflaterInitializer, "create 1 ${Thread.currentThread().name}")
//            val initScreenAwait = async(Dispatchers.IO) {
            ScreenManager.initScreenSize(activity)
//            }
            val context: Context = MutableContextWrapper(activity.toTheme(R.style.Theme_SSMusic))
//            val context: Context = MutableContextWrapper(activity)
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
            val bottomNavigationViewAwait = async(Dispatchers.IO) {
                val bottomNavigationView = BottomNavigationView(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.navigation_height).toInt())
                    lp.gravity = Gravity.BOTTOM or Gravity.LEFT
                    layoutParams = lp
                    id = res.getIdentifier("buttom_navigation", "id", activity.packageName)
                }
                bottomNavigationView.menu.apply {
                    clear()
                    add(0, res.getIdentifier("fmt_a", "id", activity.packageName), 0, res.getString(R.string.title_home)).setIcon(R.drawable.ic_home_black_24dp)
                    add(0, res.getIdentifier("fmt_b", "id", activity.packageName), 0, res.getString(R.string.title_search)).setIcon(R.drawable.ic_dashboard_black_24dp)
                    add(0, res.getIdentifier("fmt_c", "id", activity.packageName), 0, res.getString(R.string.title_setting)).setIcon(R.drawable.ic_notifications_black_24dp)
                }
                bottomNavigationView
            }
//            initScreenAwait.await()
            val activityLayout = activityLayoutViewAwait.await().apply {
                addView(viewTitleBgAwait.await())
                addView(tabLayoutAwait.await())
                addView(fragmentContainerViewAwait.await())
                addView(viewPager2LayoutAwait.await())
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