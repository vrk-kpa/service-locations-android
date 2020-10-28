package com.suomifi.palvelutietovaranto.model.ptv.details

import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiAddressPostOfficeBox
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiAddressStreetWithCoordinates
import com.suomifi.palvelutietovaranto.model.ptv.VmOpenApiLanguageItem

/** View Model of address. */
data class V7VmOpenApiAddressContact(
        /** Address type, Postal. */
        val type: String?,
        /** Address sub type, Street, PostOfficeBox or Abroad. */
        val subType: String?,
        val postOfficeBoxAddress: VmOpenApiAddressPostOfficeBox?,
        val streetAddress: VmOpenApiAddressStreetWithCoordinates?,
        /** Localized list of foreign address information. */
        val foreignAddress: List<VmOpenApiLanguageItem>?,
        /** Country code (ISO 3166-1 alpha-2), for example FI. */
        val country: String?
)
