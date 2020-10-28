package com.suomifi.palvelutietovaranto.ui.search

import com.suomifi.palvelutietovaranto.data.db.model.search.SearchLocation
import com.suomifi.palvelutietovaranto.domain.localization.Language

object LocationSearchSuggestionMapper {

    fun map(language: Language, searchLocation: SearchLocation) = LocationSearchSuggestion(
            if (language == Language.Swedish) searchLocation.nameSv else searchLocation.nameFi,
            searchLocation.latitude,
            searchLocation.longitude
    )

    fun map(locationSearchSuggestion: LocationSearchSuggestion) = LocationSearchResult(
            locationSearchSuggestion.latitude,
            locationSearchSuggestion.longitude
    )

}
