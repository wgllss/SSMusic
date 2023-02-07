package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.adapter.ViewPage2ChildFragmentAdapter
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.fragment.BaseFragment
import com.wgllss.ssmusic.core.units.LogTimer
import java.lang.reflect.Field
import javax.inject.Inject


class HomeTabFragment @Inject constructor() : BaseFragment(R.layout.fragment_home_tab) {

    private lateinit var childAdapter: ViewPage2ChildFragmentAdapter
    private lateinit var homeTabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogTimer.LogE(this, "onCreateView")
        val view = LayoutContains.getViewByKey(inflater.context, LaunchInflateKey.home_tab_fragment)!!
        homeTabLayout = view.findViewById(inflater.context.resources.getIdentifier("homeTabLayout", "id", inflater.context.packageName))
        viewPager2 = view.findViewById(inflater.context.resources.getIdentifier("homeViewPager2", "id", inflater.context.packageName))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogTimer.LogE(this, "onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        childAdapter = ViewPage2ChildFragmentAdapter(getList(), childFragmentManager, lifecycle)
        viewPager2.adapter = childAdapter
        TabLayoutMediator(homeTabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
            val textView = TextView(requireContext())
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            textView.setTextColor(resources.getColor(if (position == 0) R.color.colorPrimary else R.color.white))
            textView.text = tab.text
            tab.customView = textView
            textView.text = (childAdapter.list[position] as HomeFragment).title
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
        LogTimer.LogE(this, "onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        LogTimer.LogE(this, "onResume")
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