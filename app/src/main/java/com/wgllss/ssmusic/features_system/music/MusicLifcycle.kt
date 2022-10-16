package com.wgllss.ssmusic.features_system.music

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

open class MusicLifcycle : LifecycleOwner {
    private val mLifecycleRegistry by lazy { LifecycleRegistry(this) }

    override fun getLifecycle() = mLifecycleRegistry

    open fun onCreate() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    open fun onStart() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    open fun onResume() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    open fun onPause() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    open fun onStop() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    open fun onDestory() {
        mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}