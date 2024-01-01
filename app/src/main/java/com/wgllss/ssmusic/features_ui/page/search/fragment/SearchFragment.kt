package com.wgllss.ssmusic.features_ui.page.search.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.core.ex.HideSoftInputFromWindow
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.fragment.BaseMVVMFragment
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.FragmentKsearchBinding
import com.wgllss.ssmusic.databinding.FragmentSearchBinding
import com.wgllss.ssmusic.features_ui.home.fragment.TabTitleFragment
import com.wgllss.ssmusic.features_ui.page.classics.adapter.HomeMusicAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel2
import com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class SearchFragment : TabTitleFragment<HomeViewModel2>() {

    private lateinit var binding: FragmentSearchBinding

    private val musicAdapter by lazy { HomeMusicAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::binding.isInitialized)
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            model = this@SearchFragment.viewModel
            adapter = musicAdapter
            lifecycleOwner = this@SearchFragment
            executePendingBindings()
            rvResult?.run {
                addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                    override fun onItemClickListener(itemRootView: View, position: Int) {
                        viewModel.getDetailFromSearch(position)
                    }
                })
                addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        if (viewModel.enableLoadMore() && linearLayoutManager!!.itemCount == linearLayoutManager.findLastVisibleItemPosition() + 1) {
                            viewModel.searchKeyByTitle()
                        }
                    }
                })
            }
        }
    }

    override fun initObserve() {
        viewModel?.run {
            showUIDialog.observe(viewLifecycleOwner) {
                if (it.isShow) {
                    if (pageNo == 1 || isClick) {
                        showloading(it.msg)
                    }
                } else hideLoading()
            }
            errorMsgLiveData.observe(viewLifecycleOwner) {
                onToast(it)
            }
            nowPlay.observe(viewLifecycleOwner) {
                it?.takeIf {
                    it
                }?.let {
                    activity?.run {
                        launchActivity(Intent(this, PlayActivity::class.java))
                    }
                }
            }
            result.observe(viewLifecycleOwner) {
                musicAdapter.notifyData(it)
                musicAdapter.addFooter()
            }
            enableLoadeMore.observe(viewLifecycleOwner) {
                if (!it) {
                    musicAdapter.removeFooter()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.nowPlay.postValue(false)
    }

    fun searchKey(content: String) {
        viewModel.searchKeyByTitle(content)
    }

    fun noSearch(): Boolean {
        return if (viewModel.isFirst) {
            viewModel.isFirst = false
            musicAdapter.itemCount == 0
        } else false
    }
}