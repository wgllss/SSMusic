package com.wgllss.ssmusic.features_ui.page.album.fragment

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
import com.wgllss.core.units.LogTimer
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.ex.initColors
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.page.album.adapter.AlbumAdapter
import com.wgllss.ssmusic.features_ui.page.album.viewmodels.AlbumViewModel

class KAlbumFragment : TabTitleFragment<AlbumViewModel>() {
    private lateinit var rvPlList: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val albumAdapter by lazy { AlbumAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::swipeRefreshLayout.isInitialized) {
            swipeRefreshLayout = SwipeRefreshLayout(inflater.context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                initColors()
            }
            rvPlList = RecyclerView(inflater.context).apply {
                val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                lp.gravity = Gravity.TOP and Gravity.LEFT
                layoutParams = lp
                layoutManager = LinearLayoutManager(context)
//                    val paddingSize = res.getDimension(R.dimen.recycler_padding).toInt()
                setHasFixedSize(true)
//                    setPadding(paddingSize, 0, paddingSize, 0)
                val itemDecoration = View(context)
                val size = context.getIntToDip(1.0f).toInt()
                itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                itemDecoration.setBackgroundColor(Color.parseColor("#60000000"))
                addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
            }
            swipeRefreshLayout.addView(rvPlList)
        }
        return swipeRefreshLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogTimer.LogE(this, "$title onActivityCreated")
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.reset(key)
            viewModel.getData(key)
        }
        rvPlList?.apply {
            rvPlList.adapter = albumAdapter
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
//                    homeTabViewModel.getDetailFromSearch(albumAdapter.getItem(position))
                }
            })
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (viewModel.enableLoadMore(key) && linearLayoutManager!!.itemCount == linearLayoutManager.findLastVisibleItemPosition() + 1) {
                        viewModel.getData(key)
                    }
                }
            })
        }
    }

    override fun lazyLoad() {
        viewModel.getData(key)
    }

    override fun initObserve() {
        viewModel.run {
            initKey(key)
            result[key]?.observe(viewLifecycleOwner) {
                albumAdapter.notifyData(it)
                albumAdapter.addFooter()
            }
            enableLoadeMore[key]?.observe(viewLifecycleOwner) {
                if (!it)
                    albumAdapter.removeFooter()
            }
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