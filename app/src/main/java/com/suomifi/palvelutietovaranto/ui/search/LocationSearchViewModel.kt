package com.suomifi.palvelutietovaranto.ui.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.suomifi.palvelutietovaranto.domain.interactors.SearchLocationUseCase
import com.suomifi.palvelutietovaranto.ui.common.SingleLiveEvent
import com.suomifi.palvelutietovaranto.utils.extensions.selectedLanguage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class LocationSearchViewModel(
        private val searchLocationUseCase: SearchLocationUseCase,
        private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val closeLocationSearch = SingleLiveEvent<Unit>()
    val searchLocations = MutableLiveData<List<LocationSearchSuggestion>>()
    private val querySubject = PublishSubject.create<String>()
    private var searchDisposable = querySubject
            .observeOn(Schedulers.io())
            .distinctUntilChanged()
            .switchMapSingle { query ->
                searchLocationUseCase.execute(query)
            }.map { result ->
                val language = sharedPreferences.selectedLanguage()
                result.map { searchLocation ->
                    LocationSearchSuggestionMapper.map(language, searchLocation)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                searchLocations.value = result
            }, { error ->
                Timber.e(error)
            })

    fun onBackgroundClicked() {
        closeLocationSearch.call()
    }

    fun onSearchQueryChanged(query: String) {
        querySubject.onNext(query)
    }

    override fun onCleared() {
        searchDisposable?.dispose()
    }

}
