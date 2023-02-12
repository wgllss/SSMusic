package com.wgllss.ssmusic.features_system.startup.lazyhome

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.ex.getIntToDip
import com.wgllss.ssmusic.core.ex.initColors
import com.wgllss.ssmusic.core.units.ScreenManager
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.widget.DividerGridItemDecoration
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import com.wgllss.ssmusic.features_ui.page.home.adapter.HomeMusicAdapter


object AsyncHomeLayout {

    fun getCreateViewByKey(context: Context, key: String) = when (key) {
        LaunchInflateKey.home_activity -> syncCreateHomeActivityLayout(context, context.resources)
        LaunchInflateKey.home_navigation -> syncCreateHomeNavigationLayout(context, context.resources)
        LaunchInflateKey.home_tab_fragment -> syncCreateHomeTabFragmentLayout(context, context.resources)
        LaunchInflateKey.home_fragment -> syncCreateHomeFragmentLayout(context, context.resources)
        else -> null
    }

    private fun getColorPrimary(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    fun syncCreateHomeActivityLayout(context: Context, res: Resources): View {
        val activityLayout = FragmentContainerView(context).apply {
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            lp.bottomMargin = res.getDimension(R.dimen.navigation_height).toInt()
            layoutParams = lp
            setBackgroundColor(Color.WHITE)
            id = res.getIdentifier("nav_host_fragment_activity_main", "id", context.packageName)
        }
        ScreenManager.measureAndLayout(activityLayout)
        return activityLayout
    }

    fun syncCreateHomeNavigationLayout(context: Context, res: Resources): View {
        val bottomNavigationView = BottomNavigationView(context).apply {
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.navigation_height).toInt())
            lp.gravity = Gravity.BOTTOM or Gravity.LEFT
            layoutParams = lp
            id = res.getIdentifier("buttom_navigation", "id", context.packageName)
            labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
            menu.apply {
                clear()
                add(0, res.getIdentifier("fmt_a", "id", context.packageName), 0, res.getString(R.string.title_home))//.setIcon(R.drawable.ic_home_black_24dp)
                add(0, res.getIdentifier("fmt_b", "id", context.packageName), 0, res.getString(R.string.title_history))//.setIcon(R.drawable.ic_dashboard_black_24dp)
                add(0, res.getIdentifier("fmt_c", "id", context.packageName), 0, res.getString(R.string.title_search))//.setIcon(R.drawable.ic_dashboard_black_24dp)
                add(0, res.getIdentifier("fmt_d", "id", context.packageName), 0, res.getString(R.string.title_setting))//.setIcon(R.drawable.ic_notifications_black_24dp)
            }
        }
        ScreenManager.measureAndLayout(bottomNavigationView)
        return bottomNavigationView
    }

    fun syncCreateHomeTabFragmentLayout(context: Context, res: Resources): View {
        val tabFragmentLayout = FrameLayout(context).apply {
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            layoutParams = lp
        }
        val viewTitleBg = View(context).apply {
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_height).toInt())
            lp.gravity = Gravity.TOP or Gravity.LEFT
            layoutParams = lp
            setBackgroundColor(getColorPrimary(context))
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
//            setTabTextColors(res.getColor(R.color.colorPrimaryDark), Color.WHITE)
//            setSelectedTabIndicatorColor(res.getColor(R.color.color_select))
            setSelectedTabIndicatorColor(Color.WHITE)
            setSelectedTabIndicatorHeight(8)
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
        return tabFragmentLayout
    }

    fun syncCreateHomeFragmentLayout(context: Context, res: Resources): View {
        val swipeRefreshLayout = SwipeRefreshLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            initColors()
        }
        val homeFragmentView = RecyclerView(context).apply {
            id = res.getIdentifier("home_recycle_view", "id", context.packageName)
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            lp.gravity = Gravity.TOP or Gravity.LEFT
            layoutParams = lp
            setBackgroundColor(Color.WHITE)
            layoutManager = LinearLayoutManager(context)
            val paddingSize = res.getDimension(R.dimen.recycler_padding).toInt()
            setPadding(paddingSize, 0, paddingSize, 0)
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
                WLog.e(this@AsyncHomeLayout, " json json")
                homeMusicAdapter.notifyData(Gson().fromJson(json, object : TypeToken<MutableList<MusicItemBean>>() {}.type))
            }
        }
        swipeRefreshLayout.addView(homeFragmentView)
        ScreenManager.measureAndLayout(swipeRefreshLayout)
        return swipeRefreshLayout
    }
}