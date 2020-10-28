package com.suomifi.palvelutietovaranto.domain.interactors.ar

import com.suomifi.palvelutietovaranto.domain.interactors.base.FlowableUseCase
import com.suomifi.palvelutietovaranto.domain.location.provider.UserLocationProvider
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import io.reactivex.Flowable

class ObserveUserLocationUseCase(
        private val userLocationProvider: UserLocationProvider
) : FlowableUseCase<Unit, GeoLocation>() {

    override fun execute(params: Unit): Flowable<GeoLocation> {
        return userLocationProvider.observeUserLocation()
    }

}
