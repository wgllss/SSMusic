package com.wgllss.ssmusic.features_ui.home.viewmodels

import androidx.lifecycle.MutableLiveData
import com.wgllss.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp

class SettingViewModel : BaseViewModel() {

    private val isOpenLockerUI by lazy { MutableLiveData<Boolean>() }

    private val isNotificationOpen by lazy { MutableLiveData<Boolean>() }

    override fun start() {
        isOpenLockerUI.postValue(MMKVHelp.isOpenLockerUI())
    }

    fun setLockerSwitch(isOpen: Boolean) {
        isOpenLockerUI.postValue(isOpen)
        MMKVHelp.setLockerSwitch(isOpen)
    }

    fun setNotificationOpen(isOpen: Boolean) {
        isNotificationOpen.postValue(isOpen)
    }

}