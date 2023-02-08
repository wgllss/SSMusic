package com.wgllss.ssmusic.core.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView


abstract class BaseRecyclerAdapter<T>(val list: MutableList<T>) : RecyclerView.Adapter<BaseRecyclerAdapter.BaseBindingViewHolder>() {
    var context: Context? = null


    fun removeItem(position: Int) {
        if (list != null && list!!.size > position) {
            list!!.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun clearList() {
        if (list != null) {
            list!!.clear()
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int): T = list[position]

    @LayoutRes
    protected abstract fun getLayoutResId(viewType: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        val view = LayoutInflater.from(context).inflate(getLayoutResId(viewType), parent, false)
        return BaseBindingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder, position: Int) {
        val item = getItem(position)
        onBindItem(item, holder, position)
    }

    protected abstract fun onBindItem(item: T, holder: RecyclerView.ViewHolder, position: Int)

    class BaseBindingViewHolder internal constructor(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
}