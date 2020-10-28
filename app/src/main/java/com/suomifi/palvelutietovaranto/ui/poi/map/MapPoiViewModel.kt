package com.suomifi.palvelutietovaranto.ui.poi.map

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLngBounds
import com.suomifi.palvelutietovaranto.domain.Deselected
import com.suomifi.palvelutietovaranto.domain.interactors.ObserveSelectedPoiUseCase
import com.suomifi.palvelutietovaranto.domain.location.provider.UserLocationProvider
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import com.suomifi.palvelutietovaranto.domain.ptv.repository.PtvRepository
import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi
import com.suomifi.palvelutietovaranto.ui.common.SingleLiveEvent
import com.suomifi.palvelutietovaranto.ui.poi.common.*
import com.suomifi.palvelutietovaranto.utils.extensions.selectedLanguage
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class MapPoiViewModel(
        private val userLocationProvider: UserLocationProvider,
        private val ptvRepository: PtvRepository,
        private val sharedPreferences: SharedPreferences,
        observeSelectedPoiUseCase: ObserveSelectedPoiUseCase
) : ViewModel() {

    private var lastUserLocationDisposable: Disposable? = null
    private var userLocationDisplayed = false
    private var loadPoisDisposable: Disposable? = null
    private val displayedPois = Collections.synchronizedSet(mutableSetOf<Poi>())
    private var locationToLoad: LatLngBounds? = null
    val progressViewState = MutableLiveData<ProgressViewState>()
    val poisToDisplay = MutableLiveData<List<Poi>>()
    val userLocationLiveData = SingleLiveEvent<GeoLocation>()
    val deselectPoiLiveData = SingleLiveEvent<Unit>()
    private val observeSelectedPoiDisposable: Disposable

    init {
        observeSelectedPoiDisposable = observeSelectedPoi(observeSelectedPoiUseCase)
    }

    private fun observeSelectedPoi(observeSelectedPoiUseCase: ObserveSelectedPoiUseCase): Disposable {
        return observeSelectedPoiUseCase
                .execute(Unit)
                .filter { selection ->
                    selection is Deselected
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    deselectPoiLiveData.call()
                }
    }

    fun onDetach() {
        lastUserLocationDisposable?.dispose()
        loadPoisDisposable?.dispose()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
        observeSelectedPoiDisposable.dispose()
    }

    fun onViewReady() {
        Timber.d("onViewReady")
        loadUserLocationOrPois()
    }

    private fun loadUserLocationOrPois() {
        Timber.d("loadUserLocationOrPois")
        if (userLocationDisplayed) {
            locationToLoad?.let {
                loadPois(it)
            }
        } else {
            displayLastKnownUserLocation()
        }
    }

    private fun displayLastKnownUserLocation() {
        Timber.d("displayLastKnownUserLocation")
        lastUserLocationDisposable?.dispose()
        lastUserLocationDisposable = userLocationProvider.lastKnownUserLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userLocation ->
                    userLocationLiveData.postValue(userLocation)
                    userLocationDisplayed = true
                }, {
                    Timber.e(it, "displayLastKnownUserLocation - error")
                    progressViewState.value = ProgressViewLocationError
                })
    }

    fun onMyLocationClicked() {
        Timber.d("onMyLocationClicked")
        displayLastKnownUserLocation()
    }

    fun onMapVisibleRegionChanged(visibleRegion: LatLngBounds) {
        Timber.d("onMapVisibleRegionChanged($visibleRegion)")
        locationToLoad = visibleRegion
        loadUserLocationOrPois()
    }

    private fun loadPois(visibleRegion: LatLngBounds) {
        Timber.d("loadPois($visibleRegion)")
        loadPoisDisposable?.dispose()
        loadPoisDisposable = getPoisInArea(visibleRegion)
    }

    private fun getPoisInArea(visibleRegion: LatLngBounds) = ptvRepository.poisInArea(
            visibleRegion, sharedPreferences.selectedLanguage())
            .flatMap { pois ->
                Flowable.just(pois.minus(displayedPois))
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                Timber.d("getPoisInArea - onSubscribe")
                progressViewState.value = ProgressViewLoading
            }
            .subscribe({ pois ->
                Timber.d("getPoisInArea - onNext")
                poisToDisplay.value = pois
                displayedPois.addAll(pois)
            }, { error ->
                Timber.e(error, "getPoisInArea - onError")
                progressViewState.value = ProgressViewPoiLoadingError
            }, {
                Timber.d("getPoisInArea - onComplete")
                progressViewState.value = ProgressViewHidden
                locationToLoad = null
            })

    fun onRetryClicked() {
        Timber.d("onRetryClicked")
        loadUserLocationOrPois()
    }

    fun onZoomedOut() {
        Timber.d("onZoomedOut")
        loadPoisDisposable?.dispose()
        progressViewState.value = ProgressViewZoomedOutError
    }

}
