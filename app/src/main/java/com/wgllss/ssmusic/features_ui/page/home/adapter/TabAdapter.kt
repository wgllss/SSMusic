package com.wgllss.ssmusic.features_ui.page.home.adapter

import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.adapter.BaseRecyclerAdapter
import com.wgllss.ssmusic.core.ex.getIntToDip

class TabAdapter(list: MutableList<String>) : BaseRecyclerAdapter<String>(list) {
    override fun getLayoutResId(viewType: Int) = R.layout.adapter_item_tab

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        val textView = TextView(parent.context).apply {
            val lp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, parent.context.getIntToDip(50f).toInt())
            layoutParams = lp
            setBackgroundColor(Color.BLUE)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
            gravity = Gravity.CENTER
        }
        return BaseBindingViewHolder(textView)
    }

    override fun onBindItem(item: String, holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as TextView).text = item
    }

}