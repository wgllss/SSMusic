package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.wgllss.annotations.FragmentDestination
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.fragment.BaseMVVMFragment
import com.wgllss.ssmusic.core.permissions.PermissionInterceptor
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.databinding.FragmentSettingBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@FragmentDestination(pageUrl = "fmt_setting", label = "设置", iconId = R.drawable.ic_notifications_black_24dp)
class SettingFragment @Inject constructor() : BaseMVVMFragment<HomeViewModel, FragmentSettingBinding>(R.layout.fragment_setting) {

    private val settingViewModelL = viewModels<SettingViewModel>()

    override fun activitySameViewModel() = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LogTimer.LogE(this, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogTimer.LogE(this, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogTimer.LogE(this, "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            settingViewModel = settingViewModelL.value
            lifecycleOwner = this@SettingFragment
            executePendingBindings()
        }
        settingViewModelL.value.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            materialSwitchSettion.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    settingViewModelL.value.isOpenLockerUI.value?.takeUnless {
                        it
                    }?.let {
                        XXPermissions.with(requireActivity())
                            .permission(Permission.SYSTEM_ALERT_WINDOW)
                            .interceptor(object : PermissionInterceptor() {
                                override fun deniedPermissions(activity: Activity, allPermissions: List<String>, deniedPermissions: List<String>, never: Boolean, callback: OnPermissionCallback?) {
                                    super.deniedPermissions(activity, allPermissions, deniedPermissions, never, callback)
                                    settingViewModelL.value.setLockerSwitch(false)
                                }
                            })
                            .request(OnPermissionCallback { permissions, all ->
                                if (!all) {
                                    settingViewModelL.value.setLockerSwitch(false)
                                    return@OnPermissionCallback
                                }
                                settingViewModelL.value.setLockerSwitch(true)
//                                CommonToast.show("获取 ${PermissionNameConvert.getPermissionString(requireActivity(), permissions)}成功")
                            })
                    }
                } else {
                    settingViewModelL.value.setLockerSwitch(false)
                }
            }
            materialNotificationSwitch.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    settingViewModelL.value.isNotificationOpen.value?.takeUnless {
                        it
                    }?.let {
                        setNotificationPermissions()
                    }
                }
            }
        }
    }

    private fun setNotificationPermissions() {
        XXPermissions.with(requireActivity())
            .permission(Permission.NOTIFICATION_SERVICE)
            .interceptor(object : PermissionInterceptor() {
                override fun deniedPermissions(activity: Activity, allPermissions: List<String>, deniedPermissions: List<String>, never: Boolean, callback: OnPermissionCallback?) {
                    super.deniedPermissions(activity, allPermissions, deniedPermissions, never, callback)
                    settingViewModelL.value.setNotificationOpen(false)
                }
            })
            .request(OnPermissionCallback { permissions, all ->
                if (!all) {
                    settingViewModelL.value.setNotificationOpen(false)
                    return@OnPermissionCallback
                }
                settingViewModelL.value.setNotificationOpen(true)
            })
    }

    override fun onStart() {
        LogTimer.LogE(this, "onStart")
        super.onStart()
    }

    override fun onResume() {
        LogTimer.LogE(this, "onResume")
        super.onResume()
        setNotificationPermissions()
    }

    override fun onStop() {
        LogTimer.LogE(this, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        LogTimer.LogE(this, "onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        LogTimer.LogE(this, "onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        WLog.e(this, "onDetach")
        super.onDetach()
    }
}