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
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.getIntToDip
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.ScreenManager
import com.wgllss.ssmusic.core.widget.DividerGridItemDecoration
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AsynInflaterInitializer : Initializer<Unit> {

    @SuppressLint("ResourceType")
    override fun create(activity: Context) {
        GlobalScope.launch {
            LogTimer.LogE(this@AsynInflaterInitializer, "create ${Thread.currentThread().name}")
            ScreenManager.initScreenSize(activity)
            val context: Context = MutableContextWrapper(activity)
            val res = context.resources
            val activityLayoutView = FragmentContainerView(context).apply {
                val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                lp.bottomMargin = res.getDimension(R.dimen.navigation_height).toInt()
                layoutParams = lp
                id = res.getIdentifier("nav_host_fragment_activity_main", "id", activity.packageName)
            }
            measureAndLayout(activityLayoutView)
            LayoutContains.putViewByKey(LaunchInflateKey.home_activity, activityLayoutView)
            LogTimer.LogE(this@AsynInflaterInitializer, "LayoutContains 0")

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
            LayoutContains.putViewByKey(LaunchInflateKey.home_fragment, homeFragmentLayout)
            LogTimer.LogE(this@AsynInflaterInitializer, "LayoutContains")


//            AsyncInflateManager.initScreenSize(activity)
//            val context: Context = MutableContextWrapper(activity)
//            val homeActivity = AsyncInflateItem(LaunchInflateKey.home_activity, R.layout.activity_home, null, null)
//            val homeNavigation = AsyncInflateItem(LaunchInflateKey.home_navigation, R.layout.home_buttom_navigation, null, null)
//            val homeFragment = AsyncInflateItem(LaunchInflateKey.home_fragment, R.layout.fragment_home, null, null)
//            AsyncInflateManager.instance.asyncInflate(context, homeFragment, homeNavigation)
//            AsyncInflateManager.instance.asyncInflate(context, homeActivity, homeNavigation, homeFragment)
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