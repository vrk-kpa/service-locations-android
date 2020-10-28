package com.suomifi.palvelutietovaranto.ui.poi.map

import com.google.maps.android.clustering.ClusterItem
import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi

class PoiClusterItem(
        val poi: Poi
) : ClusterItem {
    var selected = false
    val isParkingMeter = poi.isParkingMeter
    override fun getSnippet() = ""
    override fun getTitle() = ""
    override fun getPosition() = poi.latLng
}
