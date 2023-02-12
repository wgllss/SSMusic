package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.adapter.ViewPage2ChildFragmentAdapter
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.fragment.BaseViewModelFragment
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


class HomeTabFragment @Inject constructor() : BaseViewModelFragment<HomeViewModel>(0) {

    private lateinit var childAdapter: ViewPage2ChildFragmentAdapter
    private lateinit var homeTabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private var mTabLayoutMediator: TabLayoutMediator? = null

    override fun activitySameViewModel() = true

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
        viewModel.lazyTabViewPager2.observe(viewLifecycleOwner) {
            childAdapter = ViewPage2ChildFragmentAdapter(childFragmentManager, lifecycle)
            viewPager2.adapter = childAdapter
            mTabLayoutMediator = TabLayoutMediator(homeTabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
                val textView = TextView(requireContext())
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
//                textView.setTextColor(resources.getColor(if (position == 0) R.color.colorPrimary else R.color.white))
                textView.setTextColor(resources.getColor(R.color.white))
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
//                            setTextColor(resources.getColor(R.color.colorPrimary))
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    tab?.customView?.takeIf {
                        it is TextView
                    }?.run {
                        (this as TextView).run {
                            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
//                            setTextColor(resources.getColor(R.color.white))
                        }
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }

            })
            childAdapter.notifyData(getList())
        }
        LogTimer.LogE(this, "onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        LogTimer.LogE(this, "onResume")
    }

    private fun getList() = mutableListOf<Fragment>(
        HomeFragment.newInstance("首页", "index"),
        HomeFragment.newInstance("华语", "forum-1"),
        HomeFragment.newInstance("日韩", "forum-15"),
        HomeFragment.newInstance("欧美", "forum-10"),
        HomeFragment.newInstance("remix", "forum-11"),
        HomeFragment.newInstance("纯音乐", "forum-12"),
        HomeFragment.newInstance("异次元", "forum-13"),
    )

    override fun onDetach() {
        super.onDetach()
        mTabLayoutMediator?.detach()
    }
}