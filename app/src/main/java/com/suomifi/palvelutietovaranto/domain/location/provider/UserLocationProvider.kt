package com.suomifi.palvelutietovaranto.domain.location.provider

import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import io.reactivex.Flowable
import io.reactivex.Single

interface UserLocationProvider {
    /** Emits user location when subscribed to and every time that user location changes emitted on [Schedulers.io()]. */
    fun observeUserLocation(): Flowable<GeoLocation>

    /** @return Last known user location emitted on [Schedulers.io()]. */
    fun lastKnownUserLocation(): Single<GeoLocation>
}
