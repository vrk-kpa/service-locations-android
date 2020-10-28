package com.suomifi.palvelutietovaranto.ui.poi

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import android.databinding.ObservableBoolean
import com.github.reline.GoogleMapsBottomSheetBehavior.*
import com.google.android.gms.maps.model.LatLng
import com.suomifi.palvelutietovaranto.domain.SelectedPoiSupplier
import com.suomifi.palvelutietovaranto.domain.interactors.OpenNavigationToUseCase
import com.suomifi.palvelutietovaranto.domain.localization.Language
import com.suomifi.palvelutietovaranto.domain.ptv.repository.PtvRepository
import com.suomifi.palvelutietovaranto.domain.ptv.model.PoiDetails
import com.suomifi.palvelutietovaranto.ui.common.SingleLiveEvent
import com.suomifi.palvelutietovaranto.ui.model.PoiTarget
import com.suomifi.palvelutietovaranto.ui.model.PoiTargetAction.DESELECTED
import com.suomifi.palvelutietovaranto.ui.model.PoiTargetAction.SELECTED
import com.suomifi.palvelutietovaranto.ui.poi.common.PoiDeselected
import com.suomifi.palvelutietovaranto.ui.poi.common.PoiSelected
import com.suomifi.palvelutietovaranto.ui.poi.common.PoisSelected
import com.suomifi.palvelutietovaranto.ui.poi.common.ProgressViewState
import com.suomifi.palvelutietovaranto.utils.extensions.getShowParkingMeters
import com.suomifi.palvelutietovaranto.utils.extensions.toggleShowParkingMeters
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PoiViewModel(
        private val ptvRepository: PtvRepository,
        private val sharedPreferences: SharedPreferences,
        private val openNavigationToUseCase: OpenNavigationToUseCase,
        private val selectedPoiSupplier: SelectedPoiSupplier
) : ViewModel() {

    private val poiTargetProcessor: PublishProcessor<PoiTarget> = PublishProcessor.create()
    private var parseArJsonObjectDisposable: Disposable = poiTargetProcessor
            .subscribeOn(Schedulers.io())
            .flatMapSingle { arTarget ->
                val poi = arTarget.poi
                when (arTarget.action) {
                    DESELECTED -> Single.just(PoiDeselected(poi))
                    SELECTED -> {
                        ptvRepository.poisDetailsInLocation(poi.latitude, poi.longitude)
                                .toList()
                                .map { pois ->
                                    if (pois.size == 1) {
                                        PoiSelected(poi, pois.first())
                                    } else {
                                        PoisSelected(poi, pois)
                                    }
                                }
                    }
                }
            }
            .doOnError { error ->
                Timber.e(error, "parseArJsonObject - error")
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { action ->
                Timber.d("parseArJsonObject - onNext, action: $action poi: ${action.poi.id}")
                when (action) {
                    is PoiSelected -> {
                        selectedPoiSupplier.selectPoi(action.poi)
                        displayPoi(action.poiDetails)
                        displayedPois.value = null
                    }
                    is PoiDeselected -> {
                        setBottomSheetState(STATE_HIDDEN)
                        selectedPoiSupplier.deselectPoi()
                        displayedPoi.value = null
                        displayedPois.value = null
                    }
                    is PoisSelected -> {
                        setBottomSheetState(STATE_HIDDEN)
                        selectedPoiSupplier.selectPoi(action.poi)
                        displayedPoi.value = null
                        displayedPois.value = action.poisDetails
                    }
                }
            }
    val progressViewVisibility = MutableLiveData<ProgressViewState>()
    val retryClicked = SingleLiveEvent<Unit>()
    val permissionsGranted = SingleLiveEvent<Unit>()
    val navigationBarColor = MutableLiveData<Int>()
    val settingsClicked = SingleLiveEvent<Unit>()
    val searchClicked = SingleLiveEvent<Unit>()
    val openEmailEvent = SingleLiveEvent<String>()
    val bottomSheetState = MutableLiveData<Int>().apply {
        value = STATE_HIDDEN
    }
    val closeScreenEvent = SingleLiveEvent<Unit>()
    val callPhoneEvent = SingleLiveEvent<String>()
    val openWebPageEvent = SingleLiveEvent<String>()
    val searchResult = MutableLiveData<LatLng>()
    val accessibilityInformationVisibility = MutableLiveData<Boolean>().apply {
        value = false
    }
    val showParkingMeters = MutableLiveData<Boolean>().apply {
        value = sharedPreferences.getShowParkingMeters()
    }

    var displayedPoi = object : MutableLiveData<PoiDetails>() {
        override fun setValue(value: PoiDetails?) {
            super.setValue(value)
            poisListVisibility.notifyChange()
        }
    }
    var displayedPois = object : MutableLiveData<List<PoiDetails>>() {
        override fun setValue(value: List<PoiDetails>?) {
            super.setValue(value)
            poisListVisibility.notifyChange()
        }
    }
    val poisListVisibility: ObservableBoolean = object : ObservableBoolean() {
        override fun get(): Boolean {
            if (arePoiDetailsDisplayed()) return false
            if (isPoiListDisplayed()) return true
            return false
        }
    }
    val fabMenuVisible = MutableLiveData<Boolean>().apply {
        value = true
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        Timber.d("onCleared()")
        poiTargetProcessor.onComplete()
        parseArJsonObjectDisposable.dispose()
        compositeDisposable.dispose()
    }

    fun onArTargetReceived(poiTarget: PoiTarget) {
        Timber.d("onArTargetReceived(%s)", poiTarget)
        poiTargetProcessor.onNext(poiTarget)
    }

    fun onBottomSheetMoreClicked(anchorEnabled: Boolean) {
        Timber.d("onBottomSheetMoreClicked")
        if (isBottomSheetCollapsed()) {
            if (anchorEnabled) {
                setBottomSheetState(STATE_ANCHORED)
            } else {
                setBottomSheetState(STATE_EXPANDED)
            }
        } else {
            setBottomSheetState(STATE_COLLAPSED)
        }
    }

    fun onBackPressed(anchorEnabled: Boolean) {
        Timber.d("onBackPressed")
        when (bottomSheetState.value) {
            STATE_EXPANDED -> {
                if (anchorEnabled) {
                    setBottomSheetState(STATE_ANCHORED)
                } else {
                    setBottomSheetState(STATE_COLLAPSED)
                }
            }
            STATE_ANCHORED -> {
                if (anchorEnabled) {
                    setBottomSheetState(STATE_COLLAPSED)
                } else {
                    hidePoi()
                }
            }
            STATE_COLLAPSED -> {
                hidePoi()
            }
            STATE_HIDDEN -> {
                if (isPoiListDisplayed()) {
                    deselectPoi()
                } else {
                    closeScreenEvent.call()
                }
            }
        }
    }

    /** Called when POI details view was manually closed by the user. */
    fun onPoiDetailsManuallyClosed() {
        Timber.d("onPoiDetailsManuallyClosed")
        hidePoi()
    }

    fun onPhoneClicked(selectedLanguage: Language) {
        displayedPoi.value?.let { poiDetails ->
            poiDetails.phone(selectedLanguage)?.let { phoneNr ->
                callPhoneEvent.value = phoneNr
            } ?: Timber.w("onPhoneClicked - selected POI has no phone nr")
        } ?: Timber.w("onPhoneClicked - no POI has been selected")
    }

    fun onEmailClicked(selectedLanguage: Language) {
        displayedPoi.value?.let { poiDetails ->
            poiDetails.email(selectedLanguage)?.let { email ->
                openEmailEvent.value = email
            } ?: Timber.w("onEmailClicked  - selected POI has no email")
        } ?: Timber.w("onEmailClicked - no POI has been selected")
    }

    fun onWebClicked(selectedLanguage: Language) {
        displayedPoi.value?.let { poiDetails ->
            poiDetails.webPage(selectedLanguage)?.let { url ->
                openWebPageEvent.value = url
            } ?: Timber.w("onWebClicked  - selected POI has no web page")
        } ?: Timber.w("onWebClicked - no POI has been selected")
    }

    fun onDirectionsClicked() {
        navigateToSelectedPoi()
    }

    fun onLocationClicked() {
        navigateToSelectedPoi()
    }

    private fun navigateToSelectedPoi() {
        displayedPoi.value?.let { poiDetails ->
            compositeDisposable.add(openNavigationToUseCase.execute(poiDetails.geoLocation).subscribe())
        } ?: Timber.w("navigateToSelectedPoi - no POI has been selected")
    }

    fun retryClicked() = retryClicked.call()
    fun onSettingsClicked() = settingsClicked.call()
    fun onSearchClicked() = searchClicked.call()
    private fun isBottomSheetHidden() = bottomSheetState.value == STATE_HIDDEN
    private fun isBottomSheetCollapsed() = bottomSheetState.value == STATE_COLLAPSED
    private fun setBottomSheetState(state: Int) {
        bottomSheetState.value = state
    }

    fun onPoiSelected(poiDetails: PoiDetails) {
        displayPoi(poiDetails)
    }

    private fun isPoiListDisplayed() = displayedPois.value != null

    private fun arePoiDetailsDisplayed() = displayedPoi.value != null

    private fun displayPoi(poiDetails: PoiDetails) {
        if (isBottomSheetHidden()) {
            setBottomSheetState(STATE_COLLAPSED)
        }
        displayedPoi.value = poiDetails
    }

    private fun deselectPoi() {
        selectedPoiSupplier.getSelectedPoi()?.let { poi ->
            onArTargetReceived(PoiTarget(poi, DESELECTED))
        }
    }

    private fun hidePoi() {
        setBottomSheetState(STATE_HIDDEN)
        displayedPoi.value = null
        if (!isPoiListDisplayed()) {
            deselectPoi()
        }
    }

    fun onAccessibilityInformationClicked() {
        accessibilityInformationVisibility.value = !accessibilityInformationVisibility.value!!
    }

    fun onToggleParkingMetersClicked() {
        showParkingMeters.value = sharedPreferences.toggleShowParkingMeters()
    }

}
