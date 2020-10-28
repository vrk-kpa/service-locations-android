package com.suomifi.palvelutietovaranto.domain.ptv.repository

import com.google.android.gms.maps.model.LatLngBounds
import com.suomifi.palvelutietovaranto.domain.localization.Language
import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi
import com.suomifi.palvelutietovaranto.domain.ptv.model.PoiDetails
import io.reactivex.Flowable

/**
 * Repository of services that handles data caching and offline data access.
 */
interface PtvRepository {

    /** @return POIs in the given area in chunks. */
    fun poisInArea(area: LatLngBounds, language: Language): Flowable<List<Poi>>

    /** @return Details of POIs in the supplied location. POIs details are read from cache. */
    fun poisDetailsInLocation(latitude: Double, longitude: Double): Flowable<PoiDetails>

}
