package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.core.adapter.ViewPage2ChildFragmentAdapter
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.switchFragment
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.databinding.ActivityHomeBinding
import com.wgllss.ssmusic.features_third.um.UMHelp
import com.wgllss.ssmusic.features_ui.page.home.fragment.HomeFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.HomeTabFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.SearchFragment
import com.wgllss.ssmusic.features_ui.page.home.fragment.SettingFragment
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseMVVMActivity<HomeViewModel, ActivityHomeBinding>(0) {

//    @Inject
//    lateinit var tabAdapter: Lazy<TabAdapter>

    @Inject
    lateinit var homeFragmentL: Lazy<HomeTabFragment>

    @Inject
    lateinit var searchFragmentL: Lazy<SearchFragment>

    @Inject
    lateinit var settingFragmentL: Lazy<SettingFragment>

    private lateinit var childAdapter: ViewPage2ChildFragmentAdapter
    private lateinit var homeTabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var contentLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun initControl(savedInstanceState: Bundle?) {
        LogTimer.LogE(this@HomeActivity, "initControl")
        contentLayout = LayoutContains.getViewByKey(this, LaunchInflateKey.home_activity)!!
        addContentView(contentLayout, contentLayout.layoutParams)
//        val rvPlList = contentLayout.findViewById<RecyclerView>(R.id.rv_tab_list)
//        rvPlList.adapter = TabAdapter(
//            mutableListOf(
//                "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深",
////                                "张学友", "陈奕迅", "刘德华", "张杰", "谭咏麟",
////                                "Yanni", "梁静茹", "半吨兄弟", "汪苏泷", "Beyond",
//                "王菲", "林俊杰", "许嵩", "胡彦斌", "周深",
//                "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深",
//                "周杰伦", "林俊杰", "许嵩", "胡彦斌",
//                "张学友"
//            )
//        )

//        rvPlList.adapter = tabAdapter.get()
//        tabAdapter.get().notifyData(
//            mutableListOf(
//                "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深",
//                "张学友", "陈奕迅", "刘德华", "张杰", "谭咏麟",
//                "Yanni", "梁静茹", "半吨兄弟", "汪苏泷", "Beyond",
//                "王菲", "林俊杰", "许嵩", "胡彦斌", "周深",
//                "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深",
//                "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深"
//            )
//        )

        homeTabLayout = contentLayout.findViewById(R.id.homeTabLayout)
        viewPager2 = contentLayout.findViewById(R.id.homeViewPager2)
        childAdapter = ViewPage2ChildFragmentAdapter(getList(), supportFragmentManager, lifecycle)
        viewPager2.adapter = childAdapter
        TabLayoutMediator(homeTabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
            val textView = TextView(this)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            textView.setTextColor(resources.getColor(if (position == 0) R.color.colorPrimary else R.color.white))
            textView.text = tab.text
            tab.customView = textView
            textView.text = (childAdapter.list[position] as HomeFragment).key
        }.apply(TabLayoutMediator::attach)
        homeTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab?.customView?.takeIf {
                    it is TextView
                }?.run {
                    (this as TextView).run {
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
                        setTextColor(resources.getColor(R.color.colorPrimary))
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab?.customView?.takeIf {
                    it is TextView
                }?.run {
                    (this as TextView).run {
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
                        setTextColor(resources.getColor(R.color.white))
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

        })
//        setCurrentFragment(homeFragmentL.get())
        LogTimer.LogE(this@HomeActivity, "initControl after")
    }

    override fun initValue() {
    }

    override fun onBackPressed() {
        exitApp()
    }

    override fun lazyInitValue() {
        LogTimer.LogE(this, "lazyInitValue")
        val navigationView = LayoutContains.getViewByKey(this, LaunchInflateKey.home_navigation)!!
        addContentView(navigationView, navigationView.layoutParams)
        initNavigation(navigationView as BottomNavigationView)
//        val rvPlList = contentLayout.findViewById<RecyclerView>(R.id.rv_tab_list)
//        rvPlList.adapter = TabAdapter(
//            mutableListOf(
//                "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深",
////                                "张学友", "陈奕迅", "刘德华", "张杰", "谭咏麟",
////                                "Yanni", "梁静茹", "半吨兄弟", "汪苏泷", "Beyond",
//                "王菲", "林俊杰", "许嵩", "胡彦斌", "周深",
//                "周杰伦", "林俊杰", "许嵩", "胡彦斌", "周深",
//                "周杰伦", "林俊杰", "许嵩", "胡彦斌",
//                "张学友"
//            )
//        )

        viewModel.start()
        viewModel.rootMediaId.observe(this) {
            it?.let { viewModel.subscribeByMediaID(it) }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            UMHelp.umInit(this@HomeActivity)
        }
    }

    private fun initNavigation(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.apply {
            with(menu) {
//                clear()
                get(0).setIcon(R.drawable.ic_home_black_24dp)
                get(1).setIcon(R.drawable.ic_dashboard_black_24dp)
                get(2).setIcon(R.drawable.ic_notifications_black_24dp)
//                add(0, R.id.fmt_a, 0, resources.getString(R.string.title_home)).setIcon(R.drawable.ic_home_black_24dp)
//                add(0, R.id.fmt_b, 0, resources.getString(R.string.title_search)).setIcon(R.drawable.ic_dashboard_black_24dp)
//                add(0, R.id.fmt_c, 0, resources.getString(R.string.title_setting)).setIcon(R.drawable.ic_notifications_black_24dp)
            }
            setOnItemSelectedListener { menu ->
                when (menu.itemId) {
                    R.id.fmt_a -> {
//                    setCurrentFragment(homeFragmentL.get())
                        contentLayout.findViewById<View>(R.id.nav_host_fragment_activity_main).apply {
                            visibility = View.GONE
                        }
                    }
                    R.id.fmt_b -> {
                        contentLayout.findViewById<View>(R.id.nav_host_fragment_activity_main).apply {
                            visibility = View.VISIBLE
                            bringToFront()
                        }
                        setCurrentFragment(searchFragmentL.get())
                    }
                    R.id.fmt_c -> {
                        contentLayout.findViewById<View>(R.id.nav_host_fragment_activity_main).apply {
                            visibility = View.VISIBLE
                            bringToFront()
                        }
                        setCurrentFragment(settingFragmentL.get())
                    }
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        switchFragment(fragment, viewModel.mCurrentFragmentTAG, R.id.nav_host_fragment_activity_main)
        viewModel.mCurrentFragmentTAG.delete(0, viewModel.mCurrentFragmentTAG.toString().length)
        viewModel.mCurrentFragmentTAG.append(fragment.javaClass.simpleName)
    }

    private fun getList() = mutableListOf(
        HomeFragment("首页", "index"),
        HomeFragment("华语", "forum-1"),
        HomeFragment("日韩", "forum-15"),
        HomeFragment("欧美", "forum-10"),
        HomeFragment("remix", "thread-21683"),
        HomeFragment("纯音乐", "forum-12"),
        HomeFragment("异次元", "forum-13"),
    )
}