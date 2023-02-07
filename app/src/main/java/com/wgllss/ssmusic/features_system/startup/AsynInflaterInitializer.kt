package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.MutableContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentContainerView
import androidx.startup.Initializer
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateItem
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateManager
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
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
            val context: Context = MutableContextWrapper(activity)
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
                val tabFragmentLayout = LayoutInflater.from(context).inflate(R.layout.fragment_home_tab, null, false)
                measureAndLayout(tabFragmentLayout)
                tabFragmentLayout
            }

            val homeNavigation = AsyncInflateItem(LaunchInflateKey.home_navigation, R.layout.home_buttom_navigation, null, null)
            AsyncInflateManager.instance.asyncInflate(context, homeNavigation)
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