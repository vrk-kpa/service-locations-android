package com.suomifi.palvelutietovaranto.model.ptv.location

import com.suomifi.palvelutietovaranto.model.ptv.V4VmOpenApiServiceHour
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiContactDetails
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiItem
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLocalizedListItem

data class VmOpenApiServiceChannelService(
        /**
         * Service channel identifier and name.
         */
        val service: VmOpenApiItem?,
        /**
         * Service charge type. Possible values are: Chargeable, FreeOfCharge or Other. In version 7 and older:
         * Charged, Free or Other
         */
        val serviceChargeType: String?,
        /**
         * List of localized service channel relationship descriptions.
         */
        val description: List<VmOpenApiLocalizedListItem>?,
        /**
         * The extra types related to service and service channel connection
         */
        val extraTypes: List<VmOpenApiExtraType>?,
        /**
         * List of connection related service hours.
         */
        val serviceHours: List<V4VmOpenApiServiceHour>?,
        /**
         * Contact details for connection.
         */
        val contactDetails: VmOpenApiContactDetails?,
        /**
         * List of digital authorizations related to the service.
         */
        val digitalAuthorizations: List<V4VmOpenApiFintoItem>?
)
