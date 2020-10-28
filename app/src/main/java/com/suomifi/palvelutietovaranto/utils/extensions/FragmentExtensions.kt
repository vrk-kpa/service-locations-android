@file:JvmName("Extensions")
@file:JvmMultifileClass

package com.suomifi.palvelutietovaranto.utils.extensions

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.Fragment
import com.suomifi.palvelutietovaranto.ui.tutorial.GoToSettingsDialogFragment
import com.suomifi.palvelutietovaranto.utils.Constants.RequestCodes.PERMISSIONS_REQUEST_CODE

fun Fragment.requestPermissions(sharedPreferences: SharedPreferences, permissions: Array<String>) {
    activity?.let { activity ->
        val showRationale = activity.shouldShowRequestPermissionRationale(permissions)
        if (!showRationale && sharedPreferences.didAskForPermissions()) {
            GoToSettingsDialogFragment.newInstance().show(fragmentManager, GoToSettingsDialogFragment.TAG)
        } else {
            sharedPreferences.setAskedForPermissions()
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
        }
    }
}

fun Fragment.startSystemSettings() {
    activity?.let { activity ->
        startActivityForResult(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", activity.packageName, null)
        }, PERMISSIONS_REQUEST_CODE)
    }
}
