package com.suomifi.palvelutietovaranto.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import com.suomifi.palvelutietovaranto.domain.localization.Language.English
import com.suomifi.palvelutietovaranto.domain.localization.Language.Finnish
import com.wikitude.architect.ArchitectStartupConfiguration.Features.Geo
import com.wikitude.architect.ArchitectStartupConfiguration.Features.ImageTracking
import java.util.concurrent.TimeUnit

object Constants {

    object Ar {
        /** AR features required to run the application. */
        const val AR_FEATURES = 0.or(ImageTracking).or(Geo)
        /** maximum number of characters displayed in POI title */
        const val POI_TITLE_MAX_LENGTH = 24
        /** Equals "AR.CONST.UNKNOWN_ALTITUDE" in JavaScript. */
        const val ALTITUDE_UNKNOWN = -32768
    }

    object Preferences {
        const val PREF_KEY_POI_RADIUS = "pref_poi_radius"
        const val PREF_KEY_TUTORIAL_SEEN = "pref_tutorial_seen"
        const val PREF_KEY_ASKED_FOR_PERMISSIONS = "pref_asked_for_permissions"
        const val PREF_KEY_SELECTED_LANGUAGE = "pref_selected_language"
        const val PREF_KEY_SHOW_PARKING_METERS = "pref_show_parking_meters"
        const val PREF_KEY_FEEDBACK = "pref_feedback"
    }

    object GeoLocation {
        const val DEFAULT_ACCURACY = 1000F
        const val DEFAULT_ALTITUDE = -32768.0
        /** Interval at which user location is updated (in milliseconds) */
        val LOCATION_UPDATE_INTERVAL = TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS)
        const val SMALLEST_DISPLACEMENT = 50F
        const val DEFAULT_MAP_ZOOM_LEVEL = 15.5F
        const val MINIMUM_MAP_ZOOM_LEVEL = 14.75F
    }

    object Cache {
        /** Number of elements in cache. */
        const val CACHE_SIZE = 1000
    }

    object Network {
        /** Timeout of all network requests (in seconds) */
        const val TIMEOUT = 30L
        /** Timeout of poi details request (in seconds) */
        const val POI_DETAILS_REQUEST_TIMEOUT = 7L
        const val PTV_OPEN_API_ADDRESS = "https://api.palvelutietovaranto.suomi.fi/api/v8/"
        const val WFS_GEOSERVER_ADDRESS = "https://ws.palvelutietovaranto.suomi.fi/geoserver/"
        const val POI_DETAILS_BUFFER = 10
        const val GOOGLE_MAPS_NAVIGATION_URL = "https://www.google.com/maps/dir/?api=1"
    }

    object Permissions {
        val MAP_PERMISSIONS = arrayOf(ACCESS_FINE_LOCATION)
        val AR_PERMISSIONS = arrayOf(ACCESS_FINE_LOCATION, CAMERA)
        val ALL_PERMISSIONS = AR_PERMISSIONS
        val REQUIRED_PERMISSIONS = MAP_PERMISSIONS
    }

    object RequestCodes {
        const val GOOGLE_SERVICES_REQUEST_CODE = 1
        const val PERMISSIONS_REQUEST_CODE = 2
        const val ENABLE_LOCATION_REQUEST_CODE = 3
        const val LOCATION_SEARCH_REQUEST_CODE = 4
    }

    object Language {
        val FALLBACK_LANGUAGE = Finnish
        val DEFAULT_LANGUAGE = English
    }

}
