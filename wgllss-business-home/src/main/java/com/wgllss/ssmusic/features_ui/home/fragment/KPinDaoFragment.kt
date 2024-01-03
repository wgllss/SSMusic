package com.wgllss.ssmusic.features_ui.home.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.ex.initColors
import com.wgllss.ssmusic.features_ui.home.adapter.PinDaoAdapter
import com.wgllss.ssmusic.features_ui.home.adapter.PinDaoSideAdapter
import com.wgllss.ssmusic.features_ui.home.viewmodels.PinDaoViewModel

class KPinDaoFragment : TabTitleFragment<PinDaoViewModel>() {
    private lateinit var rvPlList: RecyclerView
    private lateinit var sideView: RecyclerView
    private lateinit var frameLayout: FrameLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val pinDaoSideAdapter by lazy { PinDaoSideAdapter() }
    private val pingDaoAdapter by lazy { PinDaoAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::frameLayout.isInitialized) {
            frameLayout = FrameLayout(inflater.context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            }
            val size = inflater.context.getIntToDip(5.0f).toInt()
            swipeRefreshLayout = SwipeRefreshLayout(inflater.context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                initColors()
            }
            sideView = RecyclerView(inflater.context).apply {
                layoutParams = FrameLayout.LayoutParams(14 * size, FrameLayout.LayoutParams.MATCH_PARENT)
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                val itemDecoration = View(context)
                itemDecoration.layoutParams = ViewGroup.LayoutParams(size / 5, size / 5)
                itemDecoration.setBackgroundColor(Color.parseColor("#10000000"))
                addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
            }

            rvPlList = RecyclerView(inflater.context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
                    gravity = Gravity.LEFT and Gravity.TOP
                }
                layoutManager = GridLayoutManager(requireContext(), 3)
                setHasFixedSize(true)
                val itemDecoration = View(context)
                val size = context.getIntToDip(5.0f).toInt()
                setPadding(14 * size, 0, 0, 0)
                itemDecoration.layoutParams = ViewGroup.LayoutParams(size / 5, size / 5)
                itemDecoration.setBackgroundColor(Color.parseColor("#30000000"))
                addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
            }
            swipeRefreshLayout.addView(rvPlList)
            frameLayout.addView(swipeRefreshLayout)
            frameLayout.addView(sideView)
        }
        return frameLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sideView.apply {
            adapter = pinDaoSideAdapter
            setHasFixedSize(true)
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    viewModel.clickItem(pinDaoSideAdapter.getItem(position).dataID)
                    pinDaoSideAdapter.setSelectPosition(position)
                }
            })
        }
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
            listSides.observe(viewLifecycleOwner) {
                pinDaoSideAdapter.notifyData(it)
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