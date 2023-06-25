package com.wgllss.ssmusic.features_ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.data.DataContains
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import com.wgllss.ssmusic.features_system.startup.HomeContains
import com.wgllss.ssmusic.features_system.startup.LaunchInflateKey
import com.wgllss.ssmusic.features_ui.home.adapter.KHomeAdapter
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KHomeFragment : TabTitleFragment<HomeViewModel>() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {
        private const val TITLE_KEY = "TITLE_KEY"
        private const val KEY = "KEY"

        fun newInstance(titleS: String, keyS: String): KHomeFragment {
            val fragment = KHomeFragment().apply {
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
            swipeRefreshLayout = HomeContains.getViewByKey(inflater.context, LaunchInflateKey.home_fragment)!! as SwipeRefreshLayout
            recyclerView = swipeRefreshLayout.findViewById(R.id.home_recycle_view)
        }
        return swipeRefreshLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.homeKMusic()
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    //滑动停止
                    RecyclerView.SCROLL_STATE_IDLE -> activity?.let {
                        Glide.with(it).resumeRequests()
                    }
                    else -> activity?.let {
                        Glide.with(it).pauseRequests()
                    }
                }
            }
        })
        if (LrcHelp.getHomeData().isEmpty()) {
            DataContains.list.observe(viewLifecycleOwner) {
                (recyclerView.adapter as KHomeAdapter).notifyData(it)
            }
        }
        (recyclerView.adapter as KHomeAdapter).setOnItemClickNewList {
            viewModel.getMusicInfo(it)
        }

        viewModel.list.observe(viewLifecycleOwner) {
            (recyclerView.adapter as KHomeAdapter).notifyData(it)
        }

    }

    override fun initObserve() {
//        super.initObserve()
        viewModel?.run {
            showUIDialog.observe(viewLifecycleOwner) {
                swipeRefreshLayout.isRefreshing = it.isShow
            }
        }
    }
}