package com.wgllss.ssmusic.features_system.app

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class AppViewModelFactory @Inject constructor(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AppViewModel::class.java -> {
                AppViewModel(application)
            }
            else -> {
                throw IllegalArgumentException("Unknown class $modelClass")
            }
        } as T
    }
}