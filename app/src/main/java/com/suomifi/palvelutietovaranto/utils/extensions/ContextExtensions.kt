@file:JvmName("Extensions")
@file:JvmMultifileClass

package com.suomifi.palvelutietovaranto.utils.extensions

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import com.suomifi.palvelutietovaranto.utils.Constants.Permissions.AR_PERMISSIONS
import com.suomifi.palvelutietovaranto.utils.Constants.Permissions.MAP_PERMISSIONS
import com.suomifi.palvelutietovaranto.utils.Constants.Permissions.REQUIRED_PERMISSIONS

fun Context.hasRequiredPermissions() = hasPermissions(REQUIRED_PERMISSIONS)
fun Context.hasMapPermissions() = hasPermissions(MAP_PERMISSIONS)
fun Context.hasArPermissions() = hasPermissions(AR_PERMISSIONS)
fun Context.hasPermissions(permissions: Array<String>) = permissions.all { permission ->
    ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
}

fun Context.selectedLanguage() = PreferenceManager.getDefaultSharedPreferences(this).selectedLanguage()
