package com.suomifi.palvelutietovaranto.domain.interactors.ar

import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import com.suomifi.palvelutietovaranto.domain.SelectedPoiSupplier
import com.suomifi.palvelutietovaranto.domain.interactors.base.FlowableUseCase
import com.suomifi.palvelutietovaranto.domain.location.provider.UserLocationProvider
import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi
import com.suomifi.palvelutietovaranto.domain.ptv.repository.PtvRepository
import com.suomifi.palvelutietovaranto.utils.Constants.Preferences.PREF_KEY_POI_RADIUS
import com.suomifi.palvelutietovaranto.utils.distanceBetween
import com.suomifi.palvelutietovaranto.utils.extensions.selectedLanguage
import io.reactivex.Flowable

class LoadPoisUseCase(
        private val ptvRepository: PtvRepository,
        private val sharedPreferences: SharedPreferences,
        private val userLocationProvider: UserLocationProvider,
        private val selectedPoiSupplier: SelectedPoiSupplier
) : FlowableUseCase<Unit, Poi>() {

    override fun execute(params: Unit): Flowable<Poi> {
        return userLocationProvider.lastKnownUserLocation()
                .map { userLocation ->
                    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    (LoadPoisInput(userLocation.latitude, userLocation.longitude,
                            sharedPreferences.getString(PREF_KEY_POI_RADIUS, "").toInt()))
                }
                .flatMapPublisher { input ->
                    val poisInArea = ptvRepository.poisInArea(input.toLatLngBounds(), sharedPreferences.selectedLanguage())
                            .flatMap { pois ->
                                Flowable.just(pois.filter { poi ->
                                    input.isInTheRadius(poi)
                                })
                            }
                    selectedPoiSupplier.getSelectedPoi()?.let { selectedPoi ->
                        Flowable.merge(Flowable.just(listOf(selectedPoi)), poisInArea)
                    } ?: poisInArea
                }.flatMap { list ->
                    Flowable.fromIterable(list)
                }
                .distinct()
    }

    private data class LoadPoisInput(
            val latitude: Double,
            val longitude: Double,
            val poiRadius: Int
    ) {

        fun toLatLngBounds(): LatLngBounds {
            val from = LatLng(latitude, longitude)
            val distance = poiRadius.toDouble()

            val south = SphericalUtil.computeOffset(from, distance, 180.0)
            val southWest = SphericalUtil.computeOffset(south, distance, 270.0)

            val north = SphericalUtil.computeOffset(from, distance, 0.0)
            val northEast = SphericalUtil.computeOffset(north, distance, 90.0)

            return LatLngBounds(southWest, northEast)
        }

        /** @return true if the given [Poi] is in the radius of this input. */
        fun isInTheRadius(poi: Poi) =
                distanceBetween(poi.latitude, poi.longitude, latitude, longitude) <= poiRadius

    }

}
