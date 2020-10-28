package com.suomifi.palvelutietovaranto.domain.ptv.model

import com.suomifi.palvelutietovaranto.domain.localization.Language
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import com.suomifi.palvelutietovaranto.utils.Constants.Language.FALLBACK_LANGUAGE

data class PoiDetails(
        val id: String,
        val names: Map<Language, String?>,
        val descriptions: Map<Language, String?>,
        val emails: Map<Language, String?>,
        val phoneNrs: Map<Language, String?>,
        val webPages: Map<Language, String?>,
        val addresses: Map<Language, String?>,
        val geoLocation: GeoLocation,
        val postalCode: String?,
        var alpha: Double? = 1.0,
        val openingHours: List<OpeningHours>?,
        val accessibilityInformation: Map<Language, List<String>?>
) {

    val latitude = geoLocation.latitude
    val longitude = geoLocation.longitude
    val latLng = geoLocation.toLatLng()

    fun toPoi(language: Language) = Poi(latitude, longitude).apply {
        name = name(language) ?: ""
        isParkingMeter = isParkingMeter()
    }

    private fun isParkingMeter() = Language.all.any { language ->
        names[language]?.contains(language.parkingMeterString, true) ?: false
    }

    fun accessibilityInformation(language: Language): String? {
        return accessibilityInformation[language]?.joinToString(separator = "\n\n")
    }

    fun name(language: Language) = names[language] ?: names[FALLBACK_LANGUAGE]
    fun address(language: Language) = addresses[language] ?: addresses[FALLBACK_LANGUAGE]
    fun email(language: Language) = emails[language] ?: emails[FALLBACK_LANGUAGE]
    fun phone(language: Language) = phoneNrs[language] ?: phoneNrs[FALLBACK_LANGUAGE]
    fun webPage(language: Language) = webPages[language] ?: webPages[FALLBACK_LANGUAGE]
    fun description(language: Language) = descriptions[language] ?: descriptions[FALLBACK_LANGUAGE]

}
