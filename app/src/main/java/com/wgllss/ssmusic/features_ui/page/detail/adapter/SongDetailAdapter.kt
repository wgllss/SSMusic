package com.wgllss.ssmusic.features_ui.page.detail.adapter

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.netbean.singer.KSingerSongBean
import kotlin.random.Random

class SongDetailAdapter : BaseRecyclerAdapter<KSingerSongBean>() {
    private var cornerRadiusInt: Int = 0
    private var textColorHighlight: Int = 0

    private val footer by lazy { KSingerSongBean("", "", "", "", viewType = 1) }

    fun addFooter() {
        mData.add(footer)
        notifyItemInserted(mData.size - 1)
    }

    fun removeFooter() {
        mData.removeAt(mData.size - 1)
        notifyItemRemoved(mData.size)
    }

    override fun getItemViewType(position: Int) = mData[position].viewType

    private fun getTextHightColorPrimary(context: Context): Int {
        if (textColorHighlight == 0) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.textColorHighlight, typedValue, true)
            textColorHighlight = typedValue.data
        }
        return textColorHighlight
    }

    private val array = arrayOf(
        com.wgllss.music.skin.R.color.color_random_0, com.wgllss.music.skin.R.color.color_random_1, com.wgllss.music.skin.R.color.color_random_2,
        com.wgllss.music.skin.R.color.color_random_3, com.wgllss.music.skin.R.color.color_random_4, com.wgllss.music.skin.R.color.color_random_5,
        com.wgllss.music.skin.R.color.color_random_6, com.wgllss.music.skin.R.color.color_random_7, com.wgllss.music.skin.R.color.color_random_8,
        com.wgllss.music.skin.R.color.color_random_9, com.wgllss.music.skin.R.color.color_random_10, com.wgllss.music.skin.R.color.color_random_11,
    )

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_item_song_sheet

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        return if (viewType == 0) {
            val holder = super.onCreateViewHolder(parent, viewType)
            if (cornerRadiusInt == 0) {
                cornerRadiusInt = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 999f, parent.context.resources.displayMetrics).toInt()
            }
            holder
        } else {
            BaseBindingViewHolder(ProgressBar(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = context.getIntToDip(5f).toInt()
                }
            })
        }
    }

    override fun onBindItem(context: Context, item: KSingerSongBean, holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            holder?.itemView?.run {
                findViewById<MaterialButton>(R.id.music_no).apply {
                    setTextColor(getTextHightColorPrimary(context))
                    cornerRadius = cornerRadiusInt
                    gravity = Gravity.CENTER
                    isClickable = false
                    isFocusable = false
                    background.setTint(context.getColor(array[Random.nextInt(array.size)]))
                    text = "${position + 1}"
                }
                findViewById<TextView>(R.id.author).text = item.auhorName
                findViewById<TextView>(R.id.title).text = item.audio_name
            }
        }
    }
}