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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class AsynInflaterInitializer : Initializer<Unit> {

    override fun create(activity: Context) {
        LogTimer.LogE(this, "create")
        GlobalScope.launch {
            LogTimer.LogE(this@AsynInflaterInitializer, "create ${Thread.currentThread().name}")
            ScreenManager.initScreenSize(activity)
            val context: Context = MutableContextWrapper(activity.toTheme(R.style.Theme_SSMusic))
            val res = context.resources
            val activityLayoutViewAwait = async(Dispatchers.IO) {
                LogTimer.LogE(this@AsynInflaterInitializer, "async 1 ${Thread.currentThread().name}")
                val activityLayoutView = FragmentContainerView(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    lp.bottomMargin = res.getDimension(R.dimen.navigation_height).toInt()
                    layoutParams = lp
                    id = res.getIdentifier("nav_host_fragment_activity_main", "id", activity.packageName)
                }
                measureAndLayout(activityLayoutView)
                activityLayoutView
            }

            val tabFragmentLayoutAwait = async(Dispatchers.IO) {
                val tabFragmentLayout = FrameLayout(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    layoutParams = lp
                }
                val viewTitleBg = View(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_height).toInt())
                    lp.gravity = Gravity.TOP and Gravity.LEFT
                    layoutParams = lp
                    setBackgroundColor(res.getColor(R.color.colorAccent))
                }
                tabFragmentLayout.addView(viewTitleBg)
                val tabLayout = TabLayout(context, null).apply {
                    id = res.getIdentifier("homeTabLayout", "id", activity.packageName)
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_text_height).toInt())
                    lp.gravity = Gravity.TOP and Gravity.LEFT
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
                    lp.gravity = Gravity.TOP and Gravity.LEFT
                    lp.topMargin = res.getDimension(R.dimen.title_bar_height).toInt()
                    layoutParams = lp
                }
                tabFragmentLayout.addView(viewPager2Layout)
                tabFragmentLayout
            }

//            val homeNavigation = AsyncInflateItem(LaunchInflateKey.home_navigation, R.layout.home_buttom_navigation, null, null)
//            AsyncInflateManager.instance.asyncInflate(context, homeNavigation)
            LayoutContains.putViewByKey(LaunchInflateKey.home_activity, activityLayoutViewAwait.await())
            LayoutContains.putViewByKey(LaunchInflateKey.home_tab_fragment, tabFragmentLayoutAwait.await())
//            LayoutContains.putViewByKey(LaunchInflateKey.home_fragment, homeFragmentLayoutAwait.await())
            LogTimer.LogE(this@AsynInflaterInitializer, "LayoutContains")
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    private fun measureAndLayout(view: View) {
        view?.measure(ScreenManager.widthSpec, ScreenManager.heightSpec)
        view?.layout(0, 0, ScreenManager.screenWidth, ScreenManager.screenHeight)
    }
}