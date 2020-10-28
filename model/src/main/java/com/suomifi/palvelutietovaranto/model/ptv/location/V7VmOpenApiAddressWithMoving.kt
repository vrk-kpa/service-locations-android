package com.suomifi.palvelutietovaranto.model.ptv.location

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiAddressPostOfficeBox
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiAddressStreetWithCoordinates
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem

data class V7VmOpenApiAddressWithMoving(
        /**
         * Address type, Location or Postal.
         */
        val type: String?,
        /**
         * Address sub type, Single, Street, PostOfficeBox, Abroad or Multipoint.
         */
        val subType: String?,
        /**
         * Post office box address.
         */
        val postOfficeBoxAddress: VmOpenApiAddressPostOfficeBox?,
        /**
         * Street address.
         */
        val streetAddress: VmOpenApiAddressStreetWithCoordinates?,
        /**
         * Localized list of foreign address information.
         */
        val locationAbroad: List<VmOpenApiLanguageItem>?,
        /**
         * Moving address. Includes several street addresses.
         */
        val multipointLocation: List<VmOpenApiAddressStreetWithOrder>?,
        /**
         * Country code (ISO 3166-1 alpha-2), for example FI.
         */
        val country: String?
)
