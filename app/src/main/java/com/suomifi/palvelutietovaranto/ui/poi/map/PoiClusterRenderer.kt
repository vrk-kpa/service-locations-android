package com.suomifi.palvelutietovaranto.ui.poi.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.suomifi.palvelutietovaranto.R

class PoiClusterRenderer(context: Context,
                         googleMap: GoogleMap,
                         private val clusterManager: ClusterManager<PoiClusterItem>,
                         private val parkingMetersVisibilitySupplier: () -> Boolean
) : DefaultClusterRenderer<PoiClusterItem>(context, googleMap, clusterManager) {

    private val markerIcon by lazy {
        val size = context.resources.getDimensionPixelSize(R.dimen.marker_size)
        BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                BitmapFactory.decodeStream(context.assets.open("image-recognition/assets/map-marker.png")),
                size,
                size,
                true
        ))
    }

    private val selectedMarkerIcon by lazy {
        val size = context.resources.getDimensionPixelSize(R.dimen.marker_size)
        BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                BitmapFactory.decodeStream(context.assets.open("image-recognition/assets/map-marker-selected.png")),
                size,
                size,
                true
        ))
    }

    override fun onBeforeClusterItemRendered(item: PoiClusterItem, markerOptions: MarkerOptions) {
        if (item.selected) {
            markerOptions.icon(selectedMarkerIcon)
        } else {
            markerOptions.icon(markerIcon)
        }
    }

    override fun onClusterItemRendered(clusterItem: PoiClusterItem, marker: Marker) {
        if (clusterItem.isParkingMeter) {
            marker.isVisible = parkingMetersVisibilitySupplier.invoke()
        }
    }

    fun updateMarker(poiClusterItem: PoiClusterItem) {
        getMarker(poiClusterItem)?.setIcon(if (poiClusterItem.selected) selectedMarkerIcon else markerIcon)
    }

    fun toggleParkingMetersVisibility() {
        clusterManager.algorithm.items.filter { item ->
            item.isParkingMeter
        }.mapNotNull { item ->
            getMarker(item)
        }.forEach { marker ->
            marker.isVisible = parkingMetersVisibilitySupplier.invoke()
        }
    }

}
