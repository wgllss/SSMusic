package com.wgllss.ssmusic.core.asyninflater.factory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

class AsyncInflaterLoader constructor(private val context: Context) {

    private lateinit var asyncInflaterLayout: AsyncInflaterLayout

    fun setAsyncInflaterLayout(asyncInflaterLayout: AsyncInflaterLayout) {
        this.asyncInflaterLayout = asyncInflaterLayout
    }

    fun setInflaterlayout(parent: ViewGroup?, @LayoutRes layoutID: Int, inflaterImpl: (view: View) -> Unit) {
        if (!this::asyncInflaterLayout.isInitialized) {
            asyncInflaterLayout.asyncInflater(context, parent, layoutID, inflaterImpl)
        }
    }
}