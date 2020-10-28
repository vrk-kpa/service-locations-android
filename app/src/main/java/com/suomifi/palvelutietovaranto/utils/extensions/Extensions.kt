@file:JvmName("Extensions")
@file:JvmMultifileClass

package com.suomifi.palvelutietovaranto.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.support.annotation.DrawableRes
import android.view.animation.OvershootInterpolator
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.suomifi.palvelutietovaranto.BuildConfig
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import com.suomifi.palvelutietovaranto.utils.Constants.GeoLocation.DEFAULT_ACCURACY
import com.suomifi.palvelutietovaranto.utils.Constants.GeoLocation.DEFAULT_ALTITUDE
import com.suomifi.palvelutietovaranto.utils.Constants.GeoLocation.DEFAULT_MAP_ZOOM_LEVEL

fun LatLngBounds.toBbox(): String {
    return "${southwest.longitude},${southwest.latitude},${northeast.longitude},${northeast.latitude},EPSG:4326"
}

fun Location.toGeoLocation(): GeoLocation {
    val (latitude, longitude) = @Suppress("ConstantConditionIf")
    if (BuildConfig.FLAVOR == "fixedLocation") {
        arrayOf(60.175833, 24.933056)
    } else {
        arrayOf(latitude, longitude)
    }
    val accuracy = if (hasAccuracy()) accuracy else DEFAULT_ACCURACY
    val altitude = if (hasAltitude()) altitude else DEFAULT_ALTITUDE
    return GeoLocation(latitude, longitude, accuracy, altitude)
}

fun LocationManager.isAnyLocationProviderEnabled() = arrayOf(GPS_PROVIDER, NETWORK_PROVIDER).any { providerName ->
    isProviderEnabled(providerName)
}

fun GoogleMap.moveTo(latLng: LatLng) = moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM_LEVEL))

fun FloatingActionMenu.addIconAnimation(@DrawableRes menuClosedDrawable: Int, @DrawableRes menuOpenedDrawable: Int) {
    val set = AnimatorSet()

    val scaleOutX = ObjectAnimator.ofFloat(menuIconView, "scaleX", 1.0f, 0.2f)
    scaleOutX.duration = 50

    val scaleOutY = ObjectAnimator.ofFloat(menuIconView, "scaleY", 1.0f, 0.2f)
    scaleOutY.duration = 50

    val scaleInX = ObjectAnimator.ofFloat(menuIconView, "scaleX", 0.2f, 1.0f)
    scaleInX.duration = 150

    val scaleInY = ObjectAnimator.ofFloat(menuIconView, "scaleY", 0.2f, 1.0f)
    scaleInY.duration = 150

    scaleInX.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            menuIconView.setImageResource(if (isOpened) menuClosedDrawable else menuOpenedDrawable)
        }
    })

    set.play(scaleOutX).with(scaleOutY)
    set.play(scaleInX).with(scaleInY).after(scaleOutX)
    set.interpolator = OvershootInterpolator(2F)

    iconToggleAnimatorSet = set
}
