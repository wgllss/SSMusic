package com.wgllss.ssmusic.features_ui.home.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.ex.initColors
import com.wgllss.ssmusic.features_ui.home.adapter.KRankListAdapter
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.home.viewmodels.RankViewModel

class RankFragment : TabTitleFragment<HomeViewModel>() {
    private lateinit var rvPlList: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val kRankListAdapter by lazy { KRankListAdapter() }
    private val homeTabViewModel by lazy { viewModels<RankViewModel>().value }

    companion object {
        private const val TITLE_KEY = "TITLE_KEY"
        private const val KEY = "KEY"

        fun newInstance(titleS: String, keyS: String): RankFragment {
            val fragment = RankFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, titleS)
                    putString(KEY, keyS)
                }
                title = titleS
            }
            return fragment
        }
    }

    override fun activitySameViewModel() = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (!this::swipeRefreshLayout.isInitialized) {
            swipeRefreshLayout = SwipeRefreshLayout(inflater.context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                initColors()
            }
            rvPlList = RecyclerView(inflater.context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                layoutManager = GridLayoutManager(requireContext(), 3)
                setHasFixedSize(true)
                val itemDecoration = View(context)
                val size = context.getIntToDip(5.0f).toInt()
                itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                itemDecoration.setBackgroundColor(Color.parseColor("#20000000"))
                addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
            }
            swipeRefreshLayout.addView(rvPlList)
        }
        return swipeRefreshLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvPlList?.apply {
            adapter = kRankListAdapter
            setHasFixedSize(true)
//            setItemViewCacheSize(12)
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    try {
                        val clazz = Class.forName("com.wgllss.ssmusic.features_ui.page.detail.activity.SongSheetDetailActivity")
                        context.startActivity(Intent(context, clazz).apply {
                            putExtra("ENCODE_ID_KEY", kRankListAdapter.getItem(position).linkUrl)
                            putExtra("TYPE_KEY", 1)
//                        putExtra("AUTHOR_NAME_KEY", authorName)
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
//                    SongSheetDetailActivity.startSongSheetDetailActivity(requireContext(), kRankListAdapter.getItem(position).linkUrl, 1)
                }
            })
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    when (newState) {
//                        //滑动停止
//                        RecyclerView.SCROLL_STATE_IDLE -> activity?.let {
//                            Glide.with(it).resumeRequests()
//                        }
//                        else -> activity?.let {
//                            Glide.with(it).pauseRequests()
//                        }
//                    }
//                }
//            })
        }
        homeTabViewModel.list.observe(viewLifecycleOwner) {
            kRankListAdapter.notifyData(it)
        }
        swipeRefreshLayout.setOnRefreshListener {
            homeTabViewModel.start()
        }
    }

    override fun lazyLoad() {
        homeTabViewModel.start()
    }


    override fun initObserve() {
        super.initObserve()
        homeTabViewModel.run {
            showUIDialog.observe(viewLifecycleOwner) {
                if (!isClick) {
                    swipeRefreshLayout.isRefreshing = it.isShow
                } else {
                    if (it.isShow) showloading(it.msg) else hideLoading()
                }
            }
            errorMsgLiveData.observe(viewLifecycleOwner) {
                onToast(it)
            }
            liveDataLoadSuccessCount.observe(viewLifecycleOwner) {
                if (it > 1) swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}