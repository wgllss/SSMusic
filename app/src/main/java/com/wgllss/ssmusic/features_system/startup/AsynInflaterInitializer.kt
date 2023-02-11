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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE
import com.tencent.mmkv.MMKV
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.getIntToDip
import com.wgllss.ssmusic.core.ex.initColors
import com.wgllss.ssmusic.core.ex.toTheme
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
import com.wgllss.ssmusic.core.widget.DividerGridItemDecoration
import com.wgllss.ssmusic.features_system.startup.lazyhome.AsyncHomeLayout
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
                LayoutContains.putViewByKey(LaunchInflateKey.home_activity, AsyncHomeLayout.syncCreateHomeActivityLayout(context, res))
            }
            async(Dispatchers.IO) {
                LayoutContains.putViewByKey(LaunchInflateKey.home_navigation, AsyncHomeLayout.syncCreateHomeNavigationLayout(context, res))
            }
            async(Dispatchers.IO) {
                LayoutContains.putViewByKey(LaunchInflateKey.home_tab_fragment, AsyncHomeLayout.syncCreateHomeTabFragmentLayout(context, res))
            }
            async(Dispatchers.IO) {
                LayoutContains.putViewByKey(LaunchInflateKey.home_fragment, AsyncHomeLayout.syncCreateHomeFragmentLayout(context, res))
            }
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