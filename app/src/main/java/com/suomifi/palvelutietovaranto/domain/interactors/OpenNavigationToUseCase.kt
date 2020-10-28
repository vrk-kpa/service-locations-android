package com.suomifi.palvelutietovaranto.domain.interactors

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.domain.interactors.base.CompletableUseCase
import com.suomifi.palvelutietovaranto.domain.location.model.GeoLocation
import com.suomifi.palvelutietovaranto.utils.Constants.Network.GOOGLE_MAPS_NAVIGATION_URL
import io.reactivex.Completable
import io.reactivex.Completable.fromCallable

class OpenNavigationToUseCase(
        private val context: Context
) : CompletableUseCase<GeoLocation>() {

    override fun execute(params: GeoLocation): Completable {
        return fromCallable {
            val uri = "$GOOGLE_MAPS_NAVIGATION_URL&destination=${params.latitude},${params.longitude}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (intent.resolveActivity(context.packageManager) == null) {
                Toast.makeText(context, R.string.error_no_app_found, Toast.LENGTH_LONG).show()
            } else {
                context.startActivity(intent)
            }
        }
    }

}
