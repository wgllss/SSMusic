package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.logE
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.features_ui.page.mv.activity.KMVActivity
import com.wgllss.ssmusic.features_ui.page.home.adapter.MVListAdapter
import com.wgllss.ssmusic.ex.initColors
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.KMVListViewModel

class KMVListFragment : TabTitleFragment<KMVListViewModel>() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private val mvListAdapter by lazy { MVListAdapter() }

    companion object {
        private const val TITLE_KEY = "TITLE_KEY"
        private const val KEY = "KEY"

        fun newInstance(titleS: String, keyS: String): KMVListFragment {
            val fragment = KMVListFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, titleS)
                    putString(KEY, keyS)
                }
                title = titleS
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::swipeRefreshLayout.isInitialized) {
            swipeRefreshLayout = SwipeRefreshLayout(inflater.context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                initColors()
            }
            recyclerView = RecyclerView(inflater.context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                val itemDecoration = View(context)
                val size = context.getIntToDip(10.0f).toInt()
                itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                itemDecoration.setBackgroundColor(Color.parseColor("#30000000"))
                addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
                addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                    override fun onItemClickListener(itemRootView: View, position: Int) {
                        viewModel.getMvData(mvListAdapter.getItem(position))
                    }
                })
            }
            swipeRefreshLayout.addView(recyclerView)
        }
        return swipeRefreshLayout
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.kmvList(key)
        }
        recyclerView?.apply {
            adapter = mvListAdapter
            layoutManager = GridLayoutManager(context, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(i: Int): Int {
                        //spanCount 当横向时，2代表每列2行，
                        return when (mvListAdapter.getItemViewType(i)) {
                            0 -> 1 //   spanCount/1  个位置占满一格
                            else -> 2 // spanCount/2  个位置占满一格
                        }
                    }
                }
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (viewModel.enableLoadMore() && linearLayoutManager!!.itemCount == linearLayoutManager.findLastVisibleItemPosition() + 1) {
                        viewModel.kmvList(key)
                    }
                }
            })
        }
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.kmvList(key)
        }
    }

    override fun lazyLoad() {
        viewModel?.kmvList(key)
    }

    override fun initObserve() {
        super.initObserve()
        viewModel.run {
            initKey(key)
            result[key]?.observe(viewLifecycleOwner) {
                mvListAdapter.notifyData(it)
                if (key == "9" || key == "13") {
                    if (enableLoadeMore)
                        mvListAdapter.addFooter()
                }
            }
            liveDataMV.observe(viewLifecycleOwner) {
                activity?.run {
                    KMVActivity.startKMVActivity(this, it.url, it.title)
                }
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