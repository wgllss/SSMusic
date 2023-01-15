package com.wgllss.ssmusic.core.asyninflater

import android.content.Context
import android.content.MutableContextWrapper
import android.view.View
import java.util.concurrent.ConcurrentHashMap

object LayoutContains {
    private val map = ConcurrentHashMap<String, View>()

    fun getViewByKey(context: Context, key: String): View? {
        val view = map[key]
        replaceContextForView(view, context)
        return view
    }

    fun putViewByKey(key: String, view: View) {
        map[key] = view
    }

    /**
     * 如果  inflater初始化时是传进来的application，inflate出来的 view 的 context 没法用来 startActivity，
     * 因此用 MutableContextWrapper 进行包装，后续进行替换
     */
    private fun replaceContextForView(inflatedView: View?, context: Context?) {
        if (inflatedView == null || context == null) {
            return
        }
        val cxt = inflatedView.context
        if (cxt is MutableContextWrapper) {
            cxt.baseContext = context
        }
    }
}