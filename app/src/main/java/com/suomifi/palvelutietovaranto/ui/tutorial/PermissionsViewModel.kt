package com.suomifi.palvelutietovaranto.ui.tutorial

import android.arch.lifecycle.ViewModel
import com.suomifi.palvelutietovaranto.ui.common.SingleLiveEvent

class PermissionsViewModel : ViewModel() {
    val requestPermissions = SingleLiveEvent<Void>()
    val permissionsGranted = SingleLiveEvent<Void>()
    val goToSettings = SingleLiveEvent<Void>()
}
