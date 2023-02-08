package com.wgllss.ssmusic.features_ui.page.home.adapter

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.adapter.BaseRecyclerAdapter

class TabAdapter(list: MutableList<String>) : BaseRecyclerAdapter<String>(list) {
    override fun getLayoutResId(viewType: Int) = R.layout.adapter_item_tab
    override fun onBindItem(item: String, holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as TextView).text = item
    }

}