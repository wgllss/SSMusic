package com.wgllss.ssmusic.features_ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.wgllss.core.units.LogTimer
import com.wgllss.core.units.WLog
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.features_system.startup.HomeContains
import com.wgllss.ssmusic.features_system.startup.LaunchInflateKey
import com.wgllss.ssmusic.features_ui.home.adapter.KNewSongAdapter
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.home.viewmodels.NewListTabViewModel

class KNewLisFragment : TabTitleFragment<HomeViewModel>() {
    private lateinit var rvPlList: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val homeItem1Adapter by lazy { KNewSongAdapter() }

    private val homeTabViewModel = viewModels<NewListTabViewModel>()

    companion object {
        private const val TITLE_KEY = "TITLE_KEY"
        private const val KEY = "KEY"

        fun newInstance(titleS: String, keyS: String): KNewLisFragment {
            val fragment = KNewLisFragment().apply {
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
            rvPlList = swipeRefreshLayout.findViewById(R.id.home_recycle_view)
        }
        return swipeRefreshLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeTabViewModel.value.initKey(key)
        swipeRefreshLayout.setOnRefreshListener {
            homeTabViewModel.value.getData(key)
        }
        homeTabViewModel.value.result[key]?.observe(viewLifecycleOwner) {
            WLog.e(this@KNewLisFragment, key)
            homeItem1Adapter.notifyData(it)
        }
        rvPlList.apply {
            adapter = homeItem1Adapter
//            layoutManager = GridLayoutManager(context, 3)
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    viewModel.getMusicInfo(homeItem1Adapter.getItem(position))
                }
            })
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

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
        }
        LogTimer.LogE(this, "key:$key")
        homeTabViewModel.value.getData(key)
    }

    override fun initObserve() {
        super.initObserve()
        homeTabViewModel.value?.run {
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