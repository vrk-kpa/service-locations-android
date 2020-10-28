package com.suomifi.palvelutietovaranto.domain.location.provider

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationRequest
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import com.suomifi.palvelutietovaranto.utils.Constants.GeoLocation.LOCATION_UPDATE_INTERVAL
import com.suomifi.palvelutietovaranto.utils.extensions.toGeoLocation
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

class UserLocationProviderImpl(
        private val reactiveLocationProvider: ReactiveLocationProvider
) : UserLocationProvider {

    override fun observeUserLocation(): Flowable<GeoLocation> {
        return userLocation(LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_UPDATE_INTERVAL))
                .observeOn(Schedulers.io())
    }

    @SuppressLint("MissingPermission")
    private fun userLocation(locationRequest: LocationRequest): Flowable<GeoLocation> {
        return Flowable.merge(lastKnownUserLocation().toFlowable(),
                reactiveLocationProvider.getUpdatedLocation(locationRequest)
                        .map { location ->
                            location.toGeoLocation()
                        }
                        .toFlowable(BackpressureStrategy.MISSING))
                .distinctUntilChanged()
    }

    @SuppressLint("MissingPermission")
    override fun lastKnownUserLocation(): Single<GeoLocation> {
        return reactiveLocationProvider
                .lastKnownLocation
                .firstOrError()
                .map { location ->
                    location.toGeoLocation()
                }
                .observeOn(Schedulers.io())
    }

}
