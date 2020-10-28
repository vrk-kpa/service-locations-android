package com.suomifi.palvelutietovaranto

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import android.support.multidex.MultiDexApplication
import android.support.v4.os.ConfigurationCompat
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.facebook.stetho.Stetho
import com.suomifi.palvelutietovaranto.di.KoinModules
import com.suomifi.palvelutietovaranto.domain.localization.Language
import com.suomifi.palvelutietovaranto.utils.Constants.Language.DEFAULT_LANGUAGE
import com.suomifi.palvelutietovaranto.utils.Constants.Preferences.PREF_KEY_SELECTED_LANGUAGE
import com.suomifi.palvelutietovaranto.utils.CrashlyticsTree
import com.tspoon.traceur.Traceur
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class PrcArApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        setupFabric()
        setupPreferences()
        setupKoin()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        } else {
            Timber.plant(CrashlyticsTree())
        }
        Traceur.enableLogging()
    }

    private fun setupFabric() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics(), Answers())
        }
    }

    private fun setupPreferences() {
        setDefaultLanguage()
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false)
    }

    @SuppressLint("ApplySharedPref")
    private fun setDefaultLanguage() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sharedPreferences.contains(PREF_KEY_SELECTED_LANGUAGE)) {
            val defaultLocale = ConfigurationCompat.getLocales(resources.configuration)[0]
            val languageCode = defaultLocale.language.toUpperCase()
            val language = Language.getLanguageByPtvCode(languageCode) ?: DEFAULT_LANGUAGE
            Timber.d("setupPreferences - saving $language as current language")
            sharedPreferences
                    .edit()
                    .putString(PREF_KEY_SELECTED_LANGUAGE, language.ptvLanguageCode)
                    .commit()
        }
    }

    private fun setupKoin() {
        startKoin(this, KoinModules.modules)
    }

}
