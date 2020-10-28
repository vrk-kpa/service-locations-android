package com.suomifi.palvelutietovaranto.ui.start

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.ui.common.BaseActivity
import com.suomifi.palvelutietovaranto.ui.error.ErrorActivity
import com.suomifi.palvelutietovaranto.ui.poi.PoiActivity
import com.suomifi.palvelutietovaranto.ui.tutorial.TutorialActivity
import com.suomifi.palvelutietovaranto.utils.Constants.RequestCodes.GOOGLE_SERVICES_REQUEST_CODE
import com.suomifi.palvelutietovaranto.utils.extensions.wasTutorialSeen
import org.koin.android.ext.android.inject

class StartActivity : BaseActivity() {

    private val sharedPreferences: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkGoogleServicesAvailability()
    }

    private fun checkGoogleServicesAvailability() {
        val (googleServiceAvailable, dialogShown) = isGoogleServicesAvailable()
        if (googleServiceAvailable) {
            startNextActivity()
        } else if (!dialogShown) {
            displayError(R.string.error_google_services_unavailable)
        }
    }

    /**
     * @return Pair of booleans:
     * 1. true if Google services are available, false otherwise
     * 2. true if the error dialog has been shown, false otherwise
     */
    private fun isGoogleServicesAvailable(): Pair<Boolean, Boolean> {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode == ConnectionResult.SUCCESS) {
            return Pair(true, false)
        } else if (apiAvailability.isUserResolvableError(resultCode)) {
            apiAvailability.getErrorDialog(this, resultCode, GOOGLE_SERVICES_REQUEST_CODE).show()
            return Pair(false, true)
        }
        return Pair(false, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SERVICES_REQUEST_CODE) {
            checkGoogleServicesAvailability()
        }
    }

    private fun displayError(errorStringResId: Int) {
        ErrorActivity.start(this, errorStringResId)
        finish()
    }

    private fun startNextActivity() {
        if (sharedPreferences.wasTutorialSeen()) {
            PoiActivity.start(this)
        } else {
            TutorialActivity.start(this)
        }
        finish()
    }

}
