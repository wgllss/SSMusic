package com.wgllss.ssmusic.core.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel

abstract class BaseMVVMFragment<VM : BaseViewModel, DB : ViewDataBinding>(@LayoutRes private val contentLayoutId: Int) : BaseViewModelFragment<VM>(contentLayoutId) {

    lateinit var binding: DB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, contentLayoutId, container, false)
        return binding.root
    }
}