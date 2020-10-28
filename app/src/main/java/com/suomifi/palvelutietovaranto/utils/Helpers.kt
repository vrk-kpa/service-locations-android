package com.suomifi.palvelutietovaranto.utils

import android.location.Location

inline fun ignoreExceptions(action: () -> Unit) {
    try {
        action()
    } catch (_: Throwable) {
    }
}

fun distanceBetween(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Float {
    val result = FloatArray(1) { 0F }
    Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, result)
    return result[0]
}
