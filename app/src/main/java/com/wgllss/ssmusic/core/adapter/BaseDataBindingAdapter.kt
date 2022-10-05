package com.scclzkj.base_core.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseDataBindingAdapter<T, VB : ViewDataBinding>(var context: Context? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mData: MutableList<T>
    var selectPositon = 0

    fun notifyData(mData: MutableList<T>) {
        if (mData == null) {
            this.mData = mutableListOf()
        } else {
            this.mData = mData
        }
        notifyDataSetChanged()
    }

    fun addMoreList(mData: MutableList<T>) {
        mData?.takeIf { it.isNotEmpty() && it.size > 0 }
            ?.let {
                it.addAll(mData)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        val binding = DataBindingUtil.inflate<VB>(LayoutInflater.from(context), getLayoutResId(viewType), parent, false)
        return BaseBindingViewHolder(binding?.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<VB>(holder.itemView)
        val item = getItem(position)
        onBindItem(binding!!, item, holder, position)
        binding.executePendingBindings()
    }

    protected fun baseBindingViewHolder(view: View): BaseBindingViewHolder {
        return BaseBindingViewHolder(view)
    }

    fun getItem(position: Int): T = mData[position]


    protected abstract fun onBindItem(binding: VB, item: T, holder: RecyclerView.ViewHolder, position: Int)

    override fun getItemCount(): Int = if (!this::mData.isInitialized) 0 else mData.size

    protected fun getDataSize() = if (!this::mData.isInitialized) 0 else mData.size

    @LayoutRes
    protected abstract fun getLayoutResId(viewType: Int): Int

    class BaseBindingViewHolder internal constructor(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
}