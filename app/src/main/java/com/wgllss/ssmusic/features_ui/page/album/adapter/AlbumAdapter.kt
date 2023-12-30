package com.wgllss.ssmusic.features_ui.page.album.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.datasource.netbean.album.AlbumBean

class AlbumAdapter : BaseRecyclerAdapter<AlbumBean>() {

    private val footer by lazy { AlbumBean("", "", "", "", "", viewType = 1) }

    fun addFooter() {
        mData.add(footer)
        notifyItemInserted(mData.size - 1)
    }

    fun removeFooter() {
        mData.removeAt(mData.size - 1)
        notifyItemRemoved(mData.size)
    }

    override fun getItemViewType(position: Int) = mData[position].viewType

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_album_item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        return if (viewType == 0) super.onCreateViewHolder(parent, viewType)
        else BaseBindingViewHolder(ProgressBar(context).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
                topMargin = context.getIntToDip(5f).toInt()
            }
        })
    }

    override fun onBindItem(context: Context, item: AlbumBean, holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            holder.itemView.run {
                (findViewById<ImageView>(R.id.album_icon)).loadUrl(item.albumImage)
                findViewById<MaterialTextView>(R.id.title).text = item.albumName
                findViewById<MaterialTextView>(R.id.txt_company).text = item.company
                findViewById<MaterialTextView>(R.id.txt_create_time).text = item.albumCreateTime
            }
        }
    }
}