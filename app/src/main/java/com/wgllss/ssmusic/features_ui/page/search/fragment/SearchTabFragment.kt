package com.wgllss.ssmusic.features_ui.page.search.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.wgllss.core.ex.HideSoftInputFromWindow
import com.wgllss.core.ex.finishActivity
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.features_ui.home.fragment.BaseTabFragment
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel
import javax.inject.Inject

class SearchTabFragment @Inject constructor() : BaseTabFragment<HomeViewModel>() {

    private lateinit var imgBack: ImageView
    private lateinit var et_name: EditText
    private lateinit var shapeable_search: View
    private lateinit var root: View
    private val mList by lazy {
        mutableListOf<Fragment>(
            TabTitleFragment.newInstance("普通搜索", "1", KSearchFragment::class.java),
            TabTitleFragment.newInstance("高品质搜", "2", SearchFragment::class.java)
        )
    }

    override fun isLazyTab() = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::root.isInitialized) {
            root = inflater.inflate(R.layout.fragment_search_tab, container, false)
            homeTabLayout = root.findViewById(inflater.context.resources.getIdentifier("tab_view", "id", inflater.context.packageName))
            viewPager2 = root.findViewById(inflater.context.resources.getIdentifier("homeViewPager2", "id", inflater.context.packageName))
            imgBack = root.findViewById(R.id.img_back)
            et_name = root.findViewById(R.id.et_name)
            shapeable_search = root.findViewById(R.id.shapeable_search)
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imgBack.setOnClickListener {
            activity?.run { finishActivity() }
            HideSoftInputFromWindow(root)
        }
        shapeable_search.setOnClickListener {
            search()
        }
        et_name.requestFocus()
        et_name.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    search()
                    HideSoftInputFromWindow(root)
                    true
                }
                else -> {}
            }
            false
        }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    (getList()[1] as SearchFragment).takeIf {
                        it.noSearch()
                    }?.run {
                        searchKey(et_name.text.toString().trim())
                    }
                }
            }
        })
    }

    private fun search() {
        val content = et_name.text.toString().trim()
        if (viewPager2.currentItem == 0) {
            (getList()[0] as KSearchFragment).searchKey(content)
        } else {
            (getList()[1] as SearchFragment).searchKey(content)
        }
    }

    override fun getList() = mList
}