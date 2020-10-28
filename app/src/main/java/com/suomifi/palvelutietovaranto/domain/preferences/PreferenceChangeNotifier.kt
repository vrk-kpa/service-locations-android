package com.suomifi.palvelutietovaranto.domain.preferences

import io.reactivex.Flowable

interface PreferenceChangeNotifier {
    /** Emits the current value of the POI radius preference and new value every time it changes. */
    fun observePoiRadiusChange(): Flowable<Int>
}
