package com.wgllss.ssmusic.features_ui.home.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.ex.initColors
import com.wgllss.ssmusic.features_ui.home.adapter.PinDaoAdapter
import com.wgllss.ssmusic.features_ui.home.viewmodels.PinDaoViewModel

class KPinDaoFragment : TabTitleFragment<PinDaoViewModel>() {
    private lateinit var rvPlList: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val pingDaoAdapter by lazy { PinDaoAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
            adapter = pingDaoAdapter
            setHasFixedSize(true)
//            setItemViewCacheSize(12)
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    try {
                        viewModel.playPinDaoDetail(pingDaoAdapter.getItem(position))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.start()
        }
        viewModel.start()
    }

    override fun initObserve() {
        super.initObserve()
        viewModel.run {
            list.observe(viewLifecycleOwner) {
                pingDaoAdapter.notifyData(it)
                swipeRefreshLayout.isRefreshing = false
            }
            nowPlay.observe(viewLifecycleOwner) {
                it?.takeIf {
                    it
                }?.let {
                    activity?.run {
                        val clazz = Class.forName("com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity")
                        launchActivity(Intent(this, clazz))
                    }
                }
            }
        }
    }

}