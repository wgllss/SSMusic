package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.wgllss.ssmusic.core.fragment.BaseMVVMFragment
import com.wgllss.annotations.FragmentDestination
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.databinding.FragmentSettingBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@FragmentDestination(pageUrl = "fmt_setting", label = "设置", iconId = R.drawable.ic_notifications_black_24dp)
class SettingFragment : BaseMVVMFragment<HomeViewModel, FragmentSettingBinding>(R.layout.fragment_setting) {

    private val overLayPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(requireActivity())) {
                logE("已经拥有在其他应用上层权限")
                binding.materialSwitch.isChecked = true
                //已经拥有在其他应用上层权限
            } else {
                binding.materialSwitch.isChecked = false
            }
        }
    }

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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            materialSwitch.setOnCheckedChangeListener { v, checked ->
                if (checked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.canDrawOverlays(requireActivity())) {
                            logE("已经拥有在其他应用上层权限")
                            materialSwitch.isChecked = true
                        } else {
                            materialSwitch.isChecked = false
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                            intent.data = Uri.parse("package:${requireActivity().packageName}")
                            overLayPermission.launch(intent)
                        }
                    }
                }
            }
            materialLockerSetting.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(requireActivity())) {
                        logE("已经拥有在其他应用上层权限")
                        materialSwitch.isChecked = true
                        //已经拥有在其他应用上层权限
                    } else {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.data = Uri.parse("package:${requireActivity().packageName}")
                        overLayPermission.launch(intent)
                    }
                }
            }
        }
    }

    override fun onStart() {
        LogTimer.LogE(this, "onStart")
        super.onStart()
    }

    override fun onResume() {
        LogTimer.LogE(this, "onResume")
        super.onResume()
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