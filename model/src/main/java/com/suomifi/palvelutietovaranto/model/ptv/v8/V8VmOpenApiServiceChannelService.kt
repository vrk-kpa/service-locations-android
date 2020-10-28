package com.suomifi.palvelutietovaranto.model.ptv.v8

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiItem
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLocalizedListItem
import com.suomifi.palvelutietovaranto.model.ptv.location.V4VmOpenApiFintoItem
import com.suomifi.palvelutietovaranto.model.ptv.location.VmOpenApiExtraType

/** View Model of Service channel service V8. */
data class V8VmOpenApiServiceChannelService(
        val service: VmOpenApiItem?,
        /**
         * Service charge type. Possible values are: Chargeable, FreeOfCharge or Other.
         * In version 7 and older: Charged, Free or Other.
         */
        val serviceChargeType: String?,
        /**
         * List of localized service channel relationship descriptions.
         * (Max.Length: 500 Description). (Max.Length: 500 ChargeTypeAdditionalInfo).
         */
        val description: List<VmOpenApiLocalizedListItem>?,
        /** The extra types related to service and service channel connection. */
        val extraTypes: List<VmOpenApiExtraType>?,
        /** List of connection related service hours. */
        val serviceHours: List<V8VmOpenApiServiceHour>?,
        val contactDetails: V8VmOpenApiContactDetails?,
        /** List of digital authorizations related to the service. */
        val digitalAuthorizations: List<V4VmOpenApiFintoItem>?
)
