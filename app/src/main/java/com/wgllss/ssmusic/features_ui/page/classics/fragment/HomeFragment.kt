package com.wgllss.ssmusic.features_ui.page.classics.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.fragment.BaseViewModelFragment
import com.wgllss.core.units.LogTimer
import com.wgllss.core.units.WLog
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.ex.initColors
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.page.classics.adapter.HomeMusicAdapter
import com.wgllss.ssmusic.features_ui.page.classics.viewmodels.HomeTabViewModel
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel2

class HomeFragment : TabTitleFragment<HomeViewModel2>() {

    companion object {
        private const val TITLE_KEY = "TITLE_KEY"
        private const val KEY = "KEY"

        fun newInstance(titleS: String, keyS: String): HomeFragment {
            val fragment = HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, titleS)
                    putString(KEY, keyS)
                }
                title = titleS
            }
            return fragment
        }
    }


    private val homeTabViewModel by lazy { viewModels<HomeTabViewModel>().value }
    private lateinit var rvPlList: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var musicAdapter: HomeMusicAdapter

    override fun activitySameViewModel() = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LogTimer.LogE(this, "$title onAttach")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogTimer.LogE(this, "$title onCreateView")
        if (!this::swipeRefreshLayout.isInitialized) {
//            if ("index" == key) {
//                swipeRefreshLayout = HomeContains.getViewByKey(inflater.context, LaunchInflateKey.home_fragment)!! as SwipeRefreshLayout
//                rvPlList = swipeRefreshLayout.findViewById(R.id.home_recycle_view)
//            } else {
            swipeRefreshLayout = SwipeRefreshLayout(inflater.context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                initColors()
            }
            val res = inflater.context.resources
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
//            }
        }
        return swipeRefreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "$title onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogTimer.LogE(this, "$title onActivityCreated")
        swipeRefreshLayout.setOnRefreshListener {
            homeTabViewModel.reset(key)
            homeTabViewModel.getData(key)
        }
        rvPlList?.apply {
            musicAdapter = HomeMusicAdapter()
            rvPlList.adapter = musicAdapter
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    homeTabViewModel.getDetailFromSearch(musicAdapter.getItem(position))
                }
            })
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (homeTabViewModel.enableLoadMore(key) && linearLayoutManager!!.itemCount == linearLayoutManager.findLastVisibleItemPosition() + 1) {
                        homeTabViewModel.getData(key)
                    }
                }
            })
        }
        musicAdapter?.itemCount?.takeIf {
            it > 0
        }?.let {
            homeTabViewModel.isLoadOffine = true
        }
    }

    override fun lazyLoad() {
        homeTabViewModel.getData(key)
    }

    override fun initObserve() {
        super.initObserve()
        homeTabViewModel.run {
            initKey(key)
            result[key]?.observe(viewLifecycleOwner) {
                WLog.e(this@HomeFragment, key)
                musicAdapter.notifyData(it)
                musicAdapter.addFooter()
            }
            enableLoadeMore[key]?.observe(viewLifecycleOwner) {
                if (!it)
                    musicAdapter.removeFooter()
            }
            showUIDialog.observe(viewLifecycleOwner) {
                if (!isLoadOffine)
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