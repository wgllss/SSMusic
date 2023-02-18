package com.wgllss.ssmusic.features_system.app
//
//import android.app.Application
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.wgllss.ssmusic.datasource.repository.AppRepository
//
//class AppViewModelFactory @Inject constructor(private val application: Application, private val appRepository: AppRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return when (modelClass) {
//            AppViewModel::class.java -> {
//                AppViewModel(application, appRepository)
//            }
//            else -> {
//                throw IllegalArgumentException("Unknown class $modelClass")
//            }
//        } as T
//    }
//}