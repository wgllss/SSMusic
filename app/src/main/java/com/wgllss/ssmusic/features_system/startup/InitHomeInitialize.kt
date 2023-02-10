package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.MutableContextWrapper
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.startup.Initializer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.tencent.mmkv.MMKV
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.toTheme
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
import com.wgllss.ssmusic.features_third.um.UMHelp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InitHomeInitialize : Initializer<Unit> {

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    override fun create(context: Context) {
        LogTimer.LogE(this, "create")
        GlobalScope.launch {
            MMKV.initialize(context)
//            UMHelp.umInit(context)
//            val homeNavigation = AsyncInflateItem(LaunchInflateKey.home_navigation, R.layout.home_buttom_navigation, null, null)
//            AsyncInflateManager.instance.synIlateWithThreadPool(context, homeNavigation)
            LogTimer.LogE(this, "UMHelp.umInit")
            val context: Context = MutableContextWrapper(context.toTheme(R.style.Theme_SSMusic))
            val res = context.resources


//            val tabFragmentLayoutAwait = async(Dispatchers.IO) {
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
            tabFragmentLayout.addView(tabLayout)
            val viewPager2Layout = ViewPager2(context).apply {
                id = res.getIdentifier("homeViewPager2", "id", context.packageName)
                val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                lp.gravity = Gravity.TOP or Gravity.LEFT
                lp.topMargin = res.getDimension(R.dimen.title_bar_height).toInt()
                layoutParams = lp
            }
            tabFragmentLayout.addView(viewPager2Layout)
            ScreenManager.measureAndLayout(tabFragmentLayout)
            tabFragmentLayout
//            }
            LayoutContains.putViewByKey(LaunchInflateKey.home_tab_fragment, tabFragmentLayout)
//            LayoutContains.putViewByKey(LaunchInflateKey.home_tab_fragment, tabFragmentLayoutAwait.await())

            LogTimer.LogE(this, "LayoutContains UMHelp.umInit")
        }
    }
}