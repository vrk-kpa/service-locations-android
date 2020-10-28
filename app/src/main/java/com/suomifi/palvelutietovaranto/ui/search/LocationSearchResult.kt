package com.suomifi.palvelutietovaranto.ui.search

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationSearchResult(
        val latitude: Double,
        val longitude: Double
) : Parcelable {

    @IgnoredOnParcel
    val latLng by lazy {
        LatLng(latitude, longitude)
    }

}
