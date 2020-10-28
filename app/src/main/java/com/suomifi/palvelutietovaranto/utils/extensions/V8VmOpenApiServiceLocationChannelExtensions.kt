@file:JvmName("Extensions")
@file:JvmMultifileClass

package com.suomifi.palvelutietovaranto.utils.extensions

import com.suomifi.palvelutietovaranto.domain.localization.Language
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import com.suomifi.palvelutietovaranto.domain.ptv.model.DayOfTheWeek
import com.suomifi.palvelutietovaranto.domain.ptv.model.OpeningHours
import com.suomifi.palvelutietovaranto.domain.ptv.model.PoiDetails
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiAddressStreetWithCoordinates
import com.suomifi.palvelutietovaranto.model.ptv.v8.V8VmOpenApiAddressWithMoving
import com.suomifi.palvelutietovaranto.model.ptv.v8.V8VmOpenApiServiceLocationChannel
import org.osgeo.proj4j.util.CoordinateUtils

fun V8VmOpenApiServiceLocationChannel.toPoiDetails(): PoiDetails? {
    val address = addresses?.find {
        it.type == "Location"
    }
    val firstStreetAddress = address?.streetAddress
    id?.let { id ->
        return@let getGeoLocation(firstStreetAddress)?.let { geoLocation ->
            return PoiDetails(
                    id,
                    getNames(),
                    getDescriptions(),
                    getEmails(),
                    getPhoneNrs(),
                    getWebPages(),
                    getAddresses(firstStreetAddress),
                    geoLocation,
                    firstStreetAddress?.postalCode,
                    openingHours = getOpeningHours(),
                    accessibilityInformation = getAccessibilityInformation(address)
            )
        }
    }
    return null
}

private fun V8VmOpenApiServiceLocationChannel.getNames(): Map<Language, String?> {
    val languages = Language.all
    val names = HashMap<Language, String?>(languages.size)
    languages.forEach { language ->
        names[language] = serviceChannelNames?.find { item ->
            item.type == "Name" && item.language.equals(language.ptvLanguageCode, true)
        }?.value
    }
    return names
}

private fun V8VmOpenApiServiceLocationChannel.getDescriptions(): Map<Language, String?> {
    val languages = Language.all
    val descriptions = HashMap<Language, String?>(languages.size)
    languages.forEach { language ->
        descriptions[language] = serviceChannelDescriptions?.find { item ->
            item.type == "Description" && item.language.equals(language.ptvLanguageCode, true)
        }?.value
    }
    return descriptions
}

private fun V8VmOpenApiServiceLocationChannel.getEmails(): Map<Language, String?> {
    val languages = Language.all
    val emails = HashMap<Language, String?>(languages.size)
    languages.forEach { language ->
        emails[language] = this.emails?.find { email ->
            email.language.equals(language.ptvLanguageCode, true)
        }?.value
    }
    return emails
}

private fun V8VmOpenApiServiceLocationChannel.getPhoneNrs(): Map<Language, String?> {
    val languages = Language.all
    val phoneNrs = HashMap<Language, String?>(languages.size)
    languages.forEach { language ->
        phoneNrs[language] = phoneNumbers?.find {
            it.language.equals(language.ptvLanguageCode, true) && it.type == "Phone"
                    && !it.prefixNumber.isNullOrEmpty() && !it.number.isEmpty()
        }?.let {
            "${it.prefixNumber} ${it.number}"
        }
    }
    return phoneNrs
}

private fun V8VmOpenApiServiceLocationChannel.getWebPages(): Map<Language, String?> {
    val languages = Language.all
    val webPages = HashMap<Language, String?>(languages.size)
    languages.forEach { language ->
        webPages[language] = this.webPages?.find {
            it.language.equals(language.ptvLanguageCode, true)
        }?.url
    }
    return webPages
}

private fun V8VmOpenApiServiceLocationChannel.getOpeningHours() = serviceHours?.find { serviceHour ->
    serviceHour.serviceHourType == "DaysOfTheWeek"
}?.openingHour?.mapNotNull { openingTime ->
    openingTime.dayFrom?.let { dayFrom ->
        DayOfTheWeek.getByJsonName(dayFrom)
    }?.let { dayOfTheWeek ->
        OpeningHours(dayOfTheWeek, openingTime.from, openingTime.to)
    }
}

private fun getAddresses(firstStreetAddress: VmOpenApiAddressStreetWithCoordinates?): Map<Language, String?> {
    val languages = Language.all
    val addresses = HashMap<Language, String?>(languages.size)
    languages.forEach { language ->
        addresses[language] = firstStreetAddress?.street?.find {
            it.language.equals(language.ptvLanguageCode, true)
        }?.value?.let { streetName ->
            "$streetName ${firstStreetAddress.streetNumber ?: ""}"
        }
    }
    return addresses
}

private fun getGeoLocation(firstStreetAddress: VmOpenApiAddressStreetWithCoordinates?) =
        firstStreetAddress?.let { streetAddress ->
            streetAddress.latitude?.let { latitude ->
                streetAddress.longitude?.let { longitude ->
                    val (lat, lng) = CoordinateUtils.transformToWgs84(latitude.toDouble(), longitude.toDouble())
                    GeoLocation(lat, lng)
                }
            }
        }

private fun getAccessibilityInformation(address: V8VmOpenApiAddressWithMoving?): Map<Language, List<String>?> {
    val languages = Language.all
    val accessibilityInformation = HashMap<Language, List<String>?>(languages.size)
    languages.forEach { language ->
        accessibilityInformation[language] = address?.entrances?.mapNotNull { entrance ->
            entrance.accessibilitySentences
        }?.flatten()?.flatMap { sentence ->
            val items = mutableListOf<String>()
            sentence.sentenceGroup?.find { languageItem ->
                languageItem.language.equals(language.ptvLanguageCode, true)
            }?.value?.let { value ->
                items.add(value)
            }
            sentence.sentences?.mapNotNull { sentenceValue ->
                sentenceValue.sentence?.asSequence()?.find { item ->
                    item.language.equals(language.ptvLanguageCode, true)
                }?.value
            }?.let {
                items.addAll(it)
            }
            items
        }
    }
    return accessibilityInformation
}
