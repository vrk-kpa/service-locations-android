package com.suomifi.palvelutietovaranto.ui.poi.common

import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi
import com.suomifi.palvelutietovaranto.domain.ptv.model.PoiDetails

sealed class PoiAction(val poi: Poi)
class PoiDeselected(poi: Poi) : PoiAction(poi)
class PoiSelected(poi: Poi, val poiDetails: PoiDetails) : PoiAction(poi)
class PoisSelected(poi: Poi, val poisDetails: List<PoiDetails>) : PoiAction(poi)
