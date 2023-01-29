package com.wgllss.ssmusic.features_system.startup

import android.annotation.SuppressLint
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
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateItem
import com.wgllss.ssmusic.core.asyninflater.AsyncInflateManager
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.getIntToDip
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
import com.wgllss.ssmusic.core.widget.DividerGridItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class AsynInflaterInitializer : Initializer<Unit> {

    @SuppressLint("ResourceType")
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
            val homeFragmentLayoutAwait = async(Dispatchers.IO) {
                val homeFragmentLayout = FrameLayout(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    layoutParams = lp
                }
                val viewTitleBg = View(context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, res.getDimension(R.dimen.title_bar_height).toInt())
                    lp.gravity = Gravity.TOP and Gravity.LEFT
                    layoutParams = lp
                    setBackgroundColor(res.getColor(R.color.colorAccent))
                }
                homeFragmentLayout.addView(viewTitleBg)
                val textTitleView = TextView(context).apply {
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
                homeFragmentLayout.addView(textTitleView)
                val recyclerView = RecyclerView(context).apply {
                    LogTimer.LogE(this@AsynInflaterInitializer, "async 2 ${Thread.currentThread().name}")
                    id = res.getIdentifier("rv_pl_list", "id", activity.packageName)
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    lp.gravity = Gravity.TOP and Gravity.LEFT
                    lp.topMargin = res.getDimension(R.dimen.title_bar_height).toInt()
                    layoutParams = lp
                    setBackgroundColor(Color.WHITE)
                    layoutManager = LinearLayoutManager(context)
                    val paddingSize = res.getDimension(R.dimen.recycler_padding).toInt()
                    setPadding(paddingSize, 0, paddingSize, 0)
                    val itemDecoration = View(context)
                    val size = context.getIntToDip(1.0f).toInt()
                    itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                    itemDecoration.setBackgroundColor(Color.parseColor("#60000000"))
                    addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
                }
                homeFragmentLayout.addView(recyclerView)
                measureAndLayout(homeFragmentLayout)
                homeFragmentLayout
            }
            val homeNavigation = AsyncInflateItem(LaunchInflateKey.home_navigation, R.layout.home_buttom_navigation, null, null)
            AsyncInflateManager.instance.asyncInflate(context, homeNavigation)
            LayoutContains.putViewByKey(LaunchInflateKey.home_activity, activityLayoutViewAwait.await())
            LayoutContains.putViewByKey(LaunchInflateKey.home_fragment, homeFragmentLayoutAwait.await())
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