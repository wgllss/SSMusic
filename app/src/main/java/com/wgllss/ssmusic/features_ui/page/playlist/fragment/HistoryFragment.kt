package com.wgllss.ssmusic.features_ui.page.playlist.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.fragment.BaseMVVMFragment
import com.wgllss.core.units.LogTimer
import com.wgllss.core.units.WLog
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.FragmentHistoryBinding
import com.wgllss.ssmusic.features_ui.page.playlist.adapter.PlayListAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel2
import com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment @Inject constructor() : BaseMVVMFragment<HomeViewModel2, FragmentHistoryBinding>(R.layout.fragment_history) {

    @Inject
    lateinit var playListAdapterL: Lazy<PlayListAdapter>

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
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            viewModel = this@HistoryFragment.viewModel
            adapter = playListAdapterL.get()
            lifecycleOwner = this@HistoryFragment
            executePendingBindings()
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
                        RecyclerView.SCROLL_STATE_IDLE -> activity?.takeIf {
                            !it.isFinishing
                        }?.run {
                            Glide.with(this).resumeRequests()
                        }
                        else -> activity?.let {
                            Glide.with(it).pauseRequests()
                        }
                    }
                }
            })
            imgBack.setOnClickListener {
                activity?.run {
                    finishActivity()
                }
            }
//            img_back
        }
        viewModel.liveData.observe(viewLifecycleOwner) {
            playListAdapterL.get().notifyData(it)
        }
        viewModel.rootMediaId.observe(viewLifecycleOwner) {
            it?.let { viewModel.subscribeByMediaID(it) }
        }
        playListAdapterL.get().setBlockDelete {
            viewModel.deleteFromPlayList(it)
        }
    }

    override fun onStart() {
        LogTimer.LogE(this, "onStart")
        super.onStart()
    }

    override fun onResume() {
        LogTimer.LogE(this, "onResume")
        super.onResume()
        viewModel.currentMediaID.observe(viewLifecycleOwner) {
            playListAdapterL.get().currentMediaID = it
            playListAdapterL.get().notifyDataSetChanged()
        }
    }

    override fun onStop() {
        LogTimer.LogE(this, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        LogTimer.LogE(this, "onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        LogTimer.LogE(this, "onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        WLog.e(this, "onDetach")
        super.onDetach()
    }
}