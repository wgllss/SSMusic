package com.wgllss.ssmusic.core.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BasePagedListAdapter<T, VB : ViewDataBinding>(@NonNull diffCallback: DiffUtil.ItemCallback<T>) :
    PagedListAdapter<T, BasePagedListAdapter.BaseBindingViewHolder>(diffCallback) {

    @LayoutRes
    protected abstract fun getLayoutResId(viewType: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        val binding = DataBindingUtil.inflate<VB>(LayoutInflater.from(parent.context), getLayoutResId(viewType), parent, false)
        return BaseBindingViewHolder(binding?.root)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<VB>(holder.itemView)
        val item = getItem(position)
        onBindItem(binding!!, item, holder, position)
        binding?.apply {
            executePendingBindings()
        }
    }

    protected abstract fun onBindItem(binding: VB, item: T?, holder: RecyclerView.ViewHolder, position: Int)


    class BaseBindingViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    }

    public override  fun getItem(position: Int): T? {
        return super.getItem(position)
    }

}