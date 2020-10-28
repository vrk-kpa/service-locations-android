package com.suomifi.palvelutietovaranto.utils.metrics

import com.crashlytics.android.answers.CustomEvent
import com.suomifi.palvelutietovaranto.utils.Constants.Network.PTV_OPEN_API_ADDRESS
import com.suomifi.palvelutietovaranto.utils.Constants.Network.WFS_GEOSERVER_ADDRESS

class ResponseTimeEvent(
        url: String,
        responseTimeMs: Long
) : CustomEvent(getEventNameByUrl(url)) {
    init {
        putCustomAttribute("responseTimeMs", responseTimeMs)
    }

    companion object {
        private fun getEventNameByUrl(url: String) = when {
            url.contains(PTV_OPEN_API_ADDRESS) -> "ptv-response-time"
            url.contains(WFS_GEOSERVER_ADDRESS) -> "wfs-response-time"
            else -> "???"
        }
    }
}
