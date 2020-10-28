package com.suomifi.palvelutietovaranto.domain.ptv.repository

import com.google.android.gms.maps.model.LatLngBounds
import com.suomifi.palvelutietovaranto.data.ptv.PtvService
import com.suomifi.palvelutietovaranto.data.wfs.WfsService
import com.suomifi.palvelutietovaranto.domain.localization.Language
import com.suomifi.palvelutietovaranto.domain.ptv.cache.PtvCache
import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi
import com.suomifi.palvelutietovaranto.domain.ptv.model.PoiDetails
import com.suomifi.palvelutietovaranto.utils.Constants.Network.POI_DETAILS_BUFFER
import com.suomifi.palvelutietovaranto.utils.Constants.Network.POI_DETAILS_REQUEST_TIMEOUT
import com.suomifi.palvelutietovaranto.utils.extensions.toBbox
import com.suomifi.palvelutietovaranto.utils.extensions.toPoiDetails
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PtvRepositoryImpl(
        private val ptvService: PtvService,
        private val ptvCache: PtvCache,
        private val wfsService: WfsService
) : PtvRepository {

    override fun poisInArea(area: LatLngBounds, language: Language): Flowable<List<Poi>> {
        return getPoisFromCache(area)
                .flatMapPublisher { cachedPois ->
                    val ids = cachedPois.map { poiDetails ->
                        poiDetails.id
                    }
                    Flowable.merge(
                            Flowable.just(cachedPois.map { poiDetails ->
                                poiDetails.toPoi(language)
                            }),
                            getPoisFromNetwork(area, language, ids)
                    ).distinct()
                }
    }

    private fun getPoisFromCache(area: LatLngBounds): Single<List<PoiDetails>> {
        return ptvCache.getAll()
                .filter { poiDetails ->
                    area.contains(poiDetails.latLng)
                }
                .distinct { poiDetails ->
                    poiDetails.id
                }
                .toList()
    }

    private fun getPoisFromNetwork(area: LatLngBounds, language: Language, idsToIgnore: List<String>): Flowable<List<Poi>> {
        return wfsService.getFeatures(area.toBbox())
                .flatMapPublisher { getFeaturesResult ->
                    getFeaturesResult.rootIds?.let { rootIds ->
                        Flowable.fromIterable(rootIds)
                    } ?: Flowable.empty<String>()
                }
                .distinct()
                .filter { id ->
                    !idsToIgnore.contains(id)
                }
                .buffer(POI_DETAILS_BUFFER)
                .flatMapMaybe { ids ->
                    poiDetails(ids, language)
                }.distinct()
    }

    private fun poiDetails(identifiers: List<String>, language: Language): Maybe<List<Poi>> {
        Timber.d("poiDetails(%s)", identifiers)
        return Single.just(identifiers)
                .flatMapMaybe { ids ->
                    val guids = ids.joinToString(separator = ",")
                    ptvService.serviceLocations(guids)
                }
                .timeout(POI_DETAILS_REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .map { serviceChannels ->
                    serviceChannels.mapNotNull { serviceChannel ->
                        serviceChannel.toPoiDetails()
                    }
                }
                .doOnSuccess { poiDetailsList ->
                    poiDetailsList.forEach { poiDetails ->
                        ptvCache.storePoi(poiDetails)
                    }
                }
                .map { poiDetailsList ->
                    poiDetailsList.map { poiDetails ->
                        poiDetails.toPoi(language)
                    }
                }
                .doOnError {
                    Timber.e("Error downloading service ids: $identifiers. Details: ${it.message}")
                }
                .onErrorResumeNext(Maybe.empty())
    }

    override fun poisDetailsInLocation(latitude: Double, longitude: Double): Flowable<PoiDetails> {
        return ptvCache.getAll()
                .filter { poiDetails ->
                    poiDetails.latitude == latitude && poiDetails.longitude == longitude
                }
    }

}
