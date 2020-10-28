package com.suomifi.palvelutietovaranto.domain.interactors.ar

import com.suomifi.palvelutietovaranto.domain.interactors.base.FlowableUseCase
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import com.suomifi.palvelutietovaranto.domain.location.provider.UserLocationProvider
import com.suomifi.palvelutietovaranto.domain.preferences.PreferenceChangeNotifier
import com.suomifi.palvelutietovaranto.utils.Constants.GeoLocation.SMALLEST_DISPLACEMENT
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction

class ObservePoisChangesUseCase(
        private val preferenceChangeNotifier: PreferenceChangeNotifier,
        private val userLocationProvider: UserLocationProvider
) : FlowableUseCase<Unit, Unit>() {

    override fun execute(params: Unit): Flowable<Unit> {
        return Flowable.combineLatest(
                preferenceChangeNotifier.observePoiRadiusChange(),
                userLocationProvider.observeUserLocation(),
                BiFunction { poiRadius: Int, geoLocation: GeoLocation ->
                    ReloadPoisInput(poiRadius, geoLocation)
                })
                .scan { previous: ReloadPoisInput, current: ReloadPoisInput ->
                    if (current.shouldReloadPois(previous)) {
                        current
                    } else {
                        previous
                    }
                }
                .distinctUntilChanged()
                .map {
                    Unit
                }
    }

    private data class ReloadPoisInput(
            val poiRadius: Int,
            val userLocation: GeoLocation
    ) {
        fun shouldReloadPois(previousInput: ReloadPoisInput): Boolean {
            if (poiRadius != previousInput.poiRadius) return true
            if (userLocation.distanceBetween(previousInput.userLocation) > SMALLEST_DISPLACEMENT) return true
            return false
        }
    }

}
