package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wgllss.core.material.ThemeUtils
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.widget.SideBar
import com.wgllss.ssmusic.ex.initColors
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.page.detail.activity.SongSheetDetailActivity
import com.wgllss.ssmusic.features_ui.page.home.adapter.SingersAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.KSingerViewModel

class KSingersFragment : TabTitleFragment<KSingerViewModel>() {

    private lateinit var rootView: View
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recycler_view: RecyclerView
    private lateinit var side_bar: SideBar
    private val singersAdapter by lazy { SingersAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::rootView.isInitialized) {
            rootView = inflater.inflate(R.layout.fragment_singers, container, false)
            swipeRefreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.swiperefreshlayout).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                initColors()
            }
            recycler_view = rootView.findViewById(R.id.recycler_view)
            side_bar = rootView.findViewById(R.id.side_bar)
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
        swipeRefreshLayout.setOnRefreshListener {
            lazyLoad()
        }
        recycler_view.apply {
            adapter = singersAdapter
            addItemDecoration(GroupItemDecoration(singersAdapter).apply {
//                setColor(context.resources.getColor(com.wgllss.music.skin.R.color.color_recycler_singer_header), context.resources.getColor(com.wgllss.music.skin.R.color.color_recycler_singer_header))
                setColor(ThemeUtils.getColorPrimary(context), context.resources.getColor(com.wgllss.music.skin.R.color.color_recycler_singer_header))
            })
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    singersAdapter.getItem(position).run {
                        SongSheetDetailActivity.startSongSheetDetailActivity(requireActivity(), encode_id, 2, singername)
                    }
                }
            })
        }
        side_bar.setOnStrSelectCallBack(object : SideBar.ISideBarSelectCallBack {
            override fun onSelectStr(index: Int, selectStr: String) {
                recycler_view.layoutManager?.takeIf {
                    it.itemCount > 0 && singersAdapter.itemCount > 0
                }?.run {
                    scrollToPosition(singersAdapter.getLetterPosition(selectStr))
                }
//                recycler_view.layoutManager?.scrollToPosition(singersAdapter.getLetterPosition(selectStr))
            }
        })
    }

    override fun lazyLoad() {
        viewModel?.kSingers(key)
    }

    override fun initObserve() {
        viewModel?.run {
            initKey(key)
            result[key]?.observe(viewLifecycleOwner) {
                singersAdapter.notifyData(it)
            }
            showUIDialog.observe(viewLifecycleOwner) {
                swipeRefreshLayout.isRefreshing = it.isShow
            }
            errorMsgLiveData.observe(viewLifecycleOwner) {
                onToast(it)
            }
        }
    }
}