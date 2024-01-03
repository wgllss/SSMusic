package com.wgllss.ssmusic.features_ui.page.newsongs.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.wgllss.core.ex.finishActivity
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.features_ui.home.fragment.BaseTabFragment
import com.wgllss.ssmusic.features_ui.home.fragment.KNewLisFragment
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel

class NewSongsFragment : BaseTabFragment<HomeViewModel>() {
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
            root = inflater.inflate(R.layout.fragment_new_songs, container, false)
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
        TabTitleFragment.newInstance("华语", "https://m.kugou.com/newsong/index", KNewLisFragment::class.java),
        TabTitleFragment.newInstance("欧美", "https://m.kugou.com/newsong/index/2", KNewLisFragment::class.java),
        TabTitleFragment.newInstance("韩国", "https://m.kugou.com/newsong/index/4", KNewLisFragment::class.java),
        TabTitleFragment.newInstance("日本", "https://m.kugou.com/newsong/index/5", KNewLisFragment::class.java)
    )
}