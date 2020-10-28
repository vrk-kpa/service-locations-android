package com.suomifi.palvelutietovaranto.domain.ptv.cache

import com.suomifi.palvelutietovaranto.domain.ptv.model.PoiDetails
import io.reactivex.Flowable

/**
 * Cache of the service objects.
 */
interface PtvCache {

    /** @param poiDetails POI to store in the cache. */
    fun storePoi(poiDetails: PoiDetails)

    /** @return All POIs in the cache. */
    fun getAll(): Flowable<PoiDetails>

}
