package com.suomifi.palvelutietovaranto.ui.common

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.suomifi.palvelutietovaranto.utils.LanguageChanger
import com.suomifi.palvelutietovaranto.utils.extensions.selectedLanguage
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(newBase)
        super.attachBaseContext(LanguageChanger.onAttach(newBase, Locale(prefs.selectedLanguage().ptvLanguageCode)))
    }

}
