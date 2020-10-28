package com.suomifi.palvelutietovaranto.ui.settings

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.view.MenuItem
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.domain.localization.Language
import com.suomifi.palvelutietovaranto.ui.poi.PoiActivity
import com.suomifi.palvelutietovaranto.utils.Constants.Preferences.PREF_KEY_FEEDBACK
import com.suomifi.palvelutietovaranto.utils.Constants.Preferences.PREF_KEY_POI_RADIUS
import com.suomifi.palvelutietovaranto.utils.Constants.Preferences.PREF_KEY_SELECTED_LANGUAGE
import com.suomifi.palvelutietovaranto.utils.LanguageChanger
import com.suomifi.palvelutietovaranto.utils.extensions.safeStartActivity
import com.suomifi.palvelutietovaranto.utils.extensions.selectedLanguage
import org.koin.android.ext.android.inject
import java.util.*

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    companion object {

        private const val KEY_STATE_LANGUAGE = "KEY_STATE_LANGUAGE"

        fun start(context: Context) = context.startActivity(Intent(context, SettingsActivity::class.java))

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val index = preference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                        if (index >= 0)
                            preference.entries[index]
                        else
                            null)

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }

    private lateinit var selectedLanguage: Language

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, GeneralPreferenceFragment()).commit()
        selectedLanguage = if (savedInstanceState == null) {
            selectedLanguage()
        } else {
            val ptvLanguageCode = savedInstanceState.getString(KEY_STATE_LANGUAGE)
            Language.getLanguageByPtvCode(ptvLanguageCode)
                    ?: throw IllegalStateException("can't find language with code $ptvLanguageCode")
        }
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return GeneralPreferenceFragment::class.java.name == fragmentName
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing && languageChanged()) {
            PoiActivity.start(this)
        }
    }

    private fun languageChanged(): Boolean {
        return selectedLanguage != selectedLanguage()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_STATE_LANGUAGE, selectedLanguage.ptvLanguageCode)
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(newBase)
        super.attachBaseContext(LanguageChanger.onAttach(newBase, Locale(prefs.selectedLanguage().ptvLanguageCode)))
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {

        private val sharedPreferences: SharedPreferences by inject()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            bindPreferenceSummaryToValue(findPreference(PREF_KEY_POI_RADIUS))
            bindPreferenceSummaryToValue(findPreference(PREF_KEY_SELECTED_LANGUAGE))
            findPreference(PREF_KEY_FEEDBACK).setOnPreferenceClickListener {
                activity?.let { activity ->
                    val url = sharedPreferences.selectedLanguage().feedbackUrl
                    activity.safeStartActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    return@setOnPreferenceClickListener true
                }
                false
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == android.R.id.home) {
                activity.onBackPressed()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

}
