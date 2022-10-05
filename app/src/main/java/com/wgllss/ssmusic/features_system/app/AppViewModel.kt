package com.wgllss.ssmusic.features_system.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val installHomeJson by lazy { MutableLiveData<Boolean>() }

}