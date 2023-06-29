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
import com.wgllss.ssmusic.features_ui.page.detail.adapter.SongDetailAdapter
import com.wgllss.ssmusic.features_ui.page.detail.viewmodel.SongSingersViewModel
import com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity

class SongSingersFragment(private val encodeID: String, private val authorName: String) : BaseViewModelFragment<SongSingersViewModel>(0) {

    private lateinit var rootView: View
    private lateinit var toolbar: Toolbar
    private lateinit var img_bg: ImageView
    private lateinit var recycler_view_sheet: RecyclerView
    private lateinit var toolbar_layout: CollapsingToolbarLayout

    private val songDetailAdapter by lazy { SongDetailAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::rootView.isInitialized) {
            rootView = inflater.inflate(R.layout.fragment_song_singers_detail, container, false)
            img_bg = rootView.findViewById(R.id.img_bg)
            recycler_view_sheet = rootView.findViewById<RecyclerView?>(R.id.recycler_view_sheet).apply {
                val itemDecoration = View(context)
                val size = context.getIntToDip(1.0f).toInt()
                itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                itemDecoration.setBackgroundColor(Color.parseColor("#20000000"))
                addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
            }
            toolbar = rootView.findViewById<Toolbar>(R.id.toolbar2).apply {
                navigationIcon = resources.getDrawable(com.wgllss.music.skin.R.drawable.ic_baseline_arrow_back_24)
                (layoutParams as CollapsingToolbarLayout.LayoutParams).collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
            }
            toolbar_layout = rootView.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).apply {
                setContentScrimColor(ThemeUtils.getColorPrimary(inflater.context))
                setStatusBarScrimColor(ThemeUtils.getColorPrimary(inflater.context))
                setExpandedTitleColor(ThemeUtils.getColorPrimary(inflater.context))
//                setExpandedTitleTextAppearance(R.style.UserpageUserNameExpandedStyle)
                (layoutParams as AppBarLayout.LayoutParams).scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
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
            adapter = songDetailAdapter
            layoutManager = LinearLayoutManager(context)
            (layoutParams as CoordinatorLayout.LayoutParams).behavior = AppBarLayout.ScrollingViewBehavior()
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    viewModel.getPlayUrl(songDetailAdapter.getItem(position))
                }
            })
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (viewModel.enableLoadMore() && linearLayoutManager!!.itemCount == linearLayoutManager.findLastVisibleItemPosition() + 1) {
                        viewModel.kSingerInfo(encodeID, authorName)
                    }
                }
            })
        }

        viewModel.nowPlay.observe(viewLifecycleOwner) {
            it?.takeIf {
                it
            }?.let {
                activity?.run {
                    launchActivity(Intent(this, PlayActivity::class.java))
                }
            }
        }


    }

    override fun initObserve() {
        super.initObserve()
        viewModel?.run {
            kSingerInfo(encodeID, authorName)
            singerInfo.observe(viewLifecycleOwner) {
                img_bg.loadUrl(it.imgurl)
                toolbar_layout.title = it.singername
            }
            listLiveData.observe(viewLifecycleOwner) {
                songDetailAdapter.notifyData(it)
                songDetailAdapter.addFooter()
            }
            enableLoadeMore.observe(viewLifecycleOwner) {
                if (!it) {
                    songDetailAdapter.removeFooter()
                }
            }
        }
    }
}