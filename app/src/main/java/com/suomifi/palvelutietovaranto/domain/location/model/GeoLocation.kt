package com.suomifi.palvelutietovaranto.domain.location.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.suomifi.palvelutietovaranto.utils.Constants.GeoLocation.DEFAULT_ACCURACY
import com.suomifi.palvelutietovaranto.utils.Constants.GeoLocation.DEFAULT_ALTITUDE
import com.suomifi.palvelutietovaranto.utils.distanceBetween
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeoLocation(
        val latitude: Double,
        val longitude: Double,
        val accuracy: Float = DEFAULT_ACCURACY,
        val altitude: Double = DEFAULT_ALTITUDE
) : Parcelable {
    fun toLatLng() = LatLng(latitude, longitude)
    fun distanceBetween(geoLocation: GeoLocation) = distanceBetween(latitude, longitude, geoLocation.latitude, geoLocation.longitude)
}
