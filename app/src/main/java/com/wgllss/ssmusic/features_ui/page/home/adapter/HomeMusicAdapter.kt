package com.wgllss.ssmusic.features_ui.page.home.adapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.core.adapter.BaseRecyclerAdapter
import com.wgllss.ssmusic.data.MusicItemBean
import kotlin.random.Random

class HomeMusicAdapter : BaseRecyclerAdapter<MusicItemBean>() {


    private val array = arrayOf(
        R.color.color_random_0, R.color.color_random_1, R.color.color_random_2,
        R.color.color_random_3, R.color.color_random_4, R.color.color_random_5,
        R.color.color_random_6, R.color.color_random_7, R.color.color_random_8,
        R.color.color_random_9, R.color.color_random_10, R.color.color_random_11,
    )

    override fun getLayoutResId(viewType: Int) = com.wgllss.ssmusic.R.layout.adapter_home_item_music

    override fun onBindItem(context: Context, item: MusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.findViewById<MaterialButton>(com.wgllss.ssmusic.R.id.author).apply {
            text = item.author
            background.setTint(context.getColor(array[Random.nextInt(array.size)]))
        }
        holder.itemView.findViewById<MaterialTextView>(com.wgllss.ssmusic.R.id.mater_music_name).text = item.musicName
        holder.itemView.findViewById<MaterialTextView>(com.wgllss.ssmusic.R.id.sample_hz).text = item.author
    }
}