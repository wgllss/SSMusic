package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
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
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.units.LogTimer
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.clearLongClickToast
import com.wgllss.music.skin.R
import com.wgllss.core.material.ThemeUtils
import com.wgllss.core.units.WLog
import com.wgllss.ssmusic.data.HomeItemBean
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.ex.initColors
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import com.wgllss.ssmusic.features_ui.home.adapter.HomeMusicAdapter
import com.wgllss.ssmusic.features_ui.home.adapter.KHomeAdapter

object GenerateHomeLayout {

    fun getCreateViewByKey(context: Context, key: String) = when (key) {
        LaunchInflateKey.home_activity -> syncCreateHomeActivityLayout(context, context.resources)
        LaunchInflateKey.home_navigation -> syncCreateHomeNavigationLayout(context, context.resources)
        LaunchInflateKey.home_tab_fragment_layout -> syncCreateHomeTabFragmentLayout(context, context.resources)
        LaunchInflateKey.home_fragment -> syncCreateHomeFragmentLayout(context, context.resources)
        else -> null
    }

    fun syncCreateHomeActivityLayout(context: Context, res: Resources): View {
        val activityLayout = FragmentContainerView(context).apply {
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
//            lp.bottomMargin = res.getDimension(R.dimen.navigation_height).toInt()
            setPadding(0, 0, 0, res.getDimension(R.dimen.navigation_height).toInt())
            layoutParams = lp
            setBackgroundColor(ThemeUtils.getAndroidColorBackground(context))
            id = R.id.nav_host_fragment_activity_main
        }
//        ScreenManager.measureAndLayout(activityLayout)
        return activityLayout
    }

    fun syncCreateHomeNavigationLayout(context: Context, res: Resources): View {
        val bottomNavigationView = BottomNavigationView(context).apply {
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.navigation_height).toInt())
            lp.gravity = Gravity.BOTTOM or Gravity.LEFT
            layoutParams = lp
            id = R.id.buttom_navigation
            labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
            setBackgroundColor(Color.TRANSPARENT)
            menu.apply {
                clear()
                add(0, R.id.fmt_a, 0, res.getString(R.string.title_home))//.setIcon(R.drawable.ic_home_black_24dp)
//                add(0, R.id.fmt_b, 0, res.getString(R.string.title_history))//.setIcon(R.drawable.ic_dashboard_black_24dp)
                add(0, R.id.fmt_b, 0, res.getString(R.string.title_MV))//.setIcon(R.drawable.ic_dashboard_black_24dp)
                add(0, R.id.fmt_c, 0, res.getString(R.string.title_search))//.setIcon(R.drawable.ic_dashboard_black_24dp)
                add(0, R.id.fmt_d, 0, res.getString(R.string.title_setting))//.setIcon(R.drawable.ic_notifications_black_24dp)
            }
            clearLongClickToast(R.id.fmt_a, R.id.fmt_b, R.id.fmt_c, R.id.fmt_d)
        }
//        ScreenManager.measureAndLayout(bottomNavigationView)
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
            setBackgroundColor(ThemeUtils.getColorPrimary(context))
        }
        tabFragmentLayout.addView(viewTitleBg)
        val tabLayout = TabLayout(context).apply {
            id = R.id.homeTabLayout
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_text_height).toInt())
            lp.gravity = Gravity.TOP or Gravity.LEFT
            lp.topMargin = res.getDimension(R.dimen.status_bar_height).toInt()
            layoutParams = lp
            setBackgroundColor(Color.TRANSPARENT)
            tabMode = TabLayout.MODE_SCROLLABLE
            tabGravity = TabLayout.GRAVITY_CENTER
            setSelectedTabIndicatorColor(ThemeUtils.getColorOnPrimary(context))
            setSelectedTabIndicatorHeight(8)
        }
        tabFragmentLayout.addView(tabLayout)
        val viewPager2Layout = ViewPager2(context).apply {
            id = R.id.homeViewPager2
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            lp.gravity = Gravity.TOP or Gravity.LEFT
            lp.topMargin = res.getDimension(R.dimen.title_bar_height).toInt()
            layoutParams = lp
        }
        tabFragmentLayout.addView(viewPager2Layout)
//        ScreenManager.measureAndLayout(tabFragmentLayout)
        LogTimer.LogE(this, "LayoutContains tabFragmentLayout")
        return tabFragmentLayout
    }

    fun syncCreateHomeFragmentLayout(context: Context, res: Resources): View {
        val swipeRefreshLayout = SwipeRefreshLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            initColors()
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
            val kHomeAdapter = KHomeAdapter()
            adapter = kHomeAdapter
            LrcHelp.getHomeData()?.takeIf {
                it.isNotEmpty()
            }?.let {
                kHomeAdapter.notifyData(Gson().fromJson(it, object : TypeToken<MutableList<HomeItemBean>>() {}.type))
            }
        }
        swipeRefreshLayout.addView(homeFragmentView)
        return swipeRefreshLayout
    }
}