package com.suomifi.palvelutietovaranto.domain.preferences

import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.suomifi.palvelutietovaranto.utils.Constants.Preferences.PREF_KEY_POI_RADIUS
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class PreferenceChangeNotifierImpl(
        sharedPreferences: SharedPreferences
) : PreferenceChangeNotifier {

    private val rxSharedPreferences = RxSharedPreferences.create(sharedPreferences)

    override fun observePoiRadiusChange(): Flowable<Int> {
        return rxSharedPreferences.getString(PREF_KEY_POI_RADIUS)
                .asObservable()
                .map { it.toInt() }
                .toFlowable(BackpressureStrategy.MISSING)
    }

}
