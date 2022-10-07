package com.wgllss.ssmusic.core.asyninflater.factory

import android.content.Context
import android.view.View
import android.view.ViewGroup

interface AsyncInflaterLayout {

    fun asyncInflater(context: Context, parent: ViewGroup?, layoutID: Int, inflaterImpl: (view: View) -> Unit)
}