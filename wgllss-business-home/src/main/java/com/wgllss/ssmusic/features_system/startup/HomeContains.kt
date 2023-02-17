package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import android.content.MutableContextWrapper
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wgllss.core.units.WLog
//import com.wgllss.ssmusic.features_ui.home.fragment.HomeTabFragment
import java.util.concurrent.ConcurrentHashMap

object HomeContains {
    private val map = ConcurrentHashMap<String, View>()
    private val mapFragment = ConcurrentHashMap<String, Fragment>()

    fun getViewByKey(context: Context, key: String): View? {
        val view = map.remove(key)
        return if (view == null) {
            WLog.e(this, "getViewByKey key = $key is null")
            GenerateHomeLayout.getCreateViewByKey(context, key)
        } else {
            replaceContextForView(view, context)
            view?.takeIf {
                it.parent != null
            }?.let {
                (it.parent as ViewGroup).removeView(it)
            }
            view
        }
    }

    fun putViewByKey(key: String, view: View) {
        map[key] = view
    }

    fun putFragmentByKey(key: String, fragment: Fragment) {
        mapFragment[key] = fragment
    }

    fun getFragmentByKey(key: String) = mapFragment.remove(key) //?: HomeTabFragment()


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