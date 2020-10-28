@file:JvmName("Extensions")
@file:JvmMultifileClass

package com.suomifi.palvelutietovaranto.utils.extensions

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.suomifi.palvelutietovaranto.R

fun Activity.isPortrait() = resources.configuration.orientation == ORIENTATION_PORTRAIT

fun Activity.shouldShowRequestPermissionRationale(permissions: Array<String>) = permissions.any { permission ->
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}

fun Activity.safeStartActivity(intent: Intent) {
    if (intent.resolveActivity(packageManager) == null) {
        Toast.makeText(this, R.string.error_no_app_found, Toast.LENGTH_LONG).show()
    } else {
        startActivity(intent)
    }
}
