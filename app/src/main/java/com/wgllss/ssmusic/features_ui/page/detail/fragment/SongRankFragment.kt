package com.wgllss.ssmusic.features_ui.page.detail.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.ex.loadUrl
import com.wgllss.core.fragment.BaseViewModelFragment
import com.wgllss.core.material.ThemeUtils
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.features_ui.page.detail.adapter.SongRankAdapter
import com.wgllss.ssmusic.features_ui.page.detail.viewmodel.SongRankDetailtViewModel
import com.wgllss.ssmusic.features_ui.page.mv.activity.KMVActivity
import com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity

class SongRankFragment(private val encodeID: String) : BaseViewModelFragment<SongRankDetailtViewModel>(0) {

    private lateinit var rootView: View
    private lateinit var toolbar: Toolbar
    private lateinit var img_bg: ImageView
    private lateinit var recycler_view_sheet: RecyclerView
    private lateinit var toolbar_layout: CollapsingToolbarLayout

    private val homeItem1Adapter by lazy { SongRankAdapter() }

//    override fun activitySameViewModel() = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::rootView.isInitialized) {
            rootView = inflater.inflate(R.layout.fragment_song_rank_detail, container, false)
            img_bg = rootView.findViewById(R.id.img_bg)
            toolbar = rootView.findViewById<Toolbar>(R.id.toolbar2).apply {
                navigationIcon = resources.getDrawable(com.wgllss.music.skin.R.drawable.ic_baseline_arrow_back_24)
                (layoutParams as CollapsingToolbarLayout.LayoutParams).collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
            }
            toolbar_layout = rootView.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).apply {
                setContentScrimColor(ThemeUtils.getColorPrimary(inflater.context))
                setStatusBarScrimColor(ThemeUtils.getColorPrimary(inflater.context))
//                expandedTitleGravity = Gravity.TOP or Gravity.LEFT
                setExpandedTitleColor(Color.TRANSPARENT)
//                setExpandedTitleTextAppearance(R.style.UserpageUserNameExpandedStyle)
                (layoutParams as AppBarLayout.LayoutParams).scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
            }
            recycler_view_sheet = rootView.findViewById<RecyclerView?>(R.id.recycler_view_sheet).apply {
                val itemDecoration = View(context)
                val size = context.getIntToDip(1.0f).toInt()
                itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                itemDecoration.setBackgroundColor(Color.parseColor("#20000000"))
                addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
            }
        }
        rootView?.parent?.takeIf {
            it is ViewGroup
        }?.let {
            (it as ViewGroup).removeView(rootView)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar?.setNavigationOnClickListener {
            requireActivity().finishActivity()
        }
        recycler_view_sheet.apply {
            adapter = homeItem1Adapter
            layoutManager = LinearLayoutManager(context)
            (layoutParams as CoordinatorLayout.LayoutParams).behavior = AppBarLayout.ScrollingViewBehavior()
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    viewModel.doPlay(homeItem1Adapter.getItem(position))
                }
            })
        }
    }

    override fun initObserve() {
        super.initObserve()
        viewModel?.run {
            kSongRankDetail(encodeID)
            nowPlay.observe(viewLifecycleOwner) {
                it?.takeIf {
                    it
                }?.let {
                    activity?.run {
                        launchActivity(Intent(this, PlayActivity::class.java))
                    }
                }
            }

            songSheetDetail.observe(viewLifecycleOwner) {
                it.info.run {
                    img_bg.loadUrl(img_cover.ifEmpty { imgurl })
                    toolbar_layout.title = rankname
                }
                homeItem1Adapter.notifyData(it.listData)
            }
            liveDataMV.observe(viewLifecycleOwner) {
                activity?.run {
                    KMVActivity.startKMVActivity(this, it.url, it.title)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.nowPlay.postValue(false)
    }
}