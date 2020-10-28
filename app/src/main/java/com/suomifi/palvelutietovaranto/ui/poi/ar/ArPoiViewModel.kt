package com.suomifi.palvelutietovaranto.ui.poi.ar

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.suomifi.palvelutietovaranto.domain.Deselected
import com.suomifi.palvelutietovaranto.domain.interactors.ObserveSelectedPoiUseCase
import com.suomifi.palvelutietovaranto.domain.interactors.ar.LoadPoisUseCase
import com.suomifi.palvelutietovaranto.domain.interactors.ar.ObservePoisChangesUseCase
import com.suomifi.palvelutietovaranto.domain.interactors.ar.ObserveUserLocationUseCase
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi
import com.suomifi.palvelutietovaranto.ui.common.SingleLiveEvent
import com.suomifi.palvelutietovaranto.ui.poi.common.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ArPoiViewModel(
        private val observeUserLocationUseCase: ObserveUserLocationUseCase,
        private val observePoisChangesUseCase: ObservePoisChangesUseCase,
        private val loadPoisUseCase: LoadPoisUseCase,
        observeSelectedPoiUseCase: ObserveSelectedPoiUseCase
) : ViewModel() {

    private var reloadPoisDisposable: Disposable? = null
    private var loadPoisDisposable: Disposable? = null
    private val displayedPois = mutableSetOf<Poi>()
    private val removablePois = mutableSetOf<Poi>()
    private var removePoisDisposable: Disposable? = null
    val userLocation: MutableLiveData<GeoLocation> = MutableLiveData()
    val poiToDisplay: MutableLiveData<Poi> = MutableLiveData()
    val poisToRemove: MutableLiveData<Set<Poi>> = MutableLiveData()
    val progressViewState: MutableLiveData<ProgressViewState> = MutableLiveData()
    private var userLocationDisposable: Disposable? = null
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

    fun onAttach() {
        listenToPoiChanges()
        observeUserLocation()
    }

    private fun observeUserLocation() {
        userLocationDisposable = observeUserLocationUseCase.execute(Unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userLocation ->
                    this.userLocation.value = userLocation
                }, { error ->
                    Timber.e(error, "observeUserLocation error")
                })
    }

    private fun listenToPoiChanges() {
        reloadPoisDisposable = observePoisChangesUseCase.execute(Unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loadPois()
                }, { error ->
                    Timber.e(error, "listenToPoiChanges - onError")
                    progressViewState.value = ProgressViewLocationError
                })
    }

    private fun loadPois() {
        Timber.d("loadPois")
        loadPoisDisposable?.dispose()
        loadPoisDisposable = loadPoisUseCase.execute(Unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    Timber.d("loadPois - onSubscribe")
                    progressViewState.value = ProgressViewLoading
                    removablePois.clear()
                    removablePois.addAll(displayedPois)
                    displayedPois.clear()
                }
                .subscribe({ poi ->
                    Timber.d("loadPois - onNext $poi")
                    poiToDisplay.value = poi
                    displayedPois.add(poi)
                    removablePois.remove(poi)
                }, {
                    Timber.e(it, "loadPois - onError")
                    progressViewState.value = ProgressViewPoiLoadingError
                }, {
                    Timber.d("loadPois - onComplete")
                    progressViewState.value = ProgressViewHidden
                    poisToRemove.value = removablePois
                })
    }

    fun onRetryClicked() {
        Timber.d("onRetryClicked")
        loadPois()
    }

    fun onDetach() {
        reloadPoisDisposable?.dispose()
        loadPoisDisposable?.dispose()
        removePoisDisposable?.dispose()
        userLocationDisposable?.dispose()
    }

    override fun onCleared() {
        super.onCleared()
        observeSelectedPoiDisposable.dispose()
    }

}
