package com.wgllss.ssmusic.features_ui.home.fragment

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
import com.wgllss.core.fragment.BaseViewModelClassFragment
import com.wgllss.core.fragment.BaseViewModelFragment
import com.wgllss.core.units.LogTimer
import com.wgllss.core.units.WLog
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.ex.initColors
import com.wgllss.ssmusic.features_system.startup.HomeContains
import com.wgllss.ssmusic.features_system.startup.LaunchInflateKey
import com.wgllss.ssmusic.features_ui.home.adapter.HomeMusicAdapter
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeTabViewModel
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel

class HomeFragment : BaseViewModelClassFragment<HomeViewModel>(0) {

    var title: String = ""
    private var key: String = ""

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


    private val homeTabViewModel = viewModels<HomeTabViewModel>()
    private lateinit var rvPlList: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var musicAdapter: HomeMusicAdapter

    override fun activitySameViewModel() = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LogTimer.LogE(this, "$title onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString(TITLE_KEY, "") ?: ""
        key = arguments?.getString(KEY, "") ?: ""
        LogTimer.LogE(this, "$title onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogTimer.LogE(this, "$title onCreateView")
        if (!this::swipeRefreshLayout.isInitialized) {
            if ("index" == key) {
                swipeRefreshLayout = HomeContains.getViewByKey(inflater.context, LaunchInflateKey.home_fragment)!! as SwipeRefreshLayout
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
            homeTabViewModel.value.getData(key)
        }
        rvPlList?.run {
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    homeTabViewModel.value.getDetailFromSearch(musicAdapter.getItem(position))
                }
            })
        }
        homeTabViewModel.value.initKey(key)
        homeTabViewModel.value.result[key]?.observe(viewLifecycleOwner) {
            WLog.e(this@HomeFragment, key)
            musicAdapter.notifyData(it)
        }
        if (rvPlList.adapter == null) {
            musicAdapter = HomeMusicAdapter()
            rvPlList.adapter = musicAdapter
        } else {
            WLog.e(this, " json json 11")
            musicAdapter = rvPlList.adapter as HomeMusicAdapter
        }
        homeTabViewModel.value.getData(key)
    }

    override fun initObserve() {
        super.initObserve()
        homeTabViewModel.value?.run {
            showUIDialog.observe(viewLifecycleOwner) { it ->
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