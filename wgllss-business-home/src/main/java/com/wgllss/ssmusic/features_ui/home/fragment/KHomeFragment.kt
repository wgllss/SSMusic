package com.wgllss.ssmusic.features_ui.home.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.data.DataContains
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import com.wgllss.ssmusic.features_system.startup.HomeContains
import com.wgllss.ssmusic.features_system.startup.LaunchInflateKey
import com.wgllss.ssmusic.features_ui.home.adapter.KHomeAdapter
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel

class KHomeFragment : TabTitleFragment<HomeViewModel>() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var kHomeAdapter: KHomeAdapter

    override fun activitySameViewModel() = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (!this::swipeRefreshLayout.isInitialized) {
            swipeRefreshLayout = HomeContains.getViewByKey(inflater.context, LaunchInflateKey.home_fragment)!! as SwipeRefreshLayout
            recyclerView = swipeRefreshLayout.findViewById(R.id.home_recycle_view)
            kHomeAdapter = recyclerView.adapter as KHomeAdapter
        }
        return swipeRefreshLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.homeKMusic()
        }
        recyclerView?.apply {
            layoutManager = GridLayoutManager(requireContext(), 12).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(i: Int): Int {
                        //spanCount 当横向时，2代表每列2行，
                        return when (adapter!!.getItemViewType(i)) {
                            0, 3, 5 -> 12 //   spanCount/12  个位置占满一格
                            6 -> 3
//                            5 -> 6
                            else -> 4 // spanCount/4  个位置占满一格
                        }
                    }
                }
            }
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    val item = kHomeAdapter.getItem(position)
                    when (kHomeAdapter.getItemViewType(position)) {
                        0 -> {
                            val clazzName = when (item.homeLableBean!!.labType) {
                                1 -> "com.wgllss.ssmusic.features_ui.page.newsongs.activity.NewSongsActivity"
                                2 -> "com.wgllss.ssmusic.features_ui.page.classics.activity.ClassicsActivity"
                                else -> return
                            }
                            startToDetailActivity(clazzName)
                        }
                        1 -> {
                            viewModel.getMusicInfo(item.kMusicItemBean!!)
                        }
                        2 -> {
                            startToDetail(0, item.kKMusicHotSongBean!!.detailUrl)
                        }
                        3 -> {
                            startToDetail(1, item.kRankExBean!!.linkUrl)
                        }
                        4 -> {
                            startToDetail(2, item.kSingerBean!!.encodeID, item.kSingerBean!!.name)
                        }
                        5 -> {
//                            when (item.kMenuBean?.itemID) {
//                                1 ->
//                            startToDetailActivity("com.wgllss.ssmusic.features_ui.page.classics.activity.ClassicsActivity")
//                                4 ->
                            startToDetailActivity("com.wgllss.ssmusic.features_ui.page.search.activity.SearchActivity")
//                            }
                        }
                    }
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

        if (MMKVHelp.getHomeTab1Data().isEmpty()) {
            DataContains.list.observe(viewLifecycleOwner) {
                kHomeAdapter.notifyData(it)
            }
        }

        viewModel.list.observe(viewLifecycleOwner) {
            kHomeAdapter.notifyData(it)
        }
    }

    private fun startToDetailActivity(className: String) {
        context?.run {
            try {
                val clazz = Class.forName(className)
                startActivity(Intent(this, clazz).apply { putExtra("SEARCH_TYPE_KEY", 1) })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startToDetail(type: Int, encode: String, authorName: String = "") {
        context?.let {
            val intent = Intent(it, Class.forName("com.wgllss.ssmusic.features_ui.page.detail.activity.SongSheetDetailActivity"))
                .putExtra("ENCODE_ID_KEY", encode)
                .putExtra("TYPE_KEY", type)
                .putExtra("AUTHOR_NAME_KEY", authorName)
            it.startActivity(intent)
        }
    }

    override fun initObserve() {
        super.initObserve()
        viewModel?.run {
            showUIDialog.observe(viewLifecycleOwner) {
                if (!isClick)
                    swipeRefreshLayout.isRefreshing = it.isShow
                else
                    if (it.isShow) showloading(it.msg) else hideLoading()
            }
        }
    }
}