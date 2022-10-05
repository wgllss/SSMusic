package com.wgllss.ssmusic.dl

import android.content.Context
import com.wgllss.ssmusic.features_system.app.AppViewModel
import dagger.Lazy
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@EntryPoint
interface InitializerEntryPoint {

    fun injectAppViewModel(): Lazy<AppViewModel>

    companion object {
        fun resolve(context: Context): InitializerEntryPoint {
            val appContext = context.applicationContext ?: throw IllegalStateException()
            return EntryPointAccessors.fromApplication(appContext, InitializerEntryPoint::class.java)
        }
    }
}