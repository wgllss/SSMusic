package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.asyninflater.LaunchInflateKey
import com.wgllss.ssmusic.core.asyninflater.LayoutContains
import com.wgllss.ssmusic.core.ex.getIntToDip
import com.wgllss.ssmusic.core.ex.initColors
import com.wgllss.ssmusic.core.fragment.BaseViewModelFragment
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.widget.DividerGridItemDecoration
import com.wgllss.ssmusic.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.features_ui.page.home.adapter.MusicAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeTabViewModel
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment(val title: String, private val key: String) : BaseViewModelFragment<HomeViewModel>(0) {

    private val homeTabViewModel = viewModels<HomeTabViewModel>()
    private lateinit var rvPlList: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @Inject
    lateinit var musicAdapterL: Lazy<MusicAdapter>

    override fun activitySameViewModel() = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LogTimer.LogE(this, "$title onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogTimer.LogE(this, "$title onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogTimer.LogE(this, "$title onCreateView")
        if (!this::swipeRefreshLayout.isInitialized) {
            if ("index" == key) {
                swipeRefreshLayout = LayoutContains.getViewByKey(inflater.context, LaunchInflateKey.home_fragment)!! as SwipeRefreshLayout
                rvPlList = swipeRefreshLayout.findViewById(R.id.home_recycle_view)
            } else {
                swipeRefreshLayout = SwipeRefreshLayout(inflater.context).apply {
                    layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    initColors()
                }
                val res = inflater.context.resources
                rvPlList = RecyclerView(inflater.context).apply {
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    lp.gravity = Gravity.TOP and Gravity.LEFT
                    layoutParams = lp
                    setBackgroundColor(Color.WHITE)
                    layoutManager = LinearLayoutManager(context)
                    val paddingSize = res.getDimension(R.dimen.recycler_padding).toInt()
                    setPadding(paddingSize, 0, paddingSize, 0)
                    val itemDecoration = View(context)
                    val size = context.getIntToDip(1.0f).toInt()
                    itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                    itemDecoration.setBackgroundColor(Color.parseColor("#60000000"))
                    addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
                }
                swipeRefreshLayout.addView(rvPlList)
            }
        }
        return swipeRefreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "$title onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogTimer.LogE(this, "$title onActivityCreated")
        rvPlList.adapter = musicAdapterL.get()
        rvPlList?.run {
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    homeTabViewModel.value.getDetailFromSearch(key, position)
                }
            })
        }
        homeTabViewModel.value.initKey(key)
        homeTabViewModel.value.result[key]?.observe(viewLifecycleOwner) {
            WLog.e(this@HomeFragment, key)
            musicAdapterL.get().notifyData(it)
            swipeRefreshLayout.isRefreshing = false
        }
        homeTabViewModel.value.getData(key)
        swipeRefreshLayout.setOnRefreshListener {
            homeTabViewModel.value.getData(key)
        }
    }

    override fun initObserve() {
        super.initObserve()
        homeTabViewModel.value?.run {
            showUIDialog.observe(viewLifecycleOwner) { it ->
                swipeRefreshLayout.isRefreshing = it.isShow
//                if (it.isShow) showloading(it.msg) else hideLoading()
            }
            errorMsgLiveData.observe(viewLifecycleOwner) {
                onToast(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.currentMediaID.observe(viewLifecycleOwner) {
//            playListAdapterL.get().currentMediaID = it
//            playListAdapterL.get().notifyDataSetChanged()
//        }
    }
}