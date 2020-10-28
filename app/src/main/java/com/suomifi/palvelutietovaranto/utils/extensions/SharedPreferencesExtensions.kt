@file:JvmName("Extensions")
@file:JvmMultifileClass

package com.suomifi.palvelutietovaranto.utils.extensions

import android.content.SharedPreferences
import com.suomifi.palvelutietovaranto.domain.localization.Language
import com.suomifi.palvelutietovaranto.utils.Constants.Preferences.PREF_KEY_ASKED_FOR_PERMISSIONS
import com.suomifi.palvelutietovaranto.utils.Constants.Preferences.PREF_KEY_SELECTED_LANGUAGE
import com.suomifi.palvelutietovaranto.utils.Constants.Preferences.PREF_KEY_SHOW_PARKING_METERS
import com.suomifi.palvelutietovaranto.utils.Constants.Preferences.PREF_KEY_TUTORIAL_SEEN

fun SharedPreferences.wasTutorialSeen() = getBoolean(PREF_KEY_TUTORIAL_SEEN, false)
fun SharedPreferences.setTutorialSeen() = edit().putBoolean(PREF_KEY_TUTORIAL_SEEN, true).apply()

/** @return true if the user was asked to grant the permissions at least once */
fun SharedPreferences.didAskForPermissions() = getBoolean(PREF_KEY_ASKED_FOR_PERMISSIONS, false)

/** sets that the user was asked to grant the permissions at least once */
fun SharedPreferences.setAskedForPermissions() = edit().putBoolean(PREF_KEY_ASKED_FOR_PERMISSIONS, true).apply()

fun SharedPreferences.selectedLanguage(): Language {
    val selectedLanguageCode = getString(PREF_KEY_SELECTED_LANGUAGE, null)
            ?: throw IllegalStateException("can't find selected language")
    return Language.getLanguageByPtvCode(selectedLanguageCode)
            ?: throw IllegalStateException("can't find language with code $selectedLanguageCode")
}

fun SharedPreferences.getShowParkingMeters() = getBoolean(PREF_KEY_SHOW_PARKING_METERS, false)
fun SharedPreferences.toggleShowParkingMeters(): Boolean {
    val newValue = !getShowParkingMeters()
    edit().putBoolean(PREF_KEY_SHOW_PARKING_METERS, newValue).apply()
    return newValue
}
