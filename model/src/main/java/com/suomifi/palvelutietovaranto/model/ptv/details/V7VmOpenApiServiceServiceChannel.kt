package com.suomifi.palvelutietovaranto.model.ptv.details

import com.suomifi.palvelutietovaranto.model.ptv.V4VmOpenApiServiceHour
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiContactDetails

data class V7VmOpenApiServiceServiceChannel(
        /**
         * List of connection related service hours.
         */
        val serviceHours: List<V4VmOpenApiServiceHour>?,
        /**
         * Contact details for connection.
         */
        val contactDetails: VmOpenApiContactDetails?
)
