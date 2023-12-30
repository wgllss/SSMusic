package com.wgllss.ssmusic.features_ui.page.detail.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.wgllss.core.activity.BaseViewModelActivity
import com.wgllss.core.ex.setFramgment
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.features_ui.page.detail.fragment.AlbumDetailFragment
import com.wgllss.ssmusic.features_ui.page.detail.fragment.SongRankFragment
import com.wgllss.ssmusic.features_ui.page.detail.fragment.SongSheetFragment
import com.wgllss.ssmusic.features_ui.page.detail.fragment.SongSingersFragment
import com.wgllss.ssmusic.features_ui.page.detail.viewmodel.SongSheetViewModel

class SongSheetDetailActivity : BaseViewModelActivity<SongSheetViewModel>() {

    companion object {
        private const val ENCODE_ID_KEY = "ENCODE_ID_KEY"
        private const val TYPE_KEY = "TYPE_KEY"
        private const val AUTHOR_NAME_KEY = "AUTHOR_NAME_KEY"

        fun startSongSheetDetailActivity(context: Context, encodeID: String, type: Int = 0, authorName: String = "") {
            context.startActivity(Intent(context, SongSheetDetailActivity::class.java).apply {
                putExtra(ENCODE_ID_KEY, encodeID)
                putExtra(TYPE_KEY, type)
                putExtra(AUTHOR_NAME_KEY, authorName)
            })
        }
    }

    override fun initControl(savedInstanceState: Bundle?) {
        super.initControl(savedInstanceState)
        setContentView(R.layout.activity_detail)
        intent?.run {
            getStringExtra(ENCODE_ID_KEY)?.let {
                setFramgment(
                    when (getIntExtra(TYPE_KEY, 0)) {
                        1 -> SongRankFragment(it)
                        2 -> SongSingersFragment(it, getStringExtra(AUTHOR_NAME_KEY) ?: "")
                        3 -> AlbumDetailFragment(it)
                        else -> SongSheetFragment(it)
                    }

//                    if (getIntExtra(TYPE_KEY, 0) == 0)
//                        SongSheetFragment(it)
//                    else SongRankFragment(it)
                    ,
                    R.id.container
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(Bundle())
    }
}