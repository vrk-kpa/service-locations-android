package com.suomifi.palvelutietovaranto.domain.ptv.model

import com.google.android.gms.maps.model.LatLng
import com.suomifi.palvelutietovaranto.utils.Constants.Ar.ALTITUDE_UNKNOWN
import com.suomifi.palvelutietovaranto.utils.Constants.Ar.POI_TITLE_MAX_LENGTH
import org.json.JSONObject

data class Poi(
        val latitude: Double,
        val longitude: Double
) {
    val latLng by lazy {
        LatLng(latitude, longitude)
    }
    // TODO: this can be removed - POIs in AR can be removed comparing geo location, not ID
    val id = "$latitude,$longitude"
    var name: String = ""
        set(value) {
            field = if (value.length > POI_TITLE_MAX_LENGTH) {
                value.take(POI_TITLE_MAX_LENGTH - 3) + "..."
            } else value
        }
    var isParkingMeter = false

    fun toJsonObject() = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("latitude", latitude)
        put("longitude", longitude)
        put("altitude", ALTITUDE_UNKNOWN)
        put("alpha", 1.0)
        put("isParkingMeter", isParkingMeter)
    }

    fun idToJsonObject() = JSONObject().apply {
        put("id", id)
    }

}
