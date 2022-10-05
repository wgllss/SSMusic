package com.scclzkj.base_core.binding_adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.ssmusic.core.ex.getIntToDip
import com.wgllss.ssmusic.core.widget.DividerGridItemDecoration

object RecycleBindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["itemColorStr", "itemWidth"], requireAll = false)
    fun setRecycleInfo(recyclerView: RecyclerView, itemColorStr: String?, itemWidth: Float) {
        try {
            recyclerView?.apply {
                setHasFixedSize(true)
                val mContext = context
                val itemDecoration = View(mContext)
                val size = mContext.getIntToDip(itemWidth).toInt()
                itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                if (itemColorStr == null)
                    itemDecoration.setBackgroundColor(Color.TRANSPARENT)
                else
                    itemDecoration.setBackgroundColor(Color.parseColor(itemColorStr))
                addItemDecoration(DividerGridItemDecoration(mContext, GridLayoutManager.VERTICAL, itemDecoration))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(recyclerView.context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}