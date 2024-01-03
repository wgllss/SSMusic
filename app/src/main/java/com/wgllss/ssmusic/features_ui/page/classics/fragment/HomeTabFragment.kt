package com.wgllss.ssmusic.features_ui.page.classics.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.wgllss.core.ex.finishActivity
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.features_ui.home.fragment.BaseTabFragment
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel


class HomeTabFragment : BaseTabFragment<HomeViewModel>() {


    private lateinit var imgBack: ImageView
    private lateinit var root: View
    override fun isLazyTab() = false

    override fun getTextColor(): Int {
        val typedValue = TypedValue()
        requireActivity().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::root.isInitialized) {
            root = inflater.inflate(R.layout.fragment_classes_songsheet, container, false)
            homeTabLayout = root.findViewById(inflater.context.resources.getIdentifier("tab_view", "id", inflater.context.packageName))
            viewPager2 = root.findViewById(inflater.context.resources.getIdentifier("homeViewPager2", "id", inflater.context.packageName))
            imgBack = root.findViewById(R.id.img_back)
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imgBack.setOnClickListener {
            activity?.run { finishActivity() }
        }
    }

    override fun getList() = mutableListOf<Fragment>(
        TabTitleFragment.newInstance("推荐", "index", HomeFragment::class.java),
        TabTitleFragment.newInstance("华语", "forum-1", HomeFragment::class.java),
        TabTitleFragment.newInstance("日韩", "forum-15", HomeFragment::class.java),
        TabTitleFragment.newInstance("欧美", "forum-10", HomeFragment::class.java),
        TabTitleFragment.newInstance("remix", "forum-11", HomeFragment::class.java),
        TabTitleFragment.newInstance("纯音乐", "forum-12", HomeFragment::class.java),
        TabTitleFragment.newInstance("异次元", "forum-13", HomeFragment::class.java),
    )
}