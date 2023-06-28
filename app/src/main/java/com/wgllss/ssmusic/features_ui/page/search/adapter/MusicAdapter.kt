package com.wgllss.ssmusic.features_ui.page.search.adapter

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.core.adapter.BaseDataBindingAdapter
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.databinding.AdapterItemMusicBinding
import com.wgllss.ssmusic.R
import javax.inject.Inject
import kotlin.random.Random

class MusicAdapter @Inject constructor() : BaseDataBindingAdapter<MusicItemBean, AdapterItemMusicBinding>() {
    private var textColorHighlight: Int = 0

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

    override fun onBindItem(binding: AdapterItemMusicBinding, item: MusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
        binding?.apply {
            bean = item
            materialButtom.apply {
                setTextColor(getTextHightColorPrimary(context))
                background.setTint(context.getColor(array[Random.nextInt(array.size)]))
            }
        }
    }

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_item_music
}