package com.wgllss.ssmusic.features_ui.home.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wgllss.core.adapter.ViewPage2ChildFragmentAdapter
import com.wgllss.core.fragment.BaseViewModelFragment
import com.wgllss.core.material.ThemeUtils
import com.wgllss.core.units.LogTimer
import com.wgllss.ssmusic.features_system.startup.HomeContains
import com.wgllss.ssmusic.features_system.startup.LaunchInflateKey
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel

abstract class BaseTabFragment<VM : HomeViewModel> : BaseViewModelFragment<VM>(0) {
    private lateinit var childAdapter: ViewPage2ChildFragmentAdapter
    protected lateinit var homeTabLayout: TabLayout
    protected lateinit var viewPager2: ViewPager2
    private var mTabLayoutMediator: TabLayoutMediator? = null

    override fun activitySameViewModel() = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogTimer.LogE(this, "onCreateView")
        val view = HomeContains.getViewByKey(inflater.context, LaunchInflateKey.home_tab_fragment_layout)!!
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
            childAdapter.notifyData(getList())
            mTabLayoutMediator = TabLayoutMediator(homeTabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
                val textView = TextView(requireContext())
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
                textView.setTextColor(ThemeUtils.getColorOnPrimary(requireContext()))
                tab.customView = textView
                textView.text = (childAdapter.list[position] as TabTitleFragment<*>).title
            }
//            .apply(TabLayoutMediator::attach)
            viewPager2.adapter = childAdapter
            mTabLayoutMediator?.attach()
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
            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position > 0 && viewPager2.offscreenPageLimit != childAdapter.itemCount)
                        viewPager2.offscreenPageLimit = childAdapter.itemCount
                }
            })
        }
        LogTimer.LogE(this, "onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        LogTimer.LogE(this, "onResume")
    }

    open fun isLazyTab(): Boolean = true

    abstract fun getList(): MutableList<Fragment>

    override fun onDetach() {
        super.onDetach()
        mTabLayoutMediator?.detach()
    }
}