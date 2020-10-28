package com.suomifi.palvelutietovaranto.ui.search

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationSearchSuggestion(
        val name: String,
        val latitude: Double,
        val longitude: Double
) : SearchSuggestion {
    override fun getBody(): String {
        return name
    }
}
