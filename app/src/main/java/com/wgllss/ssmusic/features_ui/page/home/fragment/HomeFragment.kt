package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.bumptech.glide.Glide
import com.wgllss.ssmusic.R
import com.wgllss.core.asyninflater.LaunchInflateKey
import com.wgllss.core.asyninflater.LayoutContains
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.fragment.BaseViewModelFragment
import com.wgllss.core.units.LogTimer
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.features_ui.page.home.adapter.PlayListAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity
import dagger.Lazy
import javax.inject.Inject

class HomeFragment @Inject constructor() : BaseViewModelFragment<HomeViewModel>(R.layout.fragment_home) {

    @Inject
    lateinit var playListAdapterL: Lazy<PlayListAdapter>

    private lateinit var rvPlList: RecyclerView

    override fun activitySameViewModel() = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LogTimer.LogE(this, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogTimer.LogE(this, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogTimer.LogE(this, "onCreateView")
        val view = LayoutContains.getViewByKey(inflater.context, LaunchInflateKey.home_fragment)!!
        rvPlList = view.findViewById(inflater.context.resources.getIdentifier("rv_pl_list", "id", inflater.context.packageName))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogTimer.LogE(this, "onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogTimer.LogE(this, "onActivityCreated")
        rvPlList.adapter = playListAdapterL.get()
        rvPlList.addOnItemTouchListener(object : OnRecyclerViewItemClickListener(rvPlList) {
            override fun onItemClickListener(itemRootView: View, position: Int) {
                playListAdapterL.get().getItem(position)?.run {
                    viewModel.mediaItemClicked(this, this.description.extras)
                }
                activity?.let { it.launchActivity(Intent(it, PlayActivity::class.java)) }
            }
        })
        rvPlList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    //滑动停止
                    SCROLL_STATE_IDLE -> activity?.let {
                        Glide.with(it).resumeRequests()
                    }
                    else -> activity?.let {
                        Glide.with(it).pauseRequests()
                    }
                }
            }
        })

        viewModel.liveData.observe(viewLifecycleOwner) {
            playListAdapterL.get().notifyData(it)
        }
        playListAdapterL.get().setBlockDelete {
            viewModel.deleteFromPlayList(it)
        }
        LogTimer.LogE(this, "onActivityCreated 1")
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentMediaID.observe(viewLifecycleOwner) {
            playListAdapterL.get().currentMediaID = it
            playListAdapterL.get().notifyDataSetChanged()
        }
        LogTimer.LogE(this, "onResume")
    }
}